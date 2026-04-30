package it.polimi.ingsw.am32.dto.messages.lobby.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.lobby.models.LobbyInfoDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

import java.util.List;

public class AvailableLobbiesEvent implements MessageDTO {
    private final List<LobbyInfoDTO> lobbies;

    public AvailableLobbiesEvent(List<LobbyInfoDTO> games) {
        this.lobbies = List.copyOf(games);
    }

    public List<LobbyInfoDTO> getLobbies() {
        return lobbies;
    }

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
