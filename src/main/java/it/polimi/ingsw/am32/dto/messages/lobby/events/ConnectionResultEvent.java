package it.polimi.ingsw.am32.dto.messages.lobby.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * Event containing the result of a connection attempt.
 */
public class ConnectionResultEvent implements MessageDTO {
    private final boolean isSuccess;
    private final String message;
    private final boolean isReconnecting;
    private final String connectionID;

    /**
     * Constructs a new connection result event.
     *
     * @param isSuccess true if the connection was successful, false otherwise.
     * @param message the text message associated with the result.
     * @param isReconnecting true if the client is reconnecting to an ongoing game, false otherwise.
     * @param connectionID the unique identifier for the connection.
     */
    public ConnectionResultEvent(boolean isSuccess, String message, boolean isReconnecting, String connectionID) {
        // fields are initialized
        this.isSuccess = isSuccess;
        this.message = message;
        this.isReconnecting = isReconnecting;
        this.connectionID = connectionID;
    }

    /**
     * Gets the success status of the connection.
     *
     * @return true if successful, false otherwise.
     */
    public boolean isSuccess() {
        // success status is returned
        return isSuccess;
    }

    /**
     * Gets the message associated with the connection result.
     *
     * @return the text message.
     */
    public String getMessage() {
        // message is returned
        return message;
    }

    /**
     * Gets the reconnecting status.
     *
     * @return true if reconnecting, false otherwise.
     */
    public boolean isReconnecting() {
        // reconnecting status is returned
        return isReconnecting;
    }

    /**
     * Gets the connection identifier.
     *
     * @return the connection id string.
     */
    public String getConnectionID() {
        // connection id is returned
        return connectionID;
    }

    /**
     * Accepts the visitor for double dispatch.
     *
     * @param visitor the client message visitor.
     */
    @Override
    public void accept(ClientMessageVisitor visitor) {
        // visitor is accepted
        visitor.visit(this);
    }
}
