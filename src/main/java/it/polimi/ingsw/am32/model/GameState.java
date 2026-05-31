package it.polimi.ingsw.am32.model;

import it.polimi.ingsw.am32.model.cards.Card;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.model.cards.events.EventCard;
import it.polimi.ingsw.am32.model.cards.visitors.AcquisitionValidatorVisitor;
import it.polimi.ingsw.am32.model.cards.visitors.EventCollectorVisitor;
import it.polimi.ingsw.am32.model.cards.visitors.RowDistributionVisitor;
import it.polimi.ingsw.am32.model.enums.GamePhase;
import it.polimi.ingsw.am32.model.enums.GameErrorReason;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import it.polimi.ingsw.am32.model.exceptions.InvalidGameActionException;
import it.polimi.ingsw.am32.model.receipts.*;
import it.polimi.ingsw.am32.model.tiles.OfferTile;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;

import java.util.*;

/**
 * The main state of the game is represented.
 */
public class GameState {
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

    /**
     * The game state is initialized with the provided parameters.
     *
     * @param gameId the identifier of the game
     * @param targetPlayers the expected number of players
     * @param offerTrack the list of offer tiles
     * @param turnOrderTile the turn order tile
     * @param tribeDeck the deck of tribe cards
     * @param buildingDecks the map of building decks per era
     */
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
        this.currentEra = 0;
        this.currentRound = 0;
    }

    /**
     * The game board is initialized and initial resources are distributed.
     */
    public void initGame() {
        if (this.players.size() != this.targetPlayers) {
            throw new IllegalStateException("cannot start game: missing players");
        }

        // players are shuffled to determine initial turn order
        Collections.shuffle(this.players);

        // totem colors are assigned uniquely to each player
        TotemColor[] availableColors = TotemColor.values();
        for (int i = 0; i < this.players.size(); i++) {
            this.players.get(i).setTotem(availableColors[i]);
        }

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

        // first era buildings are added to the upper building row
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

    /**
     * The specified player is validated against the current turn.
     *
     * @param playerNickname the nickname of the player to validate
     * @throws InvalidGameActionException if it is not the player's turn
     */
    private void validateTurn(String playerNickname) {
        if (this.currentPlayer == null || !this.currentPlayer.getNickname().equals(playerNickname)) {
            // exception is thrown if it is not the player's turn
            throw new InvalidGameActionException(
                    GameErrorReason.NOT_YOUR_TURN,
                    "it is not player " + playerNickname + "'s turn"
            );
        }
    }

    /**
     * The current phase is validated against the expected phases.
     *
     * @param expectedPhases the allowed phases for the action
     * @throws InvalidGameActionException if the current phase is not allowed
     */
    private void validatePhase(GamePhase[] expectedPhases) {
        for (GamePhase phase : expectedPhases) {
            if (this.currentPhase == phase) {
                // execution is returned if phase matches
                return;
            }
        }

        // exception is thrown if phase is not matching any expected phase
        throw new InvalidGameActionException(
                GameErrorReason.INVALID_PHASE,
                "action not allowed in current phase: " + this.currentPhase
        );
    }

    // core gameplay phases

    /**
     * The player's totem is placed on the selected offer tile.
     *
     * @param playerNickname the nickname of the player
     * @param selectedOfferTileId the id of the selected offer tile
     */
    public void placeTotem(String playerNickname, String selectedOfferTileId) {
        Objects.requireNonNull(playerNickname, "playerNickname cannot be null");
        Objects.requireNonNull(selectedOfferTileId, "selectedOfferTileId cannot be null");

        // player is validated
        validateTurn(playerNickname);
        // phase is validated
        validatePhase(new GamePhase[]{GamePhase.PLACE_TOTEMS});

        Player player = getPlayerByNickname(playerNickname);

        OfferTile targetTile = null;
        for (OfferTile tile : offerTrack) {
            if (tile.getId().equals(selectedOfferTileId)) {
                targetTile = tile;
                break;
            }
        }

        // tile existence is verified
        if (targetTile == null) {
            throw new InvalidGameActionException(
                    GameErrorReason.OFFER_TILE_NOT_FOUND,
                    "offer tile " + selectedOfferTileId + " does not exist"
            );
        }

        Player nextPlayer = null;
        List<Player> turnOrder = turnOrderTile.getTurnOrder();
        int currentIndex = turnOrder.indexOf(player);
        if (currentIndex >= 0 && currentIndex < turnOrder.size() - 1) {
            nextPlayer = turnOrder.get(currentIndex + 1);
        }

        targetTile.placePlayer(player);
        this.turnOrderTile.removePlayer(player);

        // completion of the placement phase is evaluated
        int occupiedCount = 0;
        for (OfferTile tile : offerTrack) {
            if (!tile.isFree()) {
                occupiedCount++;
            }
        }

        if (occupiedCount == players.size()) {
            this.currentPhase = GamePhase.RESOLVE_ACTIONS;

            for (OfferTile tile : this.offerTrack) {
                if (!tile.isFree()) {
                    this.currentPlayer = tile.getOccupyingPlayer();
                    break;
                }
            }
        } else {
            this.currentPlayer = nextPlayer;
        }
    }

    /**
     * The standard action phase is resolved for a player, processing card acquisitions,
     * costs, passive effects, and totem repositioning.
     *
     * @param playerNickname the nickname of the player performing the action
     * @param chosenCharacterCardsIds the list of character card ids chosen by the player
     * @param chosenBuildingCardsIds the list of building card ids chosen by the player
     * @return an action receipt containing the resulting statistics change and turn order position
     * @throws InvalidGameActionException if the action is invalid or the player cannot afford it
     */
    public ActionReceipt resolveAction(String playerNickname, List<String> chosenCharacterCardsIds, List<String> chosenBuildingCardsIds) {
        Objects.requireNonNull(playerNickname, "playerNickname cannot be null");
        Objects.requireNonNull(chosenCharacterCardsIds, "chosenCharacterCardsIds cannot be null");
        Objects.requireNonNull(chosenBuildingCardsIds, "chosenBuildingCardsIds cannot be null");

        if (new HashSet<>(chosenCharacterCardsIds).size() != chosenCharacterCardsIds.size() ||
            new HashSet<>(chosenBuildingCardsIds).size() != chosenBuildingCardsIds.size()) {
            throw new InvalidGameActionException(
                    GameErrorReason.INVALID_SELECTION_AMOUNT,
                    "duplicate cards in request are not allowed"
            );
        }

        // player is validated
        validateTurn(playerNickname);
        // phase is validated
        validatePhase(new GamePhase[]{GamePhase.RESOLVE_ACTIONS});

        Player player = getPlayerByNickname(playerNickname);

        OfferTile targetTile = null;

        // player's occupied tile is identified
        for (OfferTile tile : this.offerTrack) {
            if (tile.isOccupiedBy(player)) {
                targetTile = tile;
                break;
            }
        }

        // target tile existence is verified
        if (targetTile == null) {
            throw new InvalidGameActionException(
                    GameErrorReason.PLAYER_NOT_ON_TILE,
                    "player is not occupying any offer tile"
            );
        }

        // list is strictly typed to character card to avoid casts later
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
                    // double dispatch is used to safely populate the typed collection or reject invalid cards
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
                    // double dispatch is used to safely populate the typed collection or reject invalid cards
                    c.accept(visitor);
                    requestedLower++;
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new InvalidGameActionException(
                        GameErrorReason.CARD_NOT_FOUND,
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
                throw new InvalidGameActionException(
                        GameErrorReason.CARD_NOT_FOUND,
                        "building card " + id + " is not available on the board"
                );
            }
        }

        // character cards present in each tribe row are counted
        int characterCardsUpper = Math.toIntExact(this.upperTribeRow.stream()
                .filter(c -> c.getId().startsWith("character_"))
                .count());

        int characterCardsLower = Math.toIntExact(this.lowerTribeRow.stream()
                .filter(c -> c.getId().startsWith("character_"))
                .count());

        // affordable buildings in each row are counted
        int affordableBuildingsUpper = Math.toIntExact(this.upperBuildingRow.stream()
                .filter(b -> !chosenBuildingCardsIds.contains(b.getId()))
                .filter(b -> player.canAffordAdditionalBuilding(foundBuildings, b))
                .count());

        int affordableBuildingsLower = Math.toIntExact(this.lowerBuildingRow.stream()
                .filter(b -> !chosenBuildingCardsIds.contains(b.getId()))
                .filter(b -> player.canAffordAdditionalBuilding(foundBuildings, b))
                .count());

        // selection validation is delegated to action
        if (!targetTile.isValidSelection(requestedUpper, requestedLower,
                characterCardsUpper, characterCardsLower,
                affordableBuildingsUpper, affordableBuildingsLower)) {
            throw new InvalidGameActionException(
                    GameErrorReason.INVALID_SELECTION_AMOUNT,
                    "selected cards do not match offer tile constraints or the player omitted available character cards"
            );
        }

        int initialFood = player.getFood();
        int initialPrestige = player.getPrestige();

        // offer tile food bonus is applied before any card payment
        int foodBonus = targetTile.getAction().getFoodBonus();
        if (foodBonus > 0) {
            player.addFood(foodBonus);
        }

        // selected buildings are validated and paid for internally by the player
        player.payForBuildings(foundBuildings);

        // transaction is executed and cards are removed from the board
        for (CharacterCard c : foundCharacters) {
            this.upperTribeRow.removeIf(card -> card.getId().equals(c.getId()));
            this.lowerTribeRow.removeIf(card -> card.getId().equals(c.getId()));
            player.acquireCharacter(c);
        }

        for (BuildingCard b : foundBuildings) {
            this.upperBuildingRow.removeIf(card -> card.getId().equals(b.getId()));
            this.lowerBuildingRow.removeIf(card -> card.getId().equals(b.getId()));
            player.acquireBuilding(b, this);
        }

        // player is moved to the turn order tile
        targetTile.removePlayer();
        int turnOrderPosition = this.turnOrderTile.placePlayer(player);

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
        } else {
            // resolve_actions phase is complete
            // building effects for the end of the action phase are triggered
            for (Player p : this.players) {
                p.triggerBuildingEffectsOnRoundEnd(this);
            }

            // phase is advanced if no extra acquisition is triggered
            if (this.currentPhase != GamePhase.EXTRA_ACQUISITION) {
                this.currentPhase = GamePhase.RESOLVE_EVENTS;
                this.currentPlayer = null;
            }
        }

        PlayerStatsChangeReceipt statsChange = null;
        if (player.getFood() != initialFood || player.getPrestige() != initialPrestige) {
            statsChange = new PlayerStatsChangeReceipt(
                    playerNickname,
                    player.getFood() - initialFood,
                    player.getPrestige() - initialPrestige,
                    player.getFood(),
                    player.getPrestige()
            );
        }

        // final receipt is generated and returned
        return new ActionReceipt(statsChange, turnOrderPosition);
    }

    /**
     * The player's action is forfeited, moving their totem directly to the turn order tile
     * without acquiring any cards.
     *
     * @param playerNickname the nickname of the player forfeiting the action
     * @return an action receipt containing the resulting statistics change and turn order position
     */
    public ActionReceipt forfeitAction(String playerNickname) {
        Objects.requireNonNull(playerNickname, "playerNickname cannot be null");

        // player is validated
        validateTurn(playerNickname);
        // phase is validated
        validatePhase(new GamePhase[]{GamePhase.RESOLVE_ACTIONS});

        Player player = getPlayerByNickname(playerNickname);

        OfferTile targetTile = null;
        // player occupied tile is identified
        for (OfferTile tile : this.offerTrack) {
            if (tile.isOccupiedBy(player)) {
                targetTile = tile;
                break;
            }
        }

        // tile existence is verified
        if (targetTile == null) {
            throw new InvalidGameActionException(
                    GameErrorReason.PLAYER_NOT_ON_TILE,
                    "player is not occupying any offer tile"
            );
        }

        // stats are recorded before totem placement
        int prePlacementFood = player.getFood();
        int prePlacementPrestige = player.getPrestige();

        // player is removed from the board and moved to the turn order tile
        targetTile.removePlayer();
        int turnOrderPosition = this.turnOrderTile.placePlayer(player);

        // placement bonus or malus is evaluated
        PlayerStatsChangeReceipt statsChange = null;
        if (player.getFood() != prePlacementFood || player.getPrestige() != prePlacementPrestige) {
            statsChange = new PlayerStatsChangeReceipt(
                    playerNickname,
                    player.getFood() - prePlacementFood,
                    player.getPrestige() - prePlacementPrestige,
                    player.getFood(),
                    player.getPrestige()
            );
        }

        // next player is determined by scanning the offer track
        Player nextPlayer = null;
        for (OfferTile tile : this.offerTrack) {
            if (!tile.isFree()) {
                nextPlayer = tile.getOccupyingPlayer();
                break;
            }
        }

        if (nextPlayer != null) {
            this.currentPlayer = nextPlayer;
        } else {
            // building effects are triggered on round end
            for (Player p : this.players) {
                p.triggerBuildingEffectsOnRoundEnd(this);
            }

            // phase transition is handled
            if (this.currentPhase != GamePhase.EXTRA_ACQUISITION) {
                this.currentPhase = GamePhase.RESOLVE_EVENTS;
                this.currentPlayer = null;
            }
        }

        // final receipt is generated and returned
        return new ActionReceipt(statsChange, turnOrderPosition);
    }

    /**
     * The extra acquisition phase is resolved, allowing a player to purchase an additional
     * card from the upper row.
     *
     * @param playerNickname the nickname of the player performing the extra acquisition
     * @param cardId the id of the card to be acquired
     * @return an extra acquisition receipt detailing the costs and bonuses of the acquisition
     */
    public ExtraAcquisitionReceipt resolveExtraAcquisition(String playerNickname, String cardId) {
        Objects.requireNonNull(playerNickname, "playerNickname cannot be null");

        // game phase is advanced and current player is reset if action is skipped
        if (cardId == null) {
            this.currentPhase = GamePhase.RESOLVE_EVENTS;
            this.currentPlayer = null;
            // empty receipt is returned
            return new ExtraAcquisitionReceipt(null);
        }

        // player is validated
        validateTurn(playerNickname);
        // phase is validated
        validatePhase(new GamePhase[]{GamePhase.EXTRA_ACQUISITION});

        Player player = getPlayerByNickname(playerNickname);

        Card targetCard = null;
        boolean isCharacter = false;

        // card is searched exclusively in the upper tribe row
        for (Card c : this.upperTribeRow) {
            if (c.getId().equals(cardId)) {
                targetCard = c;
                isCharacter = true;
                break;
            }
        }

        // card is searched in the upper building row if not found
        if (targetCard == null) {
            for (BuildingCard b : this.upperBuildingRow) {
                if (b.getId().equals(cardId)) {
                    targetCard = b;
                    break;
                }
            }
        }

        // card existence is verified
        if (targetCard == null) {
            throw new InvalidGameActionException(
                    GameErrorReason.CARD_NOT_FOUND,
                    "card " + cardId + " is not available in the upper row"
            );
        }

        // stats are recorded before acquisition
        int preAcquisitionFood = player.getFood();
        int preAcquisitionPrestige = player.getPrestige();

        // transaction is executed based on card type
        if (isCharacter) {
            this.upperTribeRow.remove(targetCard);
            player.acquireCharacter((CharacterCard) targetCard);
        } else {
            BuildingCard buildingCard = (BuildingCard) targetCard;

            // building is paid for internally
            List<BuildingCard> buildingList = new ArrayList<>();
            buildingList.add(buildingCard);
            player.payForBuildings(buildingList);

            this.upperBuildingRow.remove(buildingCard);
            player.acquireBuilding(buildingCard, this);
        }

        PlayerStatsChangeReceipt statsChange = null;
        if (player.getFood() != preAcquisitionFood || player.getPrestige() != preAcquisitionPrestige) {
            statsChange = new PlayerStatsChangeReceipt(
                    playerNickname,
                    player.getFood() - preAcquisitionFood,
                    player.getPrestige() - preAcquisitionPrestige,
                    player.getFood(),
                    player.getPrestige()
            );
        }

        // phase is advanced and current player is reset
        this.currentPhase = GamePhase.RESOLVE_EVENTS;
        this.currentPlayer = null;

        // receipt is generated and returned
        return new ExtraAcquisitionReceipt(statsChange);
    }

    /**
     * Events in the lower tribe row are resolved sequentially and resolution receipts are returned.
     *
     * @return a list of receipts detailing the resolution of each event
     * @throws IllegalStateException if the current phase is not resolve_events
     */
    public List<EventResolutionReceipt> resolveEvents() {
        // phase is validated
        validatePhase(new GamePhase[]{GamePhase.RESOLVE_EVENTS});

        EventCollectorVisitor visitor = new EventCollectorVisitor();

        // cards are processed using double dispatch to extract events
        for (Card card : this.lowerTribeRow) {
            card.accept(visitor);
        }

        if (this.currentRound == 10 || this.tribeDeck.isEmpty()) {
            for (Card card : this.upperTribeRow) {
                card.accept(visitor);
            }
        }

        List<EventCard> eventsToResolve = visitor.getCollectedEvents();

        // events are sorted naturally
        java.util.Collections.sort(eventsToResolve);

        // list of resolution receipts is initialized
        List<EventResolutionReceipt> allReceipts = new ArrayList<>();

        // events are resolved sequentially and receipts are collected
        for (EventCard event : eventsToResolve) {
            List<PlayerStatsChangeReceipt> statsChanges = event.resolveEvent(this);
            allReceipts.add(new EventResolutionReceipt(event.getId(), statsChanges));
        }

        // game phase is updated to end round
        this.currentPhase = GamePhase.END_ROUND;

        // collected receipts are returned
        return allReceipts;
    }

    /**
     * The current round is ended and the game state is prepared for the next one.
     */
    public void endRound() {
        // phase is validated
        validatePhase(new GamePhase[]{GamePhase.END_ROUND});

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

    /**
     * The specified next era is started.
     *
     * @param era the era to start
     */
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

    /**
     * The final results of the game are calculated.
     *
     * @return the game result containing winners and scores
     */
    public GameResult calculateResults() {
        Map<Player, int[]> finalScores = new HashMap<>();

        // final prestige logic is encapsulated and resolved within the player
        for (Player player : this.players) {
            player.computeFinalPrestige(this);
        }

        // highest ranking player is determined implicitly through the comparable interface
        Player topPlayer = Collections.max(this.players);

        // list of winners is populated with players matching the top score (handling ties naturally)
        List<Player> winners = new ArrayList<>();
        for (Player player : this.players) {
            if (player.compareTo(topPlayer) == 0) {
                winners.add(player);
            }

            // final score and food amount are read and saved in an array (index 0: prestige, index 1: food)
            int[] scoreData = new int[]{player.getPrestige(), player.getFood()};
            finalScores.put(player, scoreData);
        }

        // game result is created and returned
        return new GameResult(winners, finalScores);
    }

    // player management

    /**
     * A player is added to the game.
     *
     * @param player the player to add
     */
    public synchronized void addPlayer(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        this.players.add(player);
    }

    /**
     * A player is removed from the game by nickname.
     *
     * @param playerNickname the nickname of the player to remove
     */
    public synchronized void removePlayer(String playerNickname) {
        Objects.requireNonNull(playerNickname, "playerNickname cannot be null");

        Player player = getPlayerByNickname(playerNickname);

        if (!this.players.contains(player)) {
            throw new IllegalStateException("player not found in game state");
        }
        this.players.remove(player);
    }

    // state observation (getters and setters)

    /**
     * The identifier of the game is returned.
     *
     * @return the game id
     */
    public int getGameId() {
        return this.gameId;
    }

    /**
     * The target number of players is returned.
     *
     * @return the target players
     */
    public int getTargetPlayers() {
        return this.targetPlayers;
    }

    /**
     * The list of players currently in the game is returned.
     *
     * @return the list of players
     */
    public synchronized List<Player> getPlayers() {
        return List.copyOf(this.players);
    }

    /**
     * The list of offer tiles on the track is returned.
     *
     * @return the offer track
     */
    public List<OfferTile> getOfferTrack() {
        return List.copyOf(this.offerTrack);
    }

    /**
     * The current turn order of players is returned.
     *
     * @return the turn order list
     */
    public List<Player> getTurnOrder() {
        return List.copyOf(this.turnOrderTile.getTurnOrder());
    }

    /**
     * The cards present in the upper tribe row are returned.
     *
     * @return the upper tribe row cards
     */
    public List<Card> getUpperTribeRow() {
        return List.copyOf(this.upperTribeRow);
    }

    /**
     * The cards present in the lower tribe row are returned.
     *
     * @return the lower tribe row cards
     */
    public List<Card> getLowerTribeRow() {
        return List.copyOf(this.lowerTribeRow);
    }

    /**
     * The buildings present in the upper building row are returned.
     *
     * @return the upper building row cards
     */
    public List<BuildingCard> getUpperBuildingRow() {
        return List.copyOf(this.upperBuildingRow);
    }

    /**
     * The buildings present in the lower building row are returned.
     *
     * @return the lower building row cards
     */
    public List<BuildingCard> getLowerBuildingRow() {
        return List.copyOf(this.lowerBuildingRow);
    }

    /**
     * The current size of the tribe deck is returned.
     *
     * @return the tribe deck size
     */
    public int getTribeDeckSize() {
        return this.tribeDeck.getSize();
    }

    /**
     * The era of the first card in the tribe deck is returned.
     *
     * @return the first card era
     */
    public int getTribeDeckFirstCardEra() {
        if (this.tribeDeck.isEmpty()) {
            return -1;
        }
        return this.tribeDeck.getFirstCardEra();
    }

    /**
     * The unique identifier of the turn order tile currently in use is returned.
     *
     * @return the string identifier of the tile
     */
    public String getTurnOrderTileId() {
        // tile identifier is retrieved from model entity
        return this.turnOrderTile.getId();
    }

    /**
     * The current round number is returned.
     *
     * @return the current round
     */
    public int getCurrentRound() {
        return this.currentRound;
    }

    /**
     * The current era number is returned.
     *
     * @return the current era
     */
    public int getCurrentEra() {
        return currentEra;
    }

    /**
     * The current phase of the game is returned.
     *
     * @return the current game phase
     */
    public GamePhase getCurrentPhase() {
        return this.currentPhase;
    }

    /**
     * The current phase of the game is set.
     *
     * @param currentPhase the new game phase
     */
    public void setCurrentPhase(GamePhase currentPhase) {
        Objects.requireNonNull(currentPhase, "currentPhase cannot be null");

        this.currentPhase = currentPhase;
    }

    /**
     * The player currently taking their turn is returned.
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    /**
     * The player currently taking their turn is set.
     *
     * @param currentPlayer the new current player
     */
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * A player is retrieved by their nickname.
     *
     * @param nickname the player's nickname
     * @return the associated player
     */
    private Player getPlayerByNickname(String nickname) {
        Objects.requireNonNull(nickname, "nickname cannot be null");

        return this.players.stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("player " + nickname + " not found in game state"));
    }
}
