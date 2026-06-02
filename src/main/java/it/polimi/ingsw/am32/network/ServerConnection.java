package it.polimi.ingsw.am32.network;

import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;

/**
 * A generic connection to the server is represented.
 */
public interface ServerConnection {

    /**
     * The physical network connection to the server is established.
     */
    void connect();

    /**
     * Login is requested using the provided nickname.
     *
     * @param nickname the nickname chosen by the player
     */
    void login(String nickname);

    /**
     * A lobby command is sent to the server.
     *
     * @param command the lobby command to be sent
     */
    void sendLobbyCommand(LobbyCommand command);

    /**
     * A game command is sent to the server.
     *
     * @param command the game command to be sent
     */
    void sendGameCommand(GameCommand command);
}
