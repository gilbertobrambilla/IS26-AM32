package it.polimi.ingsw.am32.dto.messages.lobby.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

import java.util.List;

public class LobbyStateUpdatedEvent implements MessageDTO {
    private final int gameId;
    private final List<String> currentPlayers;
    private final int targetPlayers;

    public LobbyStateUpdatedEvent(int gameId, List<String> currentPlayers, int targetPlayers) {
        this.gameId = gameId;
        this.currentPlayers = List.copyOf(currentPlayers);
        this.targetPlayers = targetPlayers;
    }

    public int getGameId() {
        return gameId;
    }

    public List<String> getCurrentPlayers() {
        return currentPlayers;
    }

    public int getTargetPlayers() {
        return targetPlayers;
    }

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
