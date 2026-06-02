package it.polimi.ingsw.am32.network.socket.messages;

import it.polimi.ingsw.am32.network.socket.client.SocketServerProxy;
import java.io.Serializable;

/**
 * An interface for messages sent from server to client is represented.
 */
public interface ServerToClientMessage extends Serializable {

    /**
     * Message executed on the client side connection.
     *
     * @param connection the client side socket server proxy
     */
    void execute(SocketServerProxy connection);
}
