package it.polimi.ingsw.am32.controller.commands.lobby;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.controller.VirtualView;

/**
 * Command used to process a new anonymous network connection.
 */
public class ConnectCommand implements LobbyCommand {

    /**
     * A new connection command is constructed.
     */
    public ConnectCommand() {
    }

    /**
     * The connection request is executed on the lobby controller.
     *
     * @param lobby the lobby controller processing the request
     * @param view the virtual view associated with the connection
     * @param sessionNickname the session nickname, expected to be null at this stage
     */
    @Override
    public void execute(LobbyController lobby, VirtualView view, String sessionNickname) {
        lobby.processConnection(view);
    }
}
