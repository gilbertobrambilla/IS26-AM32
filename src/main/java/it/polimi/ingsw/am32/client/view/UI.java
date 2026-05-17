package it.polimi.ingsw.am32.client.view;

import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;

/**
 * The visual interface contract for the client is defined.
 */
public interface UI {

    // network

    /**
     * Connection result is shown.
     *
     * @param event the connection result event
     */
    void showConnectionResult(ConnectionResultEvent event);

    /**
     * Login result is shown.
     *
     * @param event the login result event
     */
    void showLoginResult(LoginResultEvent event);

    /**
     * Fatal connection loss is shown.
     *
     * @param message the error message
     */
    void showConnectionLost(String message);

    /**
     * Voluntary quit confirmation is shown.
     */
    void showQuitConfirmation();


    // lobby

    /**
     * Available games list is updated.
     *
     * @param event the available lobbies event
     */
    void updateAvailableLobbies(AvailableLobbiesEvent event);

    /**
     * Lobby room state is updated.
     *
     * @param event the lobby room update event
     */
    void updateLobbyRoom(LobbyRoomUpdateEvent event);

    /**
     * Join game error is shown.
     *
     * @param event the lobby error event
     */
    void showLobbyError(LobbyErrorEvent event);


    // game lifecycle

    /**
     * Game start is shown.
     *
     * @param event the game started event
     */
    void showGameStarted(GameStartedEvent event);

    /**
     * Game state is restored.
     *
     * @param event the game state event
     */
    void restoreGameState(GameStateEvent event);

    /**
     * Game phase is updated.
     *
     * @param event the game phase changed event
     */
    void updateGamePhase(GamePhaseChangedEvent event);

    /**
     * Round end summary is shown.
     *
     * @param event the round-ended event
     */
    void showRoundEnded(RoundEndedEvent event);

    /**
     * Game results are shown.
     *
     * @param event the game ended event
     */
    void showGameEnded(GameEndedEvent event);


    // gameplay actions

    /**
     * Totem placement is shown.
     *
     * @param event the totem placed event
     */
    void showTotemPlaced(TotemPlacedEvent event);

    /**
     * Action resolution is shown.
     *
     * @param event the action resolved event
     */
    void showActionResolved(ActionResolvedEvent event);

    /**
     * Event resolution is shown.
     *
     * @param event the event resolved event
     */
    void showEventResolved(EventResolvedEvent event);

    /**
     * Extra acquisition is shown.
     *
     * @param event the extra acquisition resolved event
     */
    void showExtraAcquisition(ExtraAcquisitionResolvedEvent event);

    /**
     * Focus is set on a specific player in the user interface.
     *
     * @param nickname the nickname of the player to focus on, or null to clear focus
     */
    void showPlayerFocus(String nickname);


    // resilience and errors

    /**
     * Error message is shown.
     *
     * @param event the game error event
     */
    void showGameError(GameErrorEvent event);

    /**
     * Player disconnection is shown.
     *
     * @param event the player disconnected event
     */
    void showPlayerDisconnected(PlayerDisconnectedEvent event);

    /**
     * Player reconnection is shown.
     *
     * @param event the player reconnected event
     */
    void showPlayerReconnected(PlayerReconnectedEvent event);

    /**
     * Game suspension is shown.
     *
     * @param event the game suspended event
     */
    void showGameSuspended(GameSuspendedEvent event);

    /**
     * Game resumption is shown.
     *
     * @param event the game resumed event
     */
    void showGameResumed(GameResumedEvent event);
}
