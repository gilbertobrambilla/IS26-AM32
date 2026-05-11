package it.polimi.ingsw.am32.network.socket.messages;
import it.polimi.ingsw.am32.network.socket.client.SocketServerProxy;
import it.polimi.ingsw.am32.network.socket.server.SocketClientProxy;

public class SocketPingMessage implements ClientToServerMessage, ServerToClientMessage {
    @Override
    public void execute(SocketClientProxy handler) {
        handler.processPing();
    }

    @Override
    public void execute(SocketServerProxy connection) {
        connection.processPing();
    }
}
