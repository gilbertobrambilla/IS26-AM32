package it.polimi.ingsw.am32.network.socket.messages;

import it.polimi.ingsw.am32.network.socket.server.SocketClientProxy;

/**
 * Message used to request a login via socket.
 */
public class SocketLoginMessage implements ClientToServerMessage {

    private final String nickname;

    /**
     * New socket login message constructed.
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
