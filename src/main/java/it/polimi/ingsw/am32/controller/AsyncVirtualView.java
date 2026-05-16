package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Decorator class for the virtual view that handles outgoing messages asynchronously.
 */
public class AsyncVirtualView implements VirtualView, Runnable {
    private final VirtualView realView;
    private final BlockingQueue<Runnable> messageQueue;
    private volatile boolean running;
    private final Thread senderThread;

    /**
     * A new asynchronous virtual view is constructed.
     *
     * @param realView the original synchronous virtual view
     */
    public AsyncVirtualView(VirtualView realView) {
        this.realView = realView;
        this.messageQueue = new LinkedBlockingQueue<>();
        this.running = true;
        this.senderThread = new Thread(this);
        // thread is started
        this.senderThread.start();
    }

    /**
     * The main execution loop for the sender thread is defined.
     */
    @Override
    public void run() {
        try {
            while (this.running && !Thread.currentThread().isInterrupted()) {
                // task is retrieved from queue
                Runnable task = this.messageQueue.take();
                // task is executed
                task.run();
            }
        } catch (InterruptedException e) {
            // thread interruption is handled
            Thread.currentThread().interrupt();
        }
    }

    /**
     * The virtual view is destroyed closing the underlying connection cleanly.
     */
    @Override
    public void destroy() {
        // running flag is set to false
        this.running = false;
        // thread is interrupted
        this.senderThread.interrupt();
        // original view is destroyed
        this.realView.destroy();
    }

    /**
     * The unique identifier associated with this connection is retrieved.
     *
     * @return the unique connection id
     */
    @Override
    public String getConnectionID() {
        // connection id is retrieved directly
        return this.realView.getConnectionID();
    }

    /**
     * The connection result is notified to the client asynchronously.
     *
     * @param dto the connection result event
     */
    @Override
    public void notifyConnectionResult(ConnectionResultEvent dto) {
        // task is added to queue
        this.messageQueue.add(() -> this.realView.notifyConnectionResult(dto));
    }

    /**
     * The login result is notified to the client asynchronously.
     *
     * @param dto the login result event
     */
    @Override
    public void notifyLoginResult(LoginResultEvent dto) {
        // task is added to queue
        this.messageQueue.add(() -> this.realView.notifyLoginResult(dto));
    }

    /**
     * The available games list is notified to the client asynchronously.
     *
     * @param dto the available lobbies event
     */
    @Override
    public void notifyAvailableLobbies(AvailableLobbiesEvent dto) {
        // task is added to queue
        this.messageQueue.add(() -> this.realView.notifyAvailableLobbies(dto));
    }

    /**
     * The lobby room update is notified to the client asynchronously.
     *
     * @param dto the lobby state updated event
     */
    @Override
    public void notifyLobbyRoomUpdate(LobbyRoomUpdateEvent dto) {
        // task is added to queue
        this.messageQueue.add(() -> this.realView.notifyLobbyRoomUpdate(dto));
    }

    /**
     * A lobby-related error is notified to the client asynchronously.
     *
     * @param dto the lobby error event
     */
    @Override
    public void notifyLobbyError(LobbyErrorEvent dto) {
        // task is added to queue
        this.messageQueue.add(() -> this.realView.notifyLobbyError(dto));
    }

    /**
     * The game start event is notified to the client asynchronously.
     *
     * @param dto the game started event
     */
    @Override
    public void notifyGameStarted(GameStartedEvent dto) {
        // task is added to queue
        this.messageQueue.add(() -> this.realView.notifyGameStarted(dto));
    }

    /**
     * The game state update is notified to the client asynchronously.
     *
     * @param dto the game state event
     */
    @Override
    public void notifyGameState(GameStateEvent dto) {
        // task is added to queue
        this.messageQueue.add(() -> this.realView.notifyGameState(dto));
    }

    /**
     * The game end event is notified to the client asynchronously.
     *
     * @param resultDTO the game ended event
     */
    @Override
    public void notifyGameEnded(GameEndedEvent resultDTO) {
        // task is added to queue
        this.messageQueue.add(() -> this.realView.notifyGameEnded(resultDTO));
    }

    /**
     * A game-related error is notified to the client asynchronously.
     *
     * @param dto the game error event
     */
    @Override
    public void notifyGameError(GameErrorEvent dto) {
        // task is added to queue
        this.messageQueue.add(() -> this.realView.notifyGameError(dto));
    }

    /**
     * The game suspension is notified to the client asynchronously.
     */
    @Override
    public void notifyGameSuspended() {
        // task is added to queue
        this.messageQueue.add(this.realView::notifyGameSuspended);
    }

    /**
     * The game resumption is notified to the client asynchronously.
     */
    @Override
    public void notifyGameResumed() {
        // task is added to queue
        this.messageQueue.add(this.realView::notifyGameResumed);
    }

    /**
     * A player disconnection is notified to the client asynchronously.
     *
     * @param dto the player disconnected event
     */
    @Override
    public void notifyPlayerDisconnected(PlayerDisconnectedEvent dto) {
        // task is added to queue
        this.messageQueue.add(() -> this.realView.notifyPlayerDisconnected(dto));
    }

    /**
     * A player reconnection is notified to the client asynchronously.
     *
     * @param dto the player reconnected event
     */
    @Override
    public void notifyPlayerReconnected(PlayerReconnectedEvent dto) {
        // task is added to queue
        this.messageQueue.add(() -> this.realView.notifyPlayerReconnected(dto));
    }

    /**
     * A totem placement is notified to the client asynchronously.
     *
     * @param dto the totem placed event
     */
    @Override
    public void notifyTotemPlaced(TotemPlacedEvent dto) {
        // task is added to queue
        this.messageQueue.add(() -> this.realView.notifyTotemPlaced(dto));
    }

    /**
     * An action resolution is notified to the client asynchronously.
     *
     * @param dto the action resolved event
     */
    @Override
    public void notifyActionResolved(ActionResolvedEvent dto) {
        // task is added to queue
        this.messageQueue.add(() -> this.realView.notifyActionResolved(dto));
    }

    /**
     * An extra acquisition resolution is notified to the client asynchronously.
     *
     * @param dto the extra acquisition resolved event
     */
    @Override
    public void notifyExtraAcquisitionResolved(ExtraAcquisitionResolvedEvent dto) {
        // task is added to queue
        this.messageQueue.add(() -> this.realView.notifyExtraAcquisitionResolved(dto));
    }

    /**
     * An event resolution is notified to the client asynchronously.
     *
     * @param dto the event resolved event
     */
    @Override
    public void notifyEventResolved(EventResolvedEvent dto) {
        // task is added to queue
        this.messageQueue.add(() -> this.realView.notifyEventResolved(dto));
    }

    /**
     * A round end is notified to the client asynchronously.
     *
     * @param dto the round ended event
     */
    @Override
    public void notifyRoundEnded(RoundEndedEvent dto) {
        // task is added to queue
        this.messageQueue.add(() -> this.realView.notifyRoundEnded(dto));
    }

    /**
     * A game phase change is notified to the client asynchronously.
     *
     * @param dto the game phase changed event
     */
    @Override
    public void notifyGamePhaseChanged(GamePhaseChangedEvent dto) {
        // task is added to queue
        this.messageQueue.add(() -> this.realView.notifyGamePhaseChanged(dto));
    }
}
