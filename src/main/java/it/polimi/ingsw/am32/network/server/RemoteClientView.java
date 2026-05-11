package it.polimi.ingsw.am32.network.server;

import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;
import it.polimi.ingsw.am32.controller.VirtualView;
import it.polimi.ingsw.am32.network.ClientNode;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;
import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;
import it.polimi.ingsw.am32.dto.messages.game.events.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Virtual view implementation acting as an adapter between the controller and the network.
 */
public class RemoteClientView implements VirtualView, NetworkNode {
    private final String nickname;
    private final ClientNode clientNode;
    private final Consumer<Boolean> onConnectionClosed;
    private final String connectionID;

    private final AtomicBoolean isRunning;
    private final AtomicLong lastPingReceived = new AtomicLong(System.currentTimeMillis());

    // thread pool is initialized with daemon threads
    private static final ScheduledExecutorService sharedScheduler = Executors.newScheduledThreadPool(50, r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        // thread is set as daemon
        t.setDaemon(true);
        return t;
    });
    private ScheduledFuture<?> pingTask;
    private ScheduledFuture<?> timeoutTask;
    private final AtomicBoolean isFirstPing = new AtomicBoolean(true);

    private static final long PING_PERIOD_MS = 1000;
    private static final long TIMEOUT_MS = 5000;
    private static final long INITIAL_TIMEOUT_MS = 10000;

    /**
     * Constructs a new virtual view implementation.
     *
     * @param nickname           The nickname of the player.
     * @param clientNode         The client network node.
     * @param connectionID       The unique identifier for this connection.
     * @param onConnectionClosed The callback to invoke when the connection drops.
     */
    private RemoteClientView(String nickname, ClientNode clientNode, String connectionID, Consumer<Boolean> onConnectionClosed) {
        this.nickname = nickname;
        this.clientNode = clientNode;
        this.connectionID = connectionID;
        this.onConnectionClosed = onConnectionClosed;
        this.isRunning = new AtomicBoolean(true);
    }

    /**
     * Creates and starts a new virtual view implementation.
     *
     * @param nickname           The nickname of the player.
     * @param clientNode         The client network node.
     * @param connectionID       The unique identifier for this connection.
     * @param onConnectionClosed The callback to invoke when the connection drops.
     * @return The created virtual view instance.
     */
    public static RemoteClientView create(String nickname, ClientNode clientNode, String connectionID, Consumer<Boolean> onConnectionClosed) {
        RemoteClientView view = new RemoteClientView(nickname, clientNode, connectionID, onConnectionClosed);
        // liveness routine is started
        view.startLivenessRoutine();
        return view;
    }

    @Override
    public String getConnectionID() {
        // connection id is returned
        return this.connectionID;
    }

    private void startLivenessRoutine() {
        // ping task is scheduled
        this.pingTask = sharedScheduler.scheduleAtFixedRate(() -> {
            if (isRunning.get()) {
                try {
                    // ping is sent to client node
                    clientNode.ping();
                } catch (NetworkException e) {
                    // ping failure is logged
                    LogManager.getInstance().log(LogLevel.ERROR, "[NETWORK] Ping failed for " + nickname + ". Client dropped.");
                    // disconnect is handled
                    handleDisconnect();
                }
            }
        }, 0, PING_PERIOD_MS, TimeUnit.MILLISECONDS);

        // timeout task is scheduled
        this.timeoutTask = sharedScheduler.scheduleAtFixedRate(() -> {
            if (isRunning.get()) {
                long currentTime = System.currentTimeMillis();

                long currentTimeout = isFirstPing.get() ? INITIAL_TIMEOUT_MS : TIMEOUT_MS;

                if (currentTime - lastPingReceived.get() > currentTimeout) {
                    // fatal timeout is logged
                    LogManager.getInstance().log(LogLevel.ERROR, "[SYSTEM] Fatal timeout triggered for nickname: " + nickname);
                    LogManager.getInstance().log(LogLevel.ERROR, "[SYSTEM] Time elapsed since last pong: " + (currentTime - lastPingReceived.get()) + "ms.");
                    handleDisconnect();
                }
            }
        }, TIMEOUT_MS, PING_PERIOD_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * Notifies that a ping was received from the client.
     */
    public void notifyPingReceived() {
        // last ping received timestamp is updated
        this.lastPingReceived.set(System.currentTimeMillis());

        if (isFirstPing.get()) {
            isFirstPing.set(false);
        }
    }

    /**
     * Stops the heartbeat and liveness routines.
     */
    public void stopHeartbeat() {
        if (pingTask != null) {
            // ping task is canceled
            pingTask.cancel(true);
        }
        if (timeoutTask != null) {
            // timeout task is canceled
            timeoutTask.cancel(true);
        }
    }

    @Override
    public void handleDisconnect() {
        if (isRunning.compareAndSet(true, false)) {
            // heartbeat is stopped
            stopHeartbeat();

            if (onConnectionClosed != null) {
                // connection closed callback is invoked for network drop
                onConnectionClosed.accept(true);
            }
        }
    }

    @Override
    public void destroy() {
        if (isRunning.compareAndSet(true, false)) {
            // heartbeat is stopped
            stopHeartbeat();

            if (onConnectionClosed != null) {
                // connection closed callback is invoked for clean closure
                onConnectionClosed.accept(false);
            }
        }
    }

    /**
     * Gets the nickname associated with this virtual view.
     *
     * @return The player's nickname.
     */
    public String getNickname() {
        // nickname is returned
        return this.nickname;
    }

    /**
     * Sends a generic message to the client catching potential network exceptions.
     *
     * @param message The generic message dto to be sent.
     */
    private void sendMessageToClient(MessageDTO message) {
        try {
            // generic message is pushed to client
            clientNode.sendMessage(message);
        } catch (NetworkException e) {
            // disconnect is triggered on failure
            handleDisconnect();
        }
    }

    @Override
    public void notifyConnectionResult(ConnectionResultEvent dto) {
        // connection result dto is created and pushed
        sendMessageToClient(dto);

        if (!dto.isSuccess()) {
            // heartbeat is stopped on failed connection
            stopHeartbeat();
            // network connection is kept open for retry
        }
    }

    @Override
    public void notifyLobbyState(AvailableLobbiesEvent dto) {
        // dto is pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyLobbyRoomUpdated(LobbyStateUpdatedEvent dto) {
        // dto is pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyLobbyError(LobbyErrorEvent dto) {
        // dto is pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyGameStarted(GameStartedEvent dto) {
        // dto is pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyGameState(GameStateEvent dto) {
        // dto is pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyGameEnded(GameEndedEvent dto) {
        // dto is pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyError(GameErrorEvent dto) {
        // dto is pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyGameSuspended() {
        // game suspended dto is created and pushed
        sendMessageToClient(new GameSuspendedEvent());
    }

    @Override
    public void notifyGameResumed() {
        // game resumed dto is created and pushed
        sendMessageToClient(new GameResumedEvent());
    }

    @Override
    public void notifyPlayerDisconnected(PlayerDisconnectedEvent dto) {
        // dto is pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyPlayerReconnected(PlayerReconnectedEvent dto) {
        // dto is pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyTotemPlaced(TotemPlacedEvent dto) {
        // dto is pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyActionResolved(ActionResolvedEvent dto) {
        // dto is pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyExtraAcquisitionResolved(ExtraAcquisitionResolvedEvent dto) {
        // dto is pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyEventResolved(EventResolvedEvent dto) {
        // dto is pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyRoundEnded(RoundEndedEvent dto) {
        // dto is pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyGamePhaseChanged(GamePhaseChangedEvent dto) {
        // dto is pushed to client
        sendMessageToClient(dto);
    }
}
