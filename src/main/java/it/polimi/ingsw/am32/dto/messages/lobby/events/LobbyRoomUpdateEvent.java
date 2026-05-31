package it.polimi.ingsw.am32.dto.messages.lobby.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.lobby.models.LobbyInfoDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * A lobby room update event is represented.
 */
public class LobbyRoomUpdateEvent implements MessageDTO {
    private final LobbyInfoDTO lobbyInfo;

    /**
     * A lobby room update event is initialized with lobby data.
     *
     * @param lobby the updated lobby data
     */
    public LobbyRoomUpdateEvent(LobbyInfoDTO lobby) {
        this.lobbyInfo = lobby;
    }

    /**
     * The updated lobby data is returned.
     *
     * @return the updated lobby data
     */
    public LobbyInfoDTO getLobbyInfo() {
        return lobbyInfo;
    }

    /**
     * The visitor is accepted to dispatch the event.
     *
     * @param visitor the client message visitor
     */
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
