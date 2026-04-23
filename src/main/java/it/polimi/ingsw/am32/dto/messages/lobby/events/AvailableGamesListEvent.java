package it.polimi.ingsw.am32.dto.messages.lobby.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.lobby.models.GameInfoDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

import java.util.List;

public class AvailableGamesListEvent implements MessageDTO {
    private final List<GameInfoDTO> games;

    public AvailableGamesListEvent(List<GameInfoDTO> games) {
        this.games = List.copyOf(games);
    }

    public List<GameInfoDTO> getGames() {
        return games;
    }

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
