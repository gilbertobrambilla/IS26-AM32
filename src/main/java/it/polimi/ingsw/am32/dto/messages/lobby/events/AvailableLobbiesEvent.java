package it.polimi.ingsw.am32.dto.messages.lobby.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.lobby.models.LobbyInfoDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

import java.util.List;

/**
 * An available lobbies event is represented.
 */
public class AvailableLobbiesEvent implements MessageDTO {
    private final List<LobbyInfoDTO> lobbies;

    /**
     * An available lobbies event is initialized with lobby summaries.
     *
     * @param games the available lobby summaries
     */
    public AvailableLobbiesEvent(List<LobbyInfoDTO> games) {
        this.lobbies = List.copyOf(games);
    }

    /**
     * The available lobby summaries are returned.
     *
     * @return the available lobby summaries
     */
    public List<LobbyInfoDTO> getLobbies() {
        return lobbies;
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
