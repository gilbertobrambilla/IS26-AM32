package it.polimi.ingsw.am32.network.socket.messages;

import it.polimi.ingsw.am32.network.socket.client.SocketServerProxy;
import it.polimi.ingsw.am32.network.socket.server.SocketClientProxy;

/**
 * A message used for pinging between client and server via socket is represented.
 */
public class SocketPingMessage implements ClientToServerMessage, ServerToClientMessage {

    @Override
    public void execute(SocketClientProxy handler) {
        handler.onPingReceivedFromClient();
    }

    @Override
    public void execute(SocketServerProxy connection) {
        connection.onPingReceivedFromServer();
    }
}
