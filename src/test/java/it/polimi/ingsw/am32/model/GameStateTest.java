package it.polimi.ingsw.am32.model;

import it.polimi.ingsw.am32.model.cards.Card;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.enums.GameErrorReason;
import it.polimi.ingsw.am32.model.enums.GamePhase;
import it.polimi.ingsw.am32.model.exceptions.InvalidGameActionException;
import it.polimi.ingsw.am32.model.tiles.OfferTile;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    private GameState gameState;
    private TurnOrderTile dummyTurnOrderTile;
    private List<OfferTile> dummyOfferTrack;
    private Deck<Card> dummyTribeDeck;
    private Map<Integer, Deck<BuildingCard>> dummyBuildingDecks;

    @BeforeEach
    void setUp() {
        // instantiate dummy dependencies for the game state
        dummyTurnOrderTile = new TurnOrderTile("dummy_tile", 2, Arrays.asList(0, 0), Arrays.asList(0, 0));
        dummyOfferTrack = new ArrayList<>();
        dummyTribeDeck = new Deck<>();
        dummyBuildingDecks = new HashMap<>();

        // populate the map with empty decks to avoid null pointer exceptions during era setups
        dummyBuildingDecks.put(1, new Deck<>());
        dummyBuildingDecks.put(2, new Deck<>());
        dummyBuildingDecks.put(3, new Deck<>());

        // instantiate the game state with 2 target players for simple testing
        gameState = new GameState(1, 2, dummyOfferTrack, dummyTurnOrderTile, dummyTribeDeck, dummyBuildingDecks);
    }

    @Test
    void constructorThrowsExceptionWhenGameIdIsInvalid() {
        // expect an exception when game id is less than 1
        assertThrows(IllegalArgumentException.class, () -> new GameState(0, 2, dummyOfferTrack, dummyTurnOrderTile, dummyTribeDeck, dummyBuildingDecks));
    }

    @Test
    void constructorThrowsExceptionWhenTargetPlayersIsInvalid() {
        // expect an exception when target players is strictly out of bounds
        assertThrows(IllegalArgumentException.class, () -> new GameState(1, 1, dummyOfferTrack, dummyTurnOrderTile, dummyTribeDeck, dummyBuildingDecks));
        assertThrows(IllegalArgumentException.class, () -> new GameState(1, 6, dummyOfferTrack, dummyTurnOrderTile, dummyTribeDeck, dummyBuildingDecks));
    }

    @Test
    void constructorThrowsExceptionWhenParametersAreNull() {
        // expect null pointer exceptions when essential collections are passed as null
        assertThrows(NullPointerException.class, () -> new GameState(1, 2, null, dummyTurnOrderTile, dummyTribeDeck, dummyBuildingDecks));
        assertThrows(NullPointerException.class, () -> new GameState(1, 2, dummyOfferTrack, null, dummyTribeDeck, dummyBuildingDecks));
        assertThrows(NullPointerException.class, () -> new GameState(1, 2, dummyOfferTrack, dummyTurnOrderTile, null, dummyBuildingDecks));
        assertThrows(NullPointerException.class, () -> new GameState(1, 2, dummyOfferTrack, dummyTurnOrderTile, dummyTribeDeck, null));
    }

    @Test
    void addPlayerIncreasesPlayerCountAndAssignsCorrectly() {
        // create a new player and add it to the game state
        Player player = new Player("Alice");
        gameState.addPlayer(player);

        // verify that the player is effectively stored in the game state
        assertEquals(1, gameState.getPlayers().size());
        assertEquals("Alice", gameState.getPlayers().getFirst().getNickname());
    }

    @Test
    void removePlayerDecreasesPlayerCount() {
        // add and then subsequently remove a player
        Player player = new Player("Alice");
        gameState.addPlayer(player);
        gameState.removePlayer("Alice");

        // verify that the player list is completely empty
        assertTrue(gameState.getPlayers().isEmpty());
    }

    @Test
    void removePlayerThrowsExceptionIfPlayerNotFound() {
        // expect an exception when trying to remove a non-existent player
        assertThrows(IllegalArgumentException.class, () -> gameState.removePlayer("Ghost"));
    }

    @Test
    void initGameThrowsExceptionWhenMissingPlayers() {
        // add only one player when two are targeted
        gameState.addPlayer(new Player("Alice"));

        // expect an illegal state exception due to incomplete lobby
        assertThrows(IllegalStateException.class, () -> gameState.initGame());
    }

    @Test
    void initGameSetsUpInitialStateCorrectly() {
        // add the exact required number of players
        gameState.addPlayer(new Player("Alice"));
        gameState.addPlayer(new Player("Bob"));

        // initialize the game
        gameState.initGame();

        // verify that rounds, eras and phases are configured correctly
        assertEquals(1, gameState.getCurrentRound());
        assertEquals(1, gameState.getCurrentEra());
        assertEquals(GamePhase.PLACE_TOTEMS, gameState.getCurrentPhase());

        // verify that the current player is successfully assigned
        assertNotNull(gameState.getCurrentPlayer());
    }

    @Test
    void placeTotemThrowsExceptionWhenNotPlayerTurn() {
        // set up players and fully initialize the game
        gameState.addPlayer(new Player("Alice"));
        gameState.addPlayer(new Player("Bob"));
        gameState.initGame();

        // dynamically find the player who is currently waiting for their turn
        String wrongPlayer = gameState.getCurrentPlayer().getNickname().equals("Alice") ? "Bob" : "Alice";

        // expect an exception when the wrong player tries to force an action
        InvalidGameActionException exception = assertThrows(InvalidGameActionException.class, () -> gameState.placeTotem(wrongPlayer, "offer_1"));
        assertEquals(GameErrorReason.NOT_YOUR_TURN, exception.getReason());
    }

    @Test
    void placeTotemThrowsExceptionWhenWrongPhase() {
        // set up players and initialize the game
        gameState.addPlayer(new Player("Alice"));
        gameState.addPlayer(new Player("Bob"));
        gameState.initGame();

        // manually change the internal phase to an invalid one for totem placement
        gameState.setCurrentPhase(GamePhase.RESOLVE_EVENTS);

        // expect an exception due to incorrect phase execution
        String currentPlayerName = gameState.getCurrentPlayer().getNickname();
        InvalidGameActionException exception = assertThrows(InvalidGameActionException.class, () -> gameState.placeTotem(currentPlayerName, "offer_1"));
        assertEquals(GameErrorReason.INVALID_PHASE, exception.getReason());
    }

    @Test
    void resolveActionThrowsExceptionIfDuplicateCardsRequested() {
        // set up players and initialize the game
        gameState.addPlayer(new Player("Alice"));
        gameState.addPlayer(new Player("Bob"));
        gameState.initGame();

        // manually change the phase to allow direct action resolution
        gameState.setCurrentPhase(GamePhase.RESOLVE_ACTIONS);
        String currentPlayerName = gameState.getCurrentPlayer().getNickname();

        // create a malicious list with duplicate card ids
        List<String> duplicates = Arrays.asList("card_1", "card_1");
        List<String> valid = Arrays.asList("building_1");

        // expect an exception due to duplicate selection detected in validation
        InvalidGameActionException exception = assertThrows(InvalidGameActionException.class, () -> gameState.resolveAction(currentPlayerName, duplicates, valid));
        assertEquals(GameErrorReason.INVALID_SELECTION_AMOUNT, exception.getReason());
    }

    @Test
    void resolveExtraAcquisitionAdvancesPhaseWhenCardIdIsNull() {
        // set up players and initialize the game
        gameState.addPlayer(new Player("Alice"));
        gameState.addPlayer(new Player("Bob"));
        gameState.initGame();

        // manually set the phase to extra acquisition
        gameState.setCurrentPhase(GamePhase.EXTRA_ACQUISITION);
        String currentPlayerName = gameState.getCurrentPlayer().getNickname();

        // call extra acquisition passing a null card id indicating a skipped action
        gameState.resolveExtraAcquisition(currentPlayerName, null);

        // verify that the phase gracefully advanced to resolve events
        assertEquals(GamePhase.RESOLVE_EVENTS, gameState.getCurrentPhase());
        assertNull(gameState.getCurrentPlayer());
    }

    @Test
    void endRoundShiftsCardsAndIncrementsRoundNumber() {
        // set up players and initialize the game
        gameState.addPlayer(new Player("Alice"));
        gameState.addPlayer(new Player("Bob"));
        gameState.initGame();

        // artificially change phase to end round to satisfy method validation
        gameState.setCurrentPhase(GamePhase.END_ROUND);

        // invoke the end round routine
        gameState.endRound();

        // verify that the round index incremented and the phase looped back to totem placement
        assertEquals(2, gameState.getCurrentRound());
        assertEquals(GamePhase.PLACE_TOTEMS, gameState.getCurrentPhase());
    }

    @Test
    void calculateResultsReturnsTopPlayerAsWinner() {
        // add players and artificially grant them unbalanced prestige scores
        Player p1 = new Player("Alice");
        Player p2 = new Player("Bob");
        p1.addPrestige(10);
        p2.addPrestige(20);

        gameState.addPlayer(p1);
        gameState.addPlayer(p2);

        // trigger the final calculation of the game results
        GameResult result = gameState.calculateResults();

        // verify that bob is recognized as the undisputed single winner
        assertEquals(1, result.getWinners().size());
        assertEquals("Bob", result.getWinners().getFirst().getNickname());
    }
}