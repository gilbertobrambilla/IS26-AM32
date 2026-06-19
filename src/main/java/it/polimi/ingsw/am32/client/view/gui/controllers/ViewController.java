package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;

/**
 * Optional GUI event hooks for scene controllers are defined.
 */
public interface ViewController {

    /**
     * Connection results are handled.
     *
     * @param event the connection result event
     */
    default void onConnectionResult(ConnectionResultEvent event) {}

    /**
     * Disconnection messages are handled.
     *
     * @param message the disconnection message
     */
    default void onDisconnection(String message) {}

    /**
     * Login results are handled.
     *
     * @param event the login result event
     */
    default void onLoginResult(LoginResultEvent event) {}

    /**
     * Lobby errors are handled.
     *
     * @param event the lobby error event
     */
    default void onLobbyError(LobbyErrorEvent event) {}

    /**
     * Available lobbies are handled.
     *
     * @param event the available lobbies event
     */
    default void onAvailableLobbies(AvailableLobbiesEvent event) {}

    /**
     * Lobby room updates are handled.
     *
     * @param event the lobby room update event
     */
    default void onLobbyRoomUpdate(LobbyRoomUpdateEvent event) {}

    /**
     * Game start events are handled.
     *
     * @param event the game started event
     */
    default void onGameStarted(GameStartedEvent event) {}

    /**
     * Restored game states are handled.
     *
     * @param event the game state event
     */
    default void onGameStateRestored(GameStateEvent event) {}

    /**
     * Game phase updates are handled.
     *
     * @param event the game phase changed event
     */
    default void onGamePhaseUpdated(GamePhaseChangedEvent event) {}

    /**
     * Round summaries are handled.
     *
     * @param event the round-ended event
     */
    default void onRoundSummary(RoundEndedEvent event) {}

    /**
     * Game results are handled.
     *
     * @param event the game ended event
     */
    default void onGameResults(GameEndedEvent event) {}

    /**
     * Leaderboard results are handled.
     *
     * @param event the leaderboard event
     */
    default void onLeaderboard(LeaderboardEvent event) {}


    /**
     * Totem placement updates are handled.
     *
     * @param event the totem placed event
     */
    default void onTotemPlaced(TotemPlacedEvent event) {}

    /**
     * Action resolution updates are handled.
     *
     * @param event the action resolved event
     */
    default void onActionResolved(ActionResolvedEvent event) {}

    /**
     * Event resolution updates are handled.
     *
     * @param event the event resolved event
     */
    default void onEventResolved(EventResolvedEvent event) {}

    /**
     * Extra acquisition updates are handled.
     *
     * @param event the extra acquisition resolved event
     */
    default void onExtraAcquisition(ExtraAcquisitionResolvedEvent event) {}

    /**
     * Opponent focus requests are handled.
     *
     * @param nickname the focused player nickname, or null to clear focus
     */
    default void onPlayerFocus(String nickname) {}

    /**
     * Game errors are handled.
     *
     * @param event the game error event
     */
    default void onError(GameErrorEvent event) {}

    /**
     * Player disconnection events are handled.
     *
     * @param event the player disconnected event
     */
    default void onPlayerDisconnected(PlayerDisconnectedEvent event) {}

    /**
     * Player reconnection events are handled.
     *
     * @param event the player reconnected event
     */
    default void onPlayerReconnected(PlayerReconnectedEvent event) {}

    /**
     * Game suspension events are handled.
     *
     * @param event the game suspended event
     */
    default void onGameSuspended(GameSuspendedEvent event) {}

    /**
     * Game resume events are handled.
     *
     * @param event the game resumed event
     */
    default void onGameResumed(GameResumedEvent event) {}
}
