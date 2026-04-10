package it.polimi.ingsw.am32.model;

import it.polimi.ingsw.am32.model.cards.Card;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.model.cards.events.EventCard;
import it.polimi.ingsw.am32.model.cards.visitors.AcquisitionValidatorVisitor;
import it.polimi.ingsw.am32.model.cards.visitors.EventCollectorVisitor;
import it.polimi.ingsw.am32.model.cards.visitors.RowDistributionVisitor;
import it.polimi.ingsw.am32.model.enums.GamePhase;
import it.polimi.ingsw.am32.model.enums.InvalidActionReason;
import it.polimi.ingsw.am32.model.exceptions.InvalidActionException;
import it.polimi.ingsw.am32.model.observers.GameEvent;
import it.polimi.ingsw.am32.model.observers.GameObservable;
import it.polimi.ingsw.am32.model.observers.GameObserver;
import it.polimi.ingsw.am32.model.tiles.OfferTile;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;

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
    private final Deck<Card> tribeDeck;
    private final Map<Integer, Deck<BuildingCard>> buildingDecks;
    private int currentRound;
    private int currentEra;
    private GamePhase currentPhase;
    private Player currentPlayer;
    private final List<GameObserver> observers;

    public GameState(int gameId,
                     int targetPlayers,
                     List<OfferTile> offerTrack,
                     TurnOrderTile turnOrderTile,
                     Deck<Card> tribeDeck,
                     Map<Integer,Deck<BuildingCard>> buildingDecks) {
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

        // players are placed on the turn order tile for the first round
        this.turnOrderTile.initPlayersOrder(this.players);

        // initial food is distributed based on turn order
        for (int i = 0; i < this.players.size(); i++) {
            Player player = this.players.get(i);
            if (i == 0) {
                // two food tokens are given to the first player
                player.addFood(2);
            } else if (i == 1 || i == 2) {
                // three food tokens are given to the second and third players
                player.addFood(3);
            } else {
                // four food tokens are given to the fourth and fifth players
                player.addFood(4);
            }
        }

        int targetLower = this.targetPlayers + 1;
        int targetUpper = this.targetPlayers + 4;

        // row distribution visitor is created to categorize drawn cards
        RowDistributionVisitor distributionVisitor = new RowDistributionVisitor();

        // cards are drawn until the required number of characters is reached for the lower row
        while (distributionVisitor.getCharacterCount() < targetLower && !this.tribeDeck.isEmpty()) {
            Card drawn = this.tribeDeck.draw();
            drawn.accept(distributionVisitor);
        }

        // categorized cards are assigned to the respective rows
        this.lowerTribeRow.addAll(distributionVisitor.getCharacters());
        this.upperTribeRow.addAll(distributionVisitor.getEvents());

        // remaining upper tribe row is filled
        while (this.upperTribeRow.size() < targetUpper && !this.tribeDeck.isEmpty()) {
            this.upperTribeRow.add(this.tribeDeck.draw());
        }

        // era 1 buildings are added to the upper building row
        Deck<BuildingCard> era1Buildings = this.buildingDecks.get(1);
        if (era1Buildings != null) {
            while (!era1Buildings.isEmpty()) {
                this.upperBuildingRow.add(era1Buildings.draw());
            }
        }

        // initial game state is set
        this.currentRound = 1;
        this.currentEra = 1;
        this.currentPhase = GamePhase.PLACE_TOTEMS;

        // current player is set from turn order tile
        this.currentPlayer = this.turnOrderTile.getTurnOrder().getFirst();
    }



    // core gameplay phases

    public void placeTotem(Player player, String selectedOfferTileId) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(selectedOfferTileId, "selectedOfferTileId cannot be null");

        if (!player.equals(this.currentPlayer)) {
            throw new IllegalStateException("cannot place totem: it is not player " + player.getNickname() + "'s turn");
        }

        if (this.currentPhase != GamePhase.PLACE_TOTEMS) {
            throw new IllegalStateException("cannot place totem: current phase is " + this.currentPhase);
        }

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

        if (!player.equals(this.currentPlayer)) {
            throw new IllegalStateException("cannot place totem: it is not player " + player.getNickname() + "'s turn");
        }

        if (this.currentPhase != GamePhase.RESOLVE_ACTIONS && this.currentPhase != GamePhase.EXTRA_ACQUISITION) {
            throw new IllegalStateException("cannot resolve action: current phase is " + this.currentPhase);
        }

        OfferTile targetTile = null;

        // player's occupied tile is identified
        for (OfferTile tile : this.offerTrack) {
            if (player.equals(tile.getOccupyingPlayer())) {
                targetTile = tile;
                break;
            }
        }

        // target tile existence is verified
        if (targetTile == null) {
            throw new InvalidActionException(
                    InvalidActionReason.PLAYER_NOT_ON_TILE,
                    "player is not occupying any offer tile"
            );
        }

        // list is strictly typed to charactercard to avoid casts later
        List<CharacterCard> foundCharacters = new ArrayList<>();
        List<BuildingCard> foundBuildings = new ArrayList<>();
        int requestedUpper = 0;
        int requestedLower = 0;

        // a single visitor is instantiated by passing the list as a reference
        AcquisitionValidatorVisitor visitor = new AcquisitionValidatorVisitor(foundCharacters);

        // character cards are searched in the upper and lower tribe rows
        for (String id : chosenCharacterCardsIds) {
            boolean found = false;

            // upper row is searched
            for (Card c : this.upperTribeRow) {
                if (c.getId().equals(id)) {
                    // double dispatch is utilized to safely populate the typed collection or reject invalid cards
                    c.accept(visitor);
                    requestedUpper++;
                    found = true;
                    break;
                }
            }
            if (found) continue;

            // lower row is searched
            for (Card c : this.lowerTribeRow) {
                if (c.getId().equals(id)) {
                    // double dispatch is utilized to safely populate the typed collection or reject invalid cards
                    c.accept(visitor);
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

        if (!targetTile.isValidSelection(requestedUpper, requestedLower, totalUpperSize, totalLowerSize)) {
            throw new InvalidActionException(
                    InvalidActionReason.INVALID_SELECTION_AMOUNT,
                    "selected cards do not match offer tile constraints"
            );
        }

        // player validates and pays for the selected buildings internally
        player.payForBuildings(foundBuildings);

        // transaction is executed: cards are removed from the board and dispatched to the player
        for (CharacterCard c : foundCharacters) {
            if (!this.upperTribeRow.remove(c)) {
                this.lowerTribeRow.remove(c);
            }
            // card acquisition and its related state updates are delegated to the player entity
            player.acquireCharacter(c);
        }

        for (BuildingCard b : foundBuildings) {
            if (!this.upperBuildingRow.remove(b)) {
                this.lowerBuildingRow.remove(b);
            }
            // card acquisition and its related state updates are delegated to the player entity
            player.acquireBuilding(b, this);
        }

        // player is moved to the turn order tile and end-of-turn effects are evaluated
        targetTile.removePlayer();
        this.turnOrderTile.placePlayer(player);

        // next player is determined by checking the offer track from left to right
        Player nextPlayer = null;
        for (OfferTile tile : this.offerTrack) {
            if (!tile.isFree()) {
                nextPlayer = tile.getOccupyingPlayer();
                break;
            }
        }

        if (nextPlayer != null) {
            // the turn is passed to the next player on the offer track
            this.currentPlayer = nextPlayer;
            return;
        }

        // resolve_actions phase is complete
        // building effects for the end of the action phase are triggered
        for (Player p : this.players) {
            p.triggerBuildingEffectsOnRoundEnd(this);
        }

        // if no building effect altered the phase to extra_acquisition, proceed to resolve_events
        if (this.currentPhase != GamePhase.EXTRA_ACQUISITION) {
            this.currentPhase = GamePhase.RESOLVE_EVENTS;
            // current player is set to null
            this.currentPlayer = null;
        }
    }

    public void resolveEvents() {
        if (this.currentPhase != GamePhase.RESOLVE_EVENTS) {
            throw new IllegalStateException("cannot resolve events: current phase is " + this.currentPhase);
        }

        EventCollectorVisitor visitor = new EventCollectorVisitor();

        // cards are processed using double dispatch to extract events
        for (Card card : this.lowerTribeRow) {
            card.accept(visitor);
        }

        List<EventCard> eventsToResolve = visitor.getCollectedEvents();

        // events are sorted naturally
        java.util.Collections.sort(eventsToResolve);

        // events are resolved sequentially
        for (EventCard event : eventsToResolve) {
            event.resolveEvent(this);
        }

        // game phase is updated to end round
        this.currentPhase = GamePhase.END_ROUND;
    }

    public void endRound() {
        if (this.currentPhase != GamePhase.END_ROUND) {
            throw new IllegalStateException("cannot end round: current phase is " + this.currentPhase);
        }

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
        Deck<BuildingCard> newEraBuildings = this.buildingDecks.get(era);
        if (newEraBuildings != null) {
            while (!newEraBuildings.isEmpty()) {
                this.upperBuildingRow.add(newEraBuildings.draw());
            }
        }
    }



    // endgame evaluation

    public GameResult calculateResults() {
        Map<Player, int[]> finalScores = new HashMap<>();

        // final prestige logic is encapsulated and resolved within the player
        for (Player player : this.players) {
            player.computeFinalPrestige(this);
        }

        // highest ranking player is determined implicitly through the comparable interface
        Player topPlayer = Collections.max(this.players);

        // winners list is populated with players matching the top score (handling ties naturally)
        List<Player> winners = new ArrayList<>();
        for (Player player : this.players) {
            if (player.compareTo(topPlayer) == 0) {
                winners.add(player);
            }

            // final score and food amount are read and saved in an array (index 0: prestige, index 1: food)
            int[] scoreData = new int[]{player.getPrestige(), player.getFood()};
            finalScores.put(player, scoreData);
        }

        // game result dto is created and returned
        return new GameResult(winners, finalScores);
    }



    // observer pattern implementation

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
        for (GameObserver observer : this.observers) {
            observer.update(event);
        }
    }



    // player management

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



    // state observation (getters and setters)

    public int getGameId() {
        return this.gameId;
    }

    public int getTargetPlayers() {
        return this.targetPlayers;
    }

    public List<Player> getPlayers() {
        return List.copyOf(this.players);
    }

    public List<OfferTile> getOfferTrack() {
        return List.copyOf(this.offerTrack);
    }

    public List<Player> getTurnOrder() {
        return List.copyOf(this.turnOrderTile.getTurnOrder());
    }

    public List<Card> getUpperTribeRow() {
        return List.copyOf(this.upperTribeRow);
    }

    public List<Card> getLowerTribeRow() {
        return List.copyOf(this.lowerTribeRow);
    }

    public List<BuildingCard> getUpperBuildingRow() {
        return List.copyOf(this.upperBuildingRow);
    }

    public List<BuildingCard> getLowerBuildingRow() {
        return List.copyOf(this.lowerBuildingRow);
    }

    public int getTribeDeckSize() {
        return this.tribeDeck.getSize();
    }

    public int getTribeDeckFirstCardEra() {
        return this.tribeDeck.getFirstCardEra();
    }

    public Map<Integer, Deck<BuildingCard>> getBuildingDecks() {
        return Map.copyOf(this.buildingDecks);
    }

    public int getCurrentRound() {
        return this.currentRound;
    }

    public void incrementCurrentRound() {
        this.currentRound++;
    }

    public GamePhase getCurrentPhase() {
        return this.currentPhase;
    }

    public void setCurrentPhase(GamePhase currentPhase) {
        Objects.requireNonNull(currentPhase, "currentPhase cannot be null");

        this.currentPhase = currentPhase;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}
