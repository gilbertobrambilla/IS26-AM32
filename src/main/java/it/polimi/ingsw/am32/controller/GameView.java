package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.dto.messages.game.events.*;

/**
 * Interface representing the game view.
 */
public interface GameView {

    // state and lifecycle events

    /**
     * The game start event is notified to the client.
     *
     * @param dto the game started event
     */
    void notifyGameStarted(GameStartedEvent dto);

    /**
     * The game state update is notified to the client.
     *
     * @param dto the game state event
     */
    void notifyGameState(GameStateEvent dto);

    /**
     * The game end event is notified to the client.
     *
     * @param resultDTO the game ended event
     */
    void notifyGameEnded(GameEndedEvent resultDTO);

    /**
     * A game-related error is notified to the client.
     *
     * @param dto the game error event
     */
    void notifyGameError(GameErrorEvent dto);


    // resilience events

    /**
     * The game suspension is notified to the client.
     */
    void notifyGameSuspended();

    /**
     * The game resumption is notified to the client.
     */
    void notifyGameResumed();

    /**
     * A player disconnection is notified to the client.
     *
     * @param dto the player disconnected event
     */
    void notifyPlayerDisconnected(PlayerDisconnectedEvent dto);

    /**
     * A player reconnection is notified to the client.
     *
     * @param dto the player reconnected event
     */
    void notifyPlayerReconnected(PlayerReconnectedEvent dto);


    // gameplay events

    /**
     * A totem placement is notified to the client.
     *
     * @param dto the totem placed event
     */
    void notifyTotemPlaced(TotemPlacedEvent dto);

    /**
     * An action resolution is notified to the client.
     *
     * @param dto the action resolved event
     */
    void notifyActionResolved(ActionResolvedEvent dto);

    /**
     * An extra acquisition resolution is notified to the client.
     *
     * @param dto the extra acquisition resolved event
     */
    void notifyExtraAcquisitionResolved(ExtraAcquisitionResolvedEvent dto);

    /**
     * An event resolution is notified to the client.
     *
     * @param dto the event resolved event
     */
    void notifyEventResolved(EventResolvedEvent dto);

    /**
     * A round end is notified to the client.
     *
     * @param dto the round ended event
     */
    void notifyRoundEnded(RoundEndedEvent dto);

    /**
     * A game phase change is notified to the client.
     *
     * @param dto the game phase changed event
     */
    void notifyGamePhaseChanged(GamePhaseChangedEvent dto);
}
