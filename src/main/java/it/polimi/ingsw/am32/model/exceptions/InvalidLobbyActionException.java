package it.polimi.ingsw.am32.model.exceptions;

import it.polimi.ingsw.am32.model.enums.LobbyErrorReason;

/**
 * Exception thrown when an invalid action is performed within a lobby.
 */
public class InvalidLobbyActionException extends RuntimeException {
    private final LobbyErrorReason reason;

    /**
     * Initializes the exception with the specified reason and message.
     *
     * @param reason the specific reason for the error
     * @param message the detail message
     */
    public InvalidLobbyActionException(LobbyErrorReason reason, String message) {
        super(message);
        this.reason = reason;
    }

    /**
     * Gets the reason associated with the lobby error.
     *
     * @return the error reason
     */
    public LobbyErrorReason getReason() {
        return this.reason;
    }
}
