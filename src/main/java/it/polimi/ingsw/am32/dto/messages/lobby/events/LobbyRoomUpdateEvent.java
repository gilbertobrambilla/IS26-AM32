package it.polimi.ingsw.am32.dto.messages.lobby.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.lobby.models.LobbyInfoDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

public class LobbyRoomUpdateEvent implements MessageDTO {
    private final LobbyInfoDTO lobbyInfo;

    public LobbyRoomUpdateEvent(LobbyInfoDTO lobby) {
        this.lobbyInfo = lobby;
    }

    public LobbyInfoDTO getLobbyInfo() {
        return lobbyInfo;
    }

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
