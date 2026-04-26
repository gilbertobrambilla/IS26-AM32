package it.polimi.ingsw.am32.network.socket.messages;
import it.polimi.ingsw.am32.network.socket.server.SocketClientHandler;

public class SocketConnectMessage implements ClientToServerMessage {
    private final String nickname;
    public SocketConnectMessage(String nickname) { this.nickname = nickname; }

    @Override
    public void execute(SocketClientHandler handler) {
        handler.processConnect(nickname);
    }
}
