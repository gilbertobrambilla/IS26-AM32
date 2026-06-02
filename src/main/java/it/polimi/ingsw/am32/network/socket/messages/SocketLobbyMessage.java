package it.polimi.ingsw.am32.network.socket.messages;

import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.network.socket.server.SocketClientProxy;

/**
 * A message used to send a lobby command via socket is represented.
 */
public class SocketLobbyMessage implements ClientToServerMessage {

    private final LobbyCommand command;

    /**
     * A new socket lobby message is constructed.
     *
     * @param command the lobby command
     */
    public SocketLobbyMessage(LobbyCommand command) {
        this.command = command;
    }

    @Override
    public void execute(SocketClientProxy handler) {
        handler.processLobbyCommand(command);
    }
}
