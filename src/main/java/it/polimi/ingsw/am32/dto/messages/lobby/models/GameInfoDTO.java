package it.polimi.ingsw.am32.dto.messages.lobby.models;

import java.io.Serializable;

public class GameInfoDTO implements Serializable {
    private final int gameId;
    private final int currentPlayersCount;
    private final int targetPlayers;

    public GameInfoDTO(int gameId, int currentPlayersCount, int targetPlayers) {
        this.gameId = gameId;
        this.currentPlayersCount = currentPlayersCount;
        this.targetPlayers = targetPlayers;
    }

    public int getGameId() {
        return gameId;
    }

    public int getCurrentPlayersCount() {
        return currentPlayersCount;
    }

    public int getTargetPlayers() {
        return targetPlayers;
    }
}
