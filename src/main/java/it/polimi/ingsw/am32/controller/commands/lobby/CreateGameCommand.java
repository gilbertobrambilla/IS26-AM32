package it.polimi.ingsw.am32.controller.commands.lobby;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.controller.VirtualView;

public class CreateGameCommand implements LobbyCommand {
    private final int numOfPlayers;

    public CreateGameCommand(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
    }

    @Override
    public void execute(LobbyController lobby, VirtualView view, String sessionNickname) {
        // game creation is requested passing the view and the session nickname
        lobby.createGame(sessionNickname, view, numOfPlayers);
    }
}
