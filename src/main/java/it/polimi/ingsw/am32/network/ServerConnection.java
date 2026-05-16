package it.polimi.ingsw.am32.network;

import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;

/**
 * Interface representing a generic connection to the server.
 */
public interface ServerConnection {

    /**
     * Establishes the physical network connection to the server.
     */
    void connect();

    /**
     * Login is requested using the provided nickname.
     *
     * @param nickname the nickname chosen by the player
     */
    void login(String nickname);

    /**
     * Sends a lobby command to the server.
     *
     * @param command The lobby command to be sent.
     */
    void sendLobbyCommand(LobbyCommand command);

    /**
     * Sends a game command to the server.
     *
     * @param command The game command to be sent.
     */
    void sendGameCommand(GameCommand command);
}
