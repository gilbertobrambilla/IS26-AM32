package it.polimi.ingsw.am32.network.socket.messages;

import it.polimi.ingsw.am32.network.socket.server.SocketClientProxy;

/**
 * Message used to request a new socket connection setup.
 */
public class SocketConnectMessage implements ClientToServerMessage {

    /**
     * New socket connect message constructed.
     */
    public SocketConnectMessage() {
    }

    @Override
    public void execute(SocketClientProxy handler) {
        handler.processConnect();
    }
}
