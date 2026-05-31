package it.polimi.ingsw.am32.network.socket.messages;

import it.polimi.ingsw.am32.network.socket.client.SocketServerProxy;
import java.io.Serializable;

/**
 * Interface implemented by messages sent from server to client.
 */
public interface ServerToClientMessage extends Serializable {

    /**
     * Message executed on the client side connection.
     *
     * @param connection the client side socket server proxy
     */
    void execute(SocketServerProxy connection);
}
