package it.polimi.ingsw.am32.dto.messages.lobby.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * Event containing the result of an authentication attempt.
 */
public class LoginResultEvent implements MessageDTO {
    private final boolean isSuccess;
    private final String message;
    private final boolean isReconnecting;

    /**
     * Constructs a new authentication result event.
     *
     * @param isSuccess true if the authentication was successful, false otherwise
     * @param message the text message associated with the result
     * @param isReconnecting true if the client is reconnecting to an ongoing game, false otherwise
     */
    public LoginResultEvent(boolean isSuccess, String message, boolean isReconnecting) {
        // fields are initialized
        this.isSuccess = isSuccess;
        this.message = message;
        this.isReconnecting = isReconnecting;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public boolean isReconnecting() {
        return isReconnecting;
    }

    /**
     * Accepts the visitor for double dispatch.
     *
     * @param visitor the client message visitor
     */
    @Override
    public void accept(ClientMessageVisitor visitor) {
        // visitor is accepted
        visitor.visit(this);
    }
}
