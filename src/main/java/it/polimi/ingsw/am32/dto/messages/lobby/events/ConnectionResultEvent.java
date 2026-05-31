package it.polimi.ingsw.am32.dto.messages.lobby.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * A connection result event is represented.
 */
public class ConnectionResultEvent implements MessageDTO {
    private final boolean isSuccess;
    private final String message;
    private final String connectionID;

    /**
     * A connection result event is initialized with connection outcome data.
     *
     * @param isSuccess true if the connection was successful, false otherwise
     * @param message the text message associated with the result
     * @param connectionID the unique identifier for the connection
     */
    public ConnectionResultEvent(boolean isSuccess, String message, String connectionID) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.connectionID = connectionID;
    }

    /**
     * The connection success flag is returned.
     *
     * @return true if the connection was successful, false otherwise
     */
    public boolean isSuccess() {
        return isSuccess;
    }

    /**
     * The result message is returned.
     *
     * @return the result message
     */
    public String getMessage() {
        return message;
    }

    /**
     * The connection identifier is returned.
     *
     * @return the connection identifier
     */
    public String getConnectionID() {
        return connectionID;
    }

    /**
     * The visitor is accepted to dispatch the event.
     *
     * @param visitor the client message visitor
     */
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
