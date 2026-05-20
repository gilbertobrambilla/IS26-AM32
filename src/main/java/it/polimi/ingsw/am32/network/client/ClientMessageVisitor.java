package it.polimi.ingsw.am32.network.client;

import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;

/**
 * Interface defining the visitor for client messages.
 */
public interface ClientMessageVisitor {

    // setup

    /**
     * Visits a connection result event.
     *
     * @param message the message to be visited
     */
    void visit(ConnectionResultEvent message);

    /**
     * Visits a login result event.
     *
     * @param message the message to be visited
     */
    void visit(LoginResultEvent message);

    // lobby management

    /**
     * Visits an available lobbies event.
     *
     * @param message the message to be visited
     */
    void visit(AvailableLobbiesEvent message);

    /**
     * Visits a lobby state-updated event.
     *
     * @param message the message to be visited
     */
    void visit(LobbyRoomUpdateEvent message);

    /**
     * Visits a lobby error event.
     *
     * @param message the message to be visited
     */
    void visit(LobbyErrorEvent message);

    // game flow

    /**
     * Visits a game started event.
     *
     * @param message the message to be visited
     */
    void visit(GameStartedEvent message);

    /**
     * Visits a game state event.
     *
     * @param message the message to be visited
     */
    void visit(GameStateEvent message);

    /**
     * Visits a game phase changed event.
     *
     * @param message the message to be visited
     */
    void visit(GamePhaseChangedEvent message);

    // game actions

    /**
     * Visits an action-resolved event.
     *
     * @param message the message to be visited
     */
    void visit(ActionResolvedEvent message);

    /**
     * Visits a totem-placed event.
     *
     * @param message the message to be visited
     */
    void visit(TotemPlacedEvent message);

    /**
     * Visits an extra acquisition resolved event.
     *
     * @param message the message to be visited
     */
    void visit(ExtraAcquisitionResolvedEvent message);

    /**
     * Visits an event resolved event.
     *
     * @param message the message to be visited
     */
    void visit(EventResolvedEvent message);

    // suspensions and reconnections

    /**
     * Visits a player-disconnected event.
     *
     * @param message the message to be visited
     */
    void visit(PlayerDisconnectedEvent message);

    /**
     * Visits a player-reconnected event.
     *
     * @param message the message to be visited
     */
    void visit(PlayerReconnectedEvent message);

    /**
     * Visits a game-suspended event.
     *
     * @param message the message to be visited
     */
    void visit(GameSuspendedEvent message);

    /**
     * Visits a game-resumed event.
     *
     * @param message the message to be visited
     */
    void visit(GameResumedEvent message);

    // end and errors

    /**
     * Visits a game error event.
     *
     * @param message the message to be visited
     */
    void visit(GameErrorEvent message);

    /**
     * Visits a round-ended event.
     *
     * @param message the message to be visited
     */
    void visit(RoundEndedEvent message);

    /**
     * Visits a game-ended event.
     *
     * @param message the message to be visited
     */
    void visit(GameEndedEvent message);

    /**
     * Visits a leaderboard event.
     *
     * @param message the message to be visited
     */
    void visit(LeaderboardEvent message);
}
