package it.polimi.ingsw.am32.dto.messages.lobby.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * Event containing the result of a network connection attempt.
 */
public class ConnectionResultEvent implements MessageDTO {
    private final boolean isSuccess;
    private final String message;
    private final String connectionID;

    /**
     * Constructs a new connection result event.
     *
     * @param isSuccess true if the connection was successful, false otherwise
     * @param message the text message associated with the result
     * @param connectionID the unique identifier for the connection
     */
    public ConnectionResultEvent(boolean isSuccess, String message, String connectionID) {
        // fields are initialized
        this.isSuccess = isSuccess;
        this.message = message;
        this.connectionID = connectionID;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public String getConnectionID() {
        return connectionID;
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
