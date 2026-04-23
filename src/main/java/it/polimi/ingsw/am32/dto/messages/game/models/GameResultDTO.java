package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GameResultDTO implements Serializable {
    private final List<String> winners;
    private final Map<String, int[]> matchScores;
    private final List<LeaderboardEntryDTO> globalLeaderboard;
    private final int globalPosition;

    public GameResultDTO(List<String> winners, Map<String, int[]> matchScores, List<LeaderboardEntryDTO> globalLeaderboard, int globalPosition) {
        this.winners = List.copyOf(winners);
        this.matchScores = Map.copyOf(matchScores);
        this.globalLeaderboard = List.copyOf(globalLeaderboard);
        this.globalPosition = globalPosition;
    }

    public List<String> getWinners() {
        return winners;
    }

    public Map<String, int[]> getMatchScores() {
        return matchScores;
    }

    public List<LeaderboardEntryDTO> getGlobalLeaderboard() {
        return globalLeaderboard;
    }

    public int getGlobalPosition() {
        return globalPosition;
    }
}
