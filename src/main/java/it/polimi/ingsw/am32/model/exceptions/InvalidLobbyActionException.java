package it.polimi.ingsw.am32.model.exceptions;

import it.polimi.ingsw.am32.model.enums.LobbyErrorReason;

public class InvalidLobbyActionException extends RuntimeException {
    private final LobbyErrorReason reason;

    public InvalidLobbyActionException(LobbyErrorReason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public LobbyErrorReason getReason() {
        return this.reason;
    }
}
