package it.polimi.ingsw.am32.model.exceptions;

import it.polimi.ingsw.am32.model.enums.GameErrorReason;

public class InvalidActionException extends RuntimeException {
    private final GameErrorReason reason;

    public InvalidActionException(GameErrorReason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public GameErrorReason getReason() {
        return this.reason;
    }
}
