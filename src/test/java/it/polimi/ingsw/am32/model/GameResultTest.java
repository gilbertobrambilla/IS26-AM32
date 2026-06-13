package it.polimi.ingsw.am32.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameResultTest {

    private Player player1;
    private Player player2;
    private List<Player> winners;
    private Map<Player, int[]> leaderboard;

    @BeforeEach
    void setUp() {
        // instantiate players for testing
        player1 = new Player("Alice");
        player2 = new Player("Bob");

        // initialize the winners list
        winners = new ArrayList<>();
        winners.add(player1);

        // initialize the leaderboard map
        leaderboard = new HashMap<>();
        leaderboard.put(player1, new int[]{20, 5});
        leaderboard.put(player2, new int[]{15, 3});
    }

    @Test
    void constructorCreatesObjectWithValidParameters() {
        // create the game result object
        GameResult result = new GameResult(winners, leaderboard);

        // verify the object is successfully created
        assertNotNull(result);
        assertEquals(1, result.getWinners().size());
        assertEquals(2, result.getLeaderboard().size());
    }

    @Test
    void constructorThrowsExceptionWhenWinnersListIsNull() {
        // expect a null pointer exception when the winners list is null
        assertThrows(NullPointerException.class, () -> new GameResult(null, leaderboard));
    }

    @Test
    void constructorThrowsExceptionWhenLeaderboardIsNull() {
        // expect a null pointer exception when the leaderboard is null
        assertThrows(NullPointerException.class, () -> new GameResult(winners, null));
    }

    @Test
    void constructorPerformsDeepCopyOfLeaderboardArrays() {
        // create the game result object
        GameResult result = new GameResult(winners, leaderboard);

        // modify the original array passed to the constructor
        leaderboard.get(player1)[0] = 99;

        // verify that the internal state of the game result was not affected
        int[] resultScores = result.getLeaderboard().get(player1);
        assertEquals(20, resultScores[0]);
    }

    @Test
    void getWinnersReturnsUnmodifiableList() {
        // create the game result object
        GameResult result = new GameResult(winners, leaderboard);

        // retrieve the winners list
        List<Player> retrievedWinners = result.getWinners();

        // expect an unsupported operation exception when trying to modify it
        assertThrows(UnsupportedOperationException.class, () -> retrievedWinners.add(player2));
    }

    @Test
    void getLeaderboardReturnsUnmodifiableMap() {
        // create the game result object
        GameResult result = new GameResult(winners, leaderboard);

        // retrieve the leaderboard map
        Map<Player, int[]> retrievedLeaderboard = result.getLeaderboard();

        // expect an unsupported operation exception when trying to modify it
        assertThrows(UnsupportedOperationException.class, () -> retrievedLeaderboard.clear());
    }
}