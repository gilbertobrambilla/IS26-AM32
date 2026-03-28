package it.polimi.ingsw.am32;

import java.util.*;

public class GameResult {
    private final List<Player> winners;
    private final Map<Player, int[]> leaderboard;

    public GameResult(List<Player> winners, Map<Player, int[]> leaderboard) {
        Objects.requireNonNull(winners, "winners cannot be null");
        Objects.requireNonNull(leaderboard, "leaderboard cannot be null");

        this.winners = new ArrayList<>(winners);

        // a deep copy of the array is performed to ensure immutability
        this.leaderboard = new HashMap<>();
        for (Map.Entry<Player, int[]> entry : leaderboard.entrySet()) {
            this.leaderboard.put(entry.getKey(), entry.getValue().clone());
        }
    }

    public List<Player> getWinners() {
        return List.copyOf(this.winners);
    }

    public Map<Player, int[]> getLeaderboard() {
        return Map.copyOf(this.leaderboard);
    }
}
