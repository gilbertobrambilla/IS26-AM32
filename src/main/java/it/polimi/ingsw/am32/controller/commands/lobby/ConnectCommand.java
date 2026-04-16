package it.polimi.ingsw.am32.controller.commands.lobby;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.controller.VirtualView;

public class ConnectCommand implements LobbyCommand {
    private final String requestedNickname;

    public ConnectCommand(String requestedNickname) {
        this.requestedNickname = requestedNickname;
    }

    @Override
    public void execute(LobbyController lobby, VirtualView view, String sessionNickname) {
        // session nickname is expected to be null or irrelevant here, as the player is not authenticated yet
        lobby.connectPlayer(requestedNickname, view);
    }
}
