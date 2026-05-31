package it.polimi.ingsw.am32.controller.commands.lobby;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.controller.VirtualView;

/**
 * A command used to process the creation of a new game is represented.
 */
public class CreateGameCommand implements LobbyCommand {
    private final int numOfPlayers;

    /**
     * A new create game command is constructed with the desired number of players.
     *
     * @param numOfPlayers the number of players targeted for the new game
     */
    public CreateGameCommand(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
    }

    /**
     * The game creation request is executed on the lobby controller.
     *
     * @param lobby the lobby controller processing the request
     * @param view the virtual view associated with the connection
     * @param sessionNickname the current session nickname
     */
    @Override
    public void execute(LobbyController lobby, VirtualView view, String sessionNickname) {
        lobby.processCreateGame(sessionNickname, view, this.numOfPlayers);
    }
}
