package it.polimi.ingsw.am32.network.socket.messages;
import it.polimi.ingsw.am32.network.socket.server.SocketClientProxy;

public class SocketConnectMessage implements ClientToServerMessage {
    private final String nickname;
    public SocketConnectMessage(String nickname) { this.nickname = nickname; }

    @Override
    public void execute(SocketClientProxy handler) {
        handler.processConnect(nickname);
    }
}
