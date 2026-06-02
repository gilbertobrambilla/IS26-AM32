package it.polimi.ingsw.am32.controller.commands.lobby;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.controller.VirtualView;

/**
 * A command used to process the request to join an existing game is represented.
 */
public class JoinGameCommand implements LobbyCommand {
    private final int gameId;

    /**
     * A new join game command is constructed with the targeted game id.
     *
     * @param gameId the id of the game to join
     */
    public JoinGameCommand(int gameId) {
        this.gameId = gameId;
    }

    /**
     * The game join request is executed on the lobby controller.
     *
     * @param lobby the lobby controller processing the request
     * @param view the virtual view associated with the connection
     * @param sessionNickname the current session nickname
     */
    @Override
    public void execute(LobbyController lobby, VirtualView view, String sessionNickname) {
        lobby.processJoinGame(sessionNickname, view, this.gameId);
    }
}
