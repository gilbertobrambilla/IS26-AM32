package it.polimi.ingsw.am32.network.socket.messages;
import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.network.socket.server.SocketClientProxy;

public class SocketGameMessage implements ClientToServerMessage {
    private final GameCommand command;

    public SocketGameMessage(GameCommand command) {
        this.command = command;
    }

    @Override
    public void execute(SocketClientProxy handler) {
        handler.processGameCommand(command);
    }
}
