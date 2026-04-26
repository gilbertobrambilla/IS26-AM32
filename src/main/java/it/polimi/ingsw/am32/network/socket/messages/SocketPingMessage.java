package it.polimi.ingsw.am32.network.socket.messages;
import it.polimi.ingsw.am32.network.socket.client.SocketServerConnection;
import it.polimi.ingsw.am32.network.socket.server.SocketClientHandler;

public class SocketPingMessage implements ClientToServerMessage, ServerToClientMessage {
    @Override
    public void execute(SocketClientHandler handler) { handler.processPing(); }
    @Override
    public void execute(SocketServerConnection connection) { connection.processPing(); }
}
