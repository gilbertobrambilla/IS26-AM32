package it.polimi.ingsw.am32.model;

import java.util.*;

/**
 * Represents the final result of a game, including winners and the leaderboard.
 */
public class GameResult {
    private final List<Player> winners;
    private final Map<Player, int[]> leaderboard;

    /**
     * Initializes the game result with the given winners and leaderboard.
     *
     * @param winners the list of winning players
     * @param leaderboard the map containing the leaderboard scores
     */
    public GameResult(List<Player> winners, Map<Player, int[]> leaderboard) {
        Objects.requireNonNull(winners, "winners cannot be null");
        Objects.requireNonNull(leaderboard, "leaderboard cannot be null");

        this.winners = new ArrayList<>(winners);

        // deep copy of the array is performed to ensure immutability
        this.leaderboard = new HashMap<>();
        for (Map.Entry<Player, int[]> entry : leaderboard.entrySet()) {
            this.leaderboard.put(entry.getKey(), entry.getValue().clone());
        }
    }

    /**
     * Gets the list of winners.
     *
     * @return the list of winning players
     */
    public List<Player> getWinners() {
        return List.copyOf(this.winners);
    }

    /**
     * Gets the game leaderboard.
     *
     * @return the leaderboard map
     */
    public Map<Player, int[]> getLeaderboard() {
        return Map.copyOf(this.leaderboard);
    }
}
