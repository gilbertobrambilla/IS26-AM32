package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.GameResultDTO;

public class GameEndedEvent implements MessageDTO {
    private final GameResultDTO finalResults;

    public GameEndedEvent(GameResultDTO finalResults) {
        this.finalResults = finalResults;
    }

    public GameResultDTO getFinalResults() {
        return finalResults;
    }
}
