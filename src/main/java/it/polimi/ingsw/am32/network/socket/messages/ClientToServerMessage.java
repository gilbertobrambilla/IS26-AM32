package it.polimi.ingsw.am32.network.socket.messages;

import it.polimi.ingsw.am32.network.socket.server.SocketClientProxy;
import java.io.Serializable;

/**
 * Interface implemented by messages sent from client to server.
 */
public interface ClientToServerMessage extends Serializable {

    /**
     * Message executed on the server side handler.
     *
     * @param handler the server side socket client proxy
     */
    void execute(SocketClientProxy handler);
}
