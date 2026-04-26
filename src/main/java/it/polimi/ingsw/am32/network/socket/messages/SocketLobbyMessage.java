package it.polimi.ingsw.am32.network.socket.messages;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.network.socket.server.SocketClientHandler;

public class SocketLobbyMessage implements ClientToServerMessage {
    private final LobbyCommand command;

    public SocketLobbyMessage(LobbyCommand command) {
        this.command = command;
    }

    @Override
    public void execute(SocketClientHandler handler) {
        handler.processLobbyCommand(command);
    }
}
