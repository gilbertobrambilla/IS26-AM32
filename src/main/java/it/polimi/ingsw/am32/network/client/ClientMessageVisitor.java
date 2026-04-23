package it.polimi.ingsw.am32.network.client;

import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.AvailableGamesListEvent;
import it.polimi.ingsw.am32.dto.messages.lobby.events.ConnectionResultEvent;
import it.polimi.ingsw.am32.dto.messages.lobby.events.LobbyStateUpdatedEvent;

/**
 * Interface defining the visitor for client messages.
 */
public interface ClientMessageVisitor {

    /**
     * Visits an available games list event.
     *
     * @param message The message to be visited.
     */
    void visit(AvailableGamesListEvent message);

    /**
     * Visits a lobby state updated event.
     *
     * @param message The message to be visited.
     */
    void visit(LobbyStateUpdatedEvent message);

    /**
     * Visits an action resolved event.
     *
     * @param message The message to be visited.
     */
    void visit(ActionResolvedEvent message);

    /**
     * Visits an event resolved event.
     *
     * @param message The message to be visited.
     */
    void visit(EventResolvedEvent message);

    /**
     * Visits an extra acquisition resolved event.
     *
     * @param message The message to be visited.
     */
    void visit(ExtraAcquisitionResolvedEvent message);

    /**
     * Visits a game ended event.
     *
     * @param message The message to be visited.
     */
    void visit(GameEndedEvent message);

    /**
     * Visits a game phase changed event.
     *
     * @param message The message to be visited.
     */
    void visit(GamePhaseChangedEvent message);

    /**
     * Visits a game started event.
     *
     * @param message The message to be visited.
     */
    void visit(GameStartedEvent message);

    /**
     * Visits an invalid action event.
     *
     * @param message The message to be visited.
     */
    void visit(InvalidActionEvent message);

    /**
     * Visits a player disconnected event.
     *
     * @param message The message to be visited.
     */
    void visit(PlayerDisconnectedEvent message);

    /**
     * Visits a player reconnected event.
     *
     * @param message The message to be visited.
     */
    void visit(PlayerReconnectedEvent message);

    /**
     * Visits a round ended event.
     *
     * @param message The message to be visited.
     */
    void visit(RoundEndedEvent message);

    /**
     * Visits a totem placed event.
     *
     * @param message The message to be visited.
     */
    void visit(TotemPlacedEvent message);

    void visit(ConnectionResultEvent connectionResultEvent);

    void visit(GameSuspendedEvent gameSuspendedEvent);

    void visit(GameResumedEvent gameResumedEvent);

    void visit(GameStateEvent gameStateEvent);
}
