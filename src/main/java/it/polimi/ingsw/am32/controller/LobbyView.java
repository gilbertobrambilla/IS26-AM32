package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.dto.messages.lobby.events.*;

/**
 * The lobby view is represented.
 */
public interface LobbyView {

    /**
     * The connection result is notified to the client.
     *
     * @param dto the connection result event
     */
    void notifyConnectionResult(ConnectionResultEvent dto);

    /**
     * The login result is notified to the client.
     *
     * @param dto the login result event
     */
    void notifyLoginResult(LoginResultEvent dto);

    /**
     * The available games list is notified to the client.
     *
     * @param dto the available lobbies event
     */
    void notifyAvailableLobbies(AvailableLobbiesEvent dto);

    /**
     * The lobby room update is notified to the client.
     *
     * @param dto the lobby state updated event
     */
    void notifyLobbyRoomUpdate(LobbyRoomUpdateEvent dto);

    /**
     * A lobby-related error is notified to the client.
     *
     * @param dto the lobby error event
     */
    void notifyLobbyError(LobbyErrorEvent dto);
}
