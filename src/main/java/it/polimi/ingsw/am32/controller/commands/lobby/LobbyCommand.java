package it.polimi.ingsw.am32.controller.commands.lobby;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.controller.VirtualView;

import java.io.Serializable;

/**
 * Interface representing a command executed in the lobby.
 */
public interface LobbyCommand extends Serializable {

    /**
     * The command is executed on the lobby controller.
     *
     * @param lobby the lobby controller processing the request
     * @param view the virtual view associated with the connection
     * @param sessionNickname the session nickname
     */
    void execute(LobbyController lobby, VirtualView view, String sessionNickname);
}
