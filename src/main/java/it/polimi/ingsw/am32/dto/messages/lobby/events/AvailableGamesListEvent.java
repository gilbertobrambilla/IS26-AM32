package it.polimi.ingsw.am32.dto.messages.lobby.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.lobby.models.GameInfoDTO;

import java.util.List;

public class AvailableGamesListEvent implements MessageDTO {
    private final List<GameInfoDTO> availableGames;

    public AvailableGamesListEvent(List<GameInfoDTO> availableGames) {
        this.availableGames = availableGames;
    }

    public List<GameInfoDTO> getAvailableGames() {
        return availableGames;
    }
}
