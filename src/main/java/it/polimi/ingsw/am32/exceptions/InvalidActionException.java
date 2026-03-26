package it.polimi.ingsw.am32.exceptions;

import it.polimi.ingsw.am32.enums.InvalidActionReason;

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
