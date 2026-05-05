package it.polimi.ingsw.am32.dto.messages.lobby.models;

import java.io.Serializable;
import java.util.List;

public class LobbyInfoDTO implements Serializable {
    private final int gameId;
    private final List<String> currentPlayers;
    private final int currentPlayersCount;
    private final int targetPlayers;

    public LobbyInfoDTO(int gameId, int currentPlayersCount, List<String> currentPlayers, int targetPlayers) {
        this.gameId = gameId;
        this.currentPlayersCount = currentPlayersCount;
        this.currentPlayers = currentPlayers;
        this.targetPlayers = targetPlayers;
    }

    public int getGameId() {
        return gameId;
    }

    public List<String> getCurrentPlayers() {
        return currentPlayers;
    }

    public int getCurrentPlayersCount() {
        return currentPlayersCount;
    }

    public int getTargetPlayers() {
        return targetPlayers;
    }
}
