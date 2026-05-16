package it.polimi.ingsw.am32.controller.commands.lobby;

import it.polimi.ingsw.am32.controller.LobbyController;
import it.polimi.ingsw.am32.controller.VirtualView;

/**
 * Command used to process the authentication request of a connected client.
 */
public class LoginCommand implements LobbyCommand {
    private final String requestedNickname;

    /**
     * A new login command is constructed with the desired nickname.
     *
     * @param requestedNickname the nickname requested by the client
     */
    public LoginCommand(String requestedNickname) {
        this.requestedNickname = requestedNickname;
    }

    /**
     * The login request is executed on the lobby controller.
     *
     * @param lobby the lobby controller processing the request
     * @param view the virtual view associated with the connection
     * @param sessionNickname the current session nickname, expected to be null
     */
    @Override
    public void execute(LobbyController lobby, VirtualView view, String sessionNickname) {
        lobby.processLogin(this.requestedNickname, view);
    }
}
