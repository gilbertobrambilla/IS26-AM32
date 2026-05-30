package it.polimi.ingsw.am32.model.exceptions;

import it.polimi.ingsw.am32.model.enums.GameErrorReason;

/**
 * Exception thrown when an invalid action is performed during a game.
 */
public class InvalidGameActionException extends RuntimeException {
    private final GameErrorReason reason;

    /**
     * Initializes the exception with the specified reason and message.
     *
     * @param reason the specific reason for the error
     * @param message the detail message
     */
    public InvalidGameActionException(GameErrorReason reason, String message) {
        super(message);
        this.reason = reason;
    }

    /**
     * Gets the reason associated with the game error.
     *
     * @return the error reason
     */
    public GameErrorReason getReason() {
        return this.reason;
    }
}
