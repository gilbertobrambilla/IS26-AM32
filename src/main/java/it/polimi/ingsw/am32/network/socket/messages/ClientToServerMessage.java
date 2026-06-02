package it.polimi.ingsw.am32.network.socket.messages;

import it.polimi.ingsw.am32.network.socket.server.SocketClientProxy;
import java.io.Serializable;

/**
 * An interface for messages sent from client to server is represented.
 */
public interface ClientToServerMessage extends Serializable {

    /**
     * Message executed on the server side handler.
     *
     * @param handler the server side socket client proxy
     */
    void execute(SocketClientProxy handler);
}
