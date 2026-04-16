package it.polimi.ingsw.am32.controller.commands.lobby;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.controller.VirtualView;

public class JoinGameCommand implements LobbyCommand {
    private final int gameId;

    public JoinGameCommand(int gameId) {
        this.gameId = gameId;
    }

    @Override
    public void execute(LobbyController lobby, VirtualView view, String sessionNickname) {
        // game join is requested using the targeted game id and the authenticated session nickname
        lobby.joinGame(sessionNickname, gameId);
    }
}
