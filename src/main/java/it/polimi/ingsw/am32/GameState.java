package it.polimi.ingsw.am32;

import it.polimi.ingsw.am32.cards.Card;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.enums.GamePhase;
import it.polimi.ingsw.am32.enums.InvalidActionReason;
import it.polimi.ingsw.am32.exceptions.InvalidActionException;
import it.polimi.ingsw.am32.observers.GameEvent;
import it.polimi.ingsw.am32.observers.GameObservable;
import it.polimi.ingsw.am32.observers.GameObserver;
import it.polimi.ingsw.am32.tiles.Action;
import it.polimi.ingsw.am32.tiles.OfferTile;
import it.polimi.ingsw.am32.tiles.TurnOrderTile;

import java.util.*;

public class GameState implements GameObservable {
    private final int gameId;
    private final int targetPlayers;
    private final List<Player> players;
    private final List<OfferTile> offerTrack;
    private final TurnOrderTile turnOrderTile;
    private final List<Card> upperTribeRow;
    private final List<Card> lowerTribeRow;
    private final List<BuildingCard> upperBuildingRow;
    private final List<BuildingCard> lowerBuildingRow;
    private final Deck tribeDeck;
    private final Map<Integer, Deck> buildingDecks;
    private int currentRound;
    private int currentEra;
    private GamePhase currentPhase;
    private Player currentPlayer;
    private final List<GameObserver> observers;

    public GameState(int gameId,
                     int targetPlayers,
                     List<OfferTile> offerTrack,
                     TurnOrderTile turnOrderTile,
                     Deck tribeDeck,
                     Map<Integer,Deck> buildingDecks) {
        if (gameId < 1) {
            throw new IllegalArgumentException("gameId must be greater than 0");
        }
        if (targetPlayers < 2 || targetPlayers > 5) {
            throw new IllegalArgumentException("targetPlayers must be between 2 and 5");
        }
        Objects.requireNonNull(offerTrack, "offerTrack cannot be null");
        Objects.requireNonNull(turnOrderTile, "turnOrderTile cannot be null");
        Objects.requireNonNull(tribeDeck, "tribeDeck cannot be null");
        Objects.requireNonNull(buildingDecks, "buildingDecks cannot be null");

        this.gameId = gameId;
        this.targetPlayers = targetPlayers;
        this.players = new ArrayList<>();
        this.offerTrack = offerTrack;
        this.turnOrderTile = turnOrderTile;
        this.upperTribeRow = new ArrayList<>();
        this.lowerTribeRow = new ArrayList<>();
        this.upperBuildingRow = new ArrayList<>();
        this.lowerBuildingRow = new ArrayList<>();
        this.tribeDeck = tribeDeck;
        this.buildingDecks = buildingDecks;
        this.observers = new ArrayList<>();
        this.currentEra = 0;
        this.currentRound = 0;
    }

    public void initGame() {
        if (this.players.size() != this.targetPlayers) {
            throw new IllegalStateException("cannot start game: missing players");
        }

        // players are shuffled to determine initial turn order
        Collections.shuffle(this.players);

        // initial food is distributed based on turn order
        for (int i = 0; i < this.players.size(); i++) {
            Player player = this.players.get(i);
            if (i == 0) {
                player.addFood(2);
            } else if (i == 1 || i == 2) {
                player.addFood(3);
            } else {
                player.addFood(4);
            }
        }

        int targetLower = this.targetPlayers + 1;
        int targetUpper = this.targetPlayers + 4;

        // lower tribe row is filled and events are moved to upper tribe row
        while (this.lowerTribeRow.size() < targetLower && !this.tribeDeck.isEmpty()) {
            Card drawn = this.tribeDeck.draw();

            if (drawn.isEvent()) {
                this.upperTribeRow.add(drawn);
            } else {
                this.lowerTribeRow.add(drawn);
            }
        }

        // remaining upper tribe row is filled
        while (this.upperTribeRow.size() < targetUpper && !this.tribeDeck.isEmpty()) {
            this.upperTribeRow.add(this.tribeDeck.draw());
        }

        // era 1 buildings are added to upper building row
        Deck era1Buildings = this.buildingDecks.get(1);
        if (era1Buildings != null) {
            while (!era1Buildings.isEmpty()) {
                // cast is safe because building decks only contain building cards
                this.upperBuildingRow.add((BuildingCard) era1Buildings.draw());
            }
        }

        // initial state is set
        this.currentRound = 1;
        this.currentEra = 1;
        this.currentPhase = GamePhase.PLACE_TOTEMS;
        this.currentPlayer = this.players.getFirst();
    }

    public void saveSnapshot() {
        // game state is serialized and saved to disk
    }

    public Map<Player, int[]> calculateFinalScores() {
        Map<Player, int[]> finalScores = new HashMap<>();

        for (Player player : this.players) {
            // endgame effects from buildings are triggered
            for (BuildingCard building : player.getBuildingList()) {
                building.getEffect().onGameEnd(player, this);
            }

            // prestige from builders is calculated
            int builderPoints = player.getBuildersTotalPrestige();

            // prestige from artists is calculated (10 pp for every 2 artists)
            int artistPoints = (player.getArtistCount() / 2) * 10;

            // prestige from inventors is calculated (inventors count * different inventions)
            int inventorPoints = player.getInventorCount() * player.getInventionCount();

            // total prestige is updated with character bonuses
            int totalBonus = builderPoints + artistPoints + inventorPoints;
            if (totalBonus > 0) {
                player.addPrestige(totalBonus);
            }

            // final score and food amount are saved in an array (index 0: prestige, index 1: food)
            int[] scoreData = new int[]{player.getPrestige(), player.getFood()};
            finalScores.put(player, scoreData);
        }

        return finalScores;
    }

    public void placeTotem(Player player, String selectedOfferTileId) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(selectedOfferTileId, "selectedOfferTileId cannot be null");

        OfferTile targetTile = null;
        for (OfferTile tile : offerTrack) {
            if (tile.getId().equals(selectedOfferTileId)) {
                targetTile = tile;
                break;
            }
        }

        // tile existence is verified
        if (targetTile == null) {
            throw new InvalidActionException(
                    InvalidActionReason.OFFER_TILE_NOT_FOUND,
                    "offer tile " + selectedOfferTileId + " does not exist"
            );
        }

        targetTile.placePlayer(player);

        // totem placement effects are triggered for the player
         for (BuildingCard building : player.getBuildingList()) {
             building.getEffect().onTotemPlaced(targetTile, player, this);
         }

        // completion of the placement phase is evaluated
        int occupiedCount = 0;
        for (OfferTile tile : offerTrack) {
            if (!tile.isFree()) {
                occupiedCount++;
            }
        }

        if (occupiedCount == players.size()) {
            this.currentPhase = GamePhase.RESOLVE_ACTIONS;
        } else {
            // current player is updated to the next one on the turn order tile
            List<Player> turnOrder = turnOrderTile.getTurnOrder();
            int currentIndex = turnOrder.indexOf(player);
            if (currentIndex >= 0 && currentIndex < turnOrder.size() - 1) {
                this.currentPlayer = turnOrder.get(currentIndex + 1);
            }
        }
    }

    public void resolveAction(Player player, List<String> chosenCharacterCardsIds, List<String> chosenBuildingCardsIds) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(chosenCharacterCardsIds, "chosenCharacterCardsIds cannot be null");
        Objects.requireNonNull(chosenBuildingCardsIds, "chosenBuildingCardsIds cannot be null");

        OfferTile targetTile = null;

        // player's occupied tile is identified
        for (OfferTile tile : this.offerTrack) {
            if (player.equals(tile.getOccupyingPlayer())) {
                targetTile = tile;
                break;
            }
        }

        if (targetTile == null) {
            throw new InvalidActionException(
                    InvalidActionReason.PLAYER_NOT_ON_TILE,
                    "player is not occupying any offer tile"
            );
        }

        List<Card> foundCharacters = new ArrayList<>();
        List<BuildingCard> foundBuildings = new ArrayList<>();
        int requestedUpper = 0;
        int requestedLower = 0;

        // character cards are searched in the upper and lower tribe rows
        for (String id : chosenCharacterCardsIds) {
            boolean found = false;
            for (Card c : this.upperTribeRow) {
                if (c.getId().equals(id)) {
                    // events cannot be acquired by players
                    if (c.isEvent()) {
                        throw new InvalidActionException(
                                InvalidActionReason.EVENT_CARD_NOT_ALLOWED,
                                "event cards cannot be selected"
                        );
                    }
                    foundCharacters.add(c);
                    requestedUpper++;
                    found = true;
                    break;
                }
            }
            if (found) continue;

            for (Card c : this.lowerTribeRow) {
                if (c.getId().equals(id)) {
                    // events cannot be acquired by players
                    if (c.isEvent()) {
                        throw new InvalidActionException(
                                InvalidActionReason.EVENT_CARD_NOT_ALLOWED,
                                "event cards cannot be selected"
                        );
                    }
                    foundCharacters.add(c);
                    requestedLower++;
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new InvalidActionException(
                        InvalidActionReason.CARD_NOT_FOUND,
                        "character card " + id + " is not available on the board"
                );
            }
        }

        // building cards are searched in the upper and lower building rows
        for (String id : chosenBuildingCardsIds) {
            boolean found = false;
            for (BuildingCard b : this.upperBuildingRow) {
                if (b.getId().equals(id)) {
                    foundBuildings.add(b);
                    requestedUpper++;
                    found = true;
                    break;
                }
            }
            if (found) continue;

            for (BuildingCard b : this.lowerBuildingRow) {
                if (b.getId().equals(id)) {
                    foundBuildings.add(b);
                    requestedLower++;
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new InvalidActionException(
                        InvalidActionReason.CARD_NOT_FOUND,
                        "building card " + id + " is not available on the board"
                );
            }
        }

        // action validity is checked against tile constraints
        int totalUpperSize = this.upperTribeRow.size() + this.upperBuildingRow.size();
        int totalLowerSize = this.lowerTribeRow.size() + this.lowerBuildingRow.size();
        Action tileAction = targetTile.getAction();

        if (!tileAction.isValidSelection(requestedUpper, requestedLower, totalUpperSize, totalLowerSize)) {
            throw new InvalidActionException(
                    InvalidActionReason.INVALID_SELECTION_AMOUNT,
                    "selected cards do not match offer tile constraints"
            );
        }

        // total cost is calculated taking into account player's discounts
        int totalCost = 0;
        for (BuildingCard building : foundBuildings) {
            int discountedCost = Math.max(0, building.getCost() - player.getBuildingDiscount());
            totalCost += discountedCost;
        }

        // food availability is checked
        if (!player.hasEnoughFood(totalCost)) {
            throw new InvalidActionException(
                    InvalidActionReason.NOT_ENOUGH_FOOD,
                    "player does not have enough food to buy the selected buildings"
            );
        }

        // transaction is executed: cards are removed from board
        for (Card c : foundCharacters) {
            if (!this.upperTribeRow.remove(c)) {
                this.lowerTribeRow.remove(c);
            }
            // cast is safe because events are already filtered out
            player.addCharacter((CharacterCard) c);
        }

        for (BuildingCard b : foundBuildings) {
            if (!this.upperBuildingRow.remove(b)) {
                this.lowerBuildingRow.remove(b);
            }
            player.addBuilding(b);
        }

        // food is paid
        if (totalCost > 0) {
            player.removeFood(totalCost);
        }

        // player is moved to turn order tile and end-of-turn effects are applied
        targetTile.removePlayer();
        this.turnOrderTile.placePlayer(player);
    }

    public void endRound() {
        this.currentRound++;

        // character and event cards in the lower tribe row are discarded
        this.lowerTribeRow.clear();

        // character and event cards from the upper tribe row are moved to the lower tribe row
        this.lowerTribeRow.addAll(this.upperTribeRow);
        this.upperTribeRow.clear();

        int targetUpper = this.targetPlayers + 4;

        // upper tribe row is refilled
        while (this.upperTribeRow.size() < targetUpper && !this.tribeDeck.isEmpty()) {

            // next card era is checked to trigger era transition
            int nextCardEra = this.tribeDeck.getFirstCardEra();
            if (nextCardEra > this.currentEra) {
                this.currentEra = nextCardEra;
                startNextEra(this.currentEra);
            }

            this.upperTribeRow.add(this.tribeDeck.draw());
        }

        // next phase is set
        this.currentPhase = GamePhase.PLACE_TOTEMS;

        // current player is set to the first player on the turn order tile
        List<Player> turnOrder = this.turnOrderTile.getTurnOrder();
        if (!turnOrder.isEmpty()) {
            this.currentPlayer = turnOrder.getFirst();
        }
    }

    private void startNextEra(int era) {
        if (era < 2 || era > 3) {
            throw new IllegalArgumentException("invalid era transition");
        }

        if (era == 3) {
            // building cards in the lower building row are discarded
            this.lowerBuildingRow.clear();
        }

        // building cards from the upper building row are moved to the lower building row
        this.lowerBuildingRow.addAll(this.upperBuildingRow);
        this.upperBuildingRow.clear();

        // new era buildings are added to the upper building row
        Deck newEraBuildings = this.buildingDecks.get(era);
        if (newEraBuildings != null) {
            while (!newEraBuildings.isEmpty()) {
                // cast is safe because building decks only contain building cards
                this.upperBuildingRow.add((BuildingCard) newEraBuildings.draw());
            }
        }
    }

    @Override
    public void addObserver(GameObserver observer) {
        Objects.requireNonNull(observer, "observer cannot be null");

        this.observers.add(observer);
    }

    @Override
    public void removeObserver(GameObserver observer) {
        Objects.requireNonNull(observer, "observer cannot be null");

        this.observers.remove(observer);
    }

    @Override
    public void notifyObservers(GameEvent event) {
        Objects.requireNonNull(event, "event cannot be null");

        // observers are notified with the latest game event
        for (GameObserver observer : observers) {
            observer.update(event);
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        this.players.add(player);
    }

    public void removePlayer(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        if (!this.players.contains(player)) {
            throw new IllegalStateException("player not found in game state");
        }
        this.players.remove(player);
    }

    public int getGameId() {
        return gameId;
    }

    public int getTargetPlayers() {
        return targetPlayers;
    }

    public List<OfferTile> getOfferTrack() {
        return offerTrack;
    }

    public TurnOrderTile getTurnOrderTile() {
        return turnOrderTile;
    }

    public List<Card> getUpperTribeRow() {
        return upperTribeRow;
    }

    public List<Card> getLowerTribeRow() {
        return lowerTribeRow;
    }

    public List<BuildingCard> getUpperBuildingRow() {
        return upperBuildingRow;
    }

    public List<BuildingCard> getLowerBuildingRow() {
        return lowerBuildingRow;
    }

    public Deck getTribeDeck() {
        return tribeDeck;
    }

    public Map<Integer, Deck> getBuildingDecks() {
        return buildingDecks;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void incrementCurrentRound() {
        this.currentRound++;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(GamePhase currentPhase) {
        Objects.requireNonNull(currentPhase, "currentPhase cannot be null");

        this.currentPhase = currentPhase;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}
