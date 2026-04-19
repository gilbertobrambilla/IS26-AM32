package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GameResultDTO implements Serializable {
    private final List<String> winnerNicknames;
    private final Map<String, int[]> matchScores;
    private final List<LeaderboardEntryDTO> globalLeaderboard;
    private final int playerGlobalPosition;

    public GameResultDTO(List<String> winnerNicknames, Map<String, int[]> matchScores, List<LeaderboardEntryDTO> globalLeaderboard, int playerGlobalPosition) {
        this.winnerNicknames = winnerNicknames;
        this.matchScores = matchScores;
        this.globalLeaderboard = globalLeaderboard;
        this.playerGlobalPosition = playerGlobalPosition;
    }

    public List<String> getWinnerNicknames() {
        return this.winnerNicknames;
    }

    public Map<String, int[]> getMatchScores() {
        return this.matchScores;
    }

    public List<LeaderboardEntryDTO> getGlobalLeaderboard() {
        return this.globalLeaderboard;
    }

    public int getPlayerGlobalPosition() {
        return this.playerGlobalPosition;
    }
}
