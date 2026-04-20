package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.model.enums.GamePhase;

public class GamePhaseChangedEvent implements MessageDTO {
    private final GamePhase newPhase;
    private final String nextPlayerNickname;

    public GamePhaseChangedEvent(GamePhase newPhase, String nextPlayerNickname) {
        this.newPhase = newPhase;
        this.nextPlayerNickname = nextPlayerNickname;
    }

    public GamePhase getNewPhase() {
        return newPhase;
    }

    public String getNextPlayerNickname() {
        return nextPlayerNickname;
    }
}
