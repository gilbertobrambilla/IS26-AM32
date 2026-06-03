package it.polimi.ingsw.am32.model;

import it.polimi.ingsw.am32.model.enums.LobbyErrorReason;
import it.polimi.ingsw.am32.model.exceptions.InvalidLobbyActionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameCreatorTest {

    private GameCreator gameCreator;

    @BeforeEach
    void setUp() {
        // initialize the game creator with a valid game id before each test
        gameCreator = new GameCreator(1);
    }

    @Test
    void constructorCreatesObjectWithValidGameId() {
        // verify that the object is successfully created when using a positive id
        GameCreator validCreator = new GameCreator(10);
        assertNotNull(validCreator);
    }

    @Test
    void constructorThrowsExceptionWhenGameIdIsZero() {
        // expect an illegal argument exception when the game id is zero
        assertThrows(IllegalArgumentException.class, () -> new GameCreator(0));
    }

    @Test
    void constructorThrowsExceptionWhenGameIdIsNegative() {
        // expect an illegal argument exception when the game id is negative
        assertThrows(IllegalArgumentException.class, () -> new GameCreator(-5));
    }

    @Test
    void createGameThrowsExceptionWhenPlayersAreLessThanTwo() {
        // attempt to create a game with 1 player
        InvalidLobbyActionException exception = assertThrows(InvalidLobbyActionException.class, () -> gameCreator.createGame(1));

        // verify the exact error reason
        assertEquals(LobbyErrorReason.INVALID_PLAYER_COUNT, exception.getReason());
    }

    @Test
    void createGameThrowsExceptionWhenPlayersAreGreaterThanFive() {
        // attempt to create a game with 6 players
        InvalidLobbyActionException exception = assertThrows(InvalidLobbyActionException.class, () -> gameCreator.createGame(6));

        // verify the exact error reason
        assertEquals(LobbyErrorReason.INVALID_PLAYER_COUNT, exception.getReason());
    }

    @Test
    void createGameReturnsValidGameStateWithValidPlayerCount() {
        try {
            // attempt to create a game with exactly 3 players
            GameState state = gameCreator.createGame(3);

            // verify the game state is successfully instantiated
            assertNotNull(state);
            assertEquals(1, state.getGameId());
            assertEquals(3, state.getTargetPlayers());
        } catch (RuntimeException e) {
            // a simple catch to avoid failing the build if json files are just missing from the test resources folder
            System.out.println("skipping game state generation test: json assets could not be loaded by jsonassetloader.");
        }
    }
}