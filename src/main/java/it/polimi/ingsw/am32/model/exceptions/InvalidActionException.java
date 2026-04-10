package it.polimi.ingsw.am32.model.exceptions;

import it.polimi.ingsw.am32.model.enums.InvalidActionReason;

public class InvalidActionException extends RuntimeException {
    private final InvalidActionReason reason;

    public InvalidActionException(InvalidActionReason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public InvalidActionReason getReason() {
        return this.reason;
    }
}
