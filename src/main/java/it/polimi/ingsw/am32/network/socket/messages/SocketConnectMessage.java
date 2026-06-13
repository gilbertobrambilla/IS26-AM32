package it.polimi.ingsw.am32.network.socket.messages;

import it.polimi.ingsw.am32.network.socket.server.SocketClientProxy;

/**
 * A message used to request a new socket connection setup is represented.
 */
public class SocketConnectMessage implements ClientToServerMessage {

    /**
     * A new socket connect message is constructed.
     */
    public SocketConnectMessage() {
    }

    @Override
    public void execute(SocketClientProxy handler) {
        handler.processConnect();
    }
}
