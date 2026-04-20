package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.model.enums.InvalidActionReason;

public class InvalidActionEvent implements MessageDTO {
    private final String playerNickname;
    private final InvalidActionReason reason;
    private final String errorMessage;

    public InvalidActionEvent(String playerNickname, InvalidActionReason reason, String errorMessage) {
        this.playerNickname = playerNickname;
        this.reason = reason;
        this.errorMessage = errorMessage;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public InvalidActionReason getReason() {
        return reason;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
