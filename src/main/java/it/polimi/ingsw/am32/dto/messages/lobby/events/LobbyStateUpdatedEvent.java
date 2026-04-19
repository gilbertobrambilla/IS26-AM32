package it.polimi.ingsw.am32.dto.messages.lobby.events;

import java.util.List;

public class LobbyStateUpdatedEvent {
    private final int gameId;
    private final List<String> currentPlayersNicknames;
    private final int targetPlayers;

    public LobbyStateUpdatedEvent(int gameId, List<String> currentPlayersNicknames, int targetPlayers) {
        this.gameId = gameId;
        this.currentPlayersNicknames = currentPlayersNicknames;
        this.targetPlayers = targetPlayers;
    }

    public int getGameId() {
        return gameId;
    }

    public List<String> getCurrentPlayersNicknames() {
        return currentPlayersNicknames;
    }

    public int getTargetPlayers() {
        return targetPlayers;
    }
}
