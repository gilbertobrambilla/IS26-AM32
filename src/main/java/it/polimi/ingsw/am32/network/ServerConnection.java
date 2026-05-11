package it.polimi.ingsw.am32.network;

import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;

/**
 * Interface representing a generic connection to the server.
 */
public interface ServerConnection {
    /**
     * Connects the player to the server using the provided nickname.
     *
     * @param nickname The nickname of the player.
     */
    void connect(String nickname);

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
