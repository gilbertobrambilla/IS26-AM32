package it.polimi.ingsw.am32.network.socket.messages;

import it.polimi.ingsw.am32.network.socket.server.SocketClientProxy;

/**
 * A message used to request a login via socket is represented.
 */
public class SocketLoginMessage implements ClientToServerMessage {

    private final String nickname;

    /**
     * A new socket login message is constructed.
     *
     * @param nickname the requested nickname
     */
    public SocketLoginMessage(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void execute(SocketClientProxy handler) {
        handler.onLoginRequest(nickname);
    }
}
