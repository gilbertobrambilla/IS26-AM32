package it.polimi.ingsw.am32.network.socket.messages;

import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.network.socket.server.SocketClientProxy;

/**
 * Message used to send a lobby command via socket.
 */
public class SocketLobbyMessage implements ClientToServerMessage {

    private final LobbyCommand command;

    /**
     * New socket lobby message constructed.
     * * @param command the lobby command
     */
    public SocketLobbyMessage(LobbyCommand command) {
        this.command = command;
    }

    @Override
    public void execute(SocketClientProxy handler) {
        handler.processLobbyCommand(command);
    }
}
