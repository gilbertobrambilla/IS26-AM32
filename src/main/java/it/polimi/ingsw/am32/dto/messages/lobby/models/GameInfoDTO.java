package it.polimi.ingsw.am32.dto.messages.lobby.models;

import java.io.Serializable;

public class GameInfoDTO implements Serializable {
    private final int gameId;
    private final int currentPlayers;
    private final int targetPlayers;

    public GameInfoDTO(int gameId, int currentPlayers, int targetPlayers) {
        this.gameId = gameId;
        this.currentPlayers = currentPlayers;
        this.targetPlayers = targetPlayers;
    }

    public int getGameId() {
        return this.gameId;
    }

    public int getCurrentPlayers() {
        return this.currentPlayers;
    }

    public int getTargetPlayers() {
        return this.targetPlayers;
    }
}
