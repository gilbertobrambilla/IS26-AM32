package it.polimi.ingsw.am32.model.exceptions;

import it.polimi.ingsw.am32.model.enums.GameErrorReason;

public class InvalidGameActionException extends RuntimeException {
    private final GameErrorReason reason;

    public InvalidGameActionException(GameErrorReason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public GameErrorReason getReason() {
        return this.reason;
    }
}
