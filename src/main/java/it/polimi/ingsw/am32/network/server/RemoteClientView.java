package it.polimi.ingsw.am32.network.server;

import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;
import it.polimi.ingsw.am32.controller.VirtualView;
import it.polimi.ingsw.am32.network.ClientNode;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;
import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;
import it.polimi.ingsw.am32.dto.messages.game.events.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Virtual view implementation used as an adapter between the controller and the network.
 */
public class RemoteClientView implements VirtualView, NetworkNode {

    private static final long PING_PERIOD_MS = 1000;
    private static final long TIMEOUT_MS = 5000;
    private static final long INITIAL_TIMEOUT_MS = 10000;

    // thread pool initialized with daemon threads
    private static final ScheduledExecutorService sharedScheduler = Executors.newScheduledThreadPool(50, r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        // thread set as daemon
        t.setDaemon(true);
        return t;
    });

    private final ClientNode clientNode;
    private final Consumer<Boolean> onConnectionClosed;
    private final String connectionID;
    private final AtomicBoolean isRunning;
    private final AtomicLong lastPingReceived = new AtomicLong(System.currentTimeMillis());
    private final AtomicBoolean isFirstPing = new AtomicBoolean(true);
    private final BlockingQueue<MessageDTO> messageQueue;

    private ScheduledFuture<?> pingTask;
    private ScheduledFuture<?> timeoutTask;
    private Future<?> messageConsumerTask;

    /**
     * New virtual view implementation constructed.
     *
     * @param clientNode the client network node
     * @param connectionID the unique identifier for this connection
     * @param onConnectionClosed the callback to invoke when the connection drops
     */
    private RemoteClientView(ClientNode clientNode, String connectionID, Consumer<Boolean> onConnectionClosed) {
        this.clientNode = clientNode;
        this.connectionID = connectionID;
        this.onConnectionClosed = onConnectionClosed;
        this.isRunning = new AtomicBoolean(true);
        this.messageQueue = new LinkedBlockingQueue<>();
    }

    /**
     * New virtual view implementation created and started.
     *
     * @param clientNode the client network node
     * @param connectionID the unique identifier for this connection
     * @param onConnectionClosed the callback to invoke when the connection drops
     * @return the created virtual view instance
     */
    public static RemoteClientView create(ClientNode clientNode, String connectionID, Consumer<Boolean> onConnectionClosed) {
        RemoteClientView view = new RemoteClientView(clientNode, connectionID, onConnectionClosed);
        // liveness routine started immediately for all connections
        view.startClientHeartbeat();
        return view;
    }

    @Override
    public String getConnectionID() {
        // connection id returned
        return this.connectionID;
    }

    @Override
    public void notifyConnectionResult(ConnectionResultEvent dto) {
        // connection result dto pushed to the queue
        sendMessageToClient(dto);
    }

    @Override
    public void notifyLoginResult(LoginResultEvent dto) {
        // login result dto pushed to the queue
        sendMessageToClient(dto);
    }

    @Override
    public void notifyAvailableLobbies(AvailableLobbiesEvent dto) {
        // dto pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyLobbyRoomUpdate(LobbyRoomUpdateEvent dto) {
        // dto pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyLobbyError(LobbyErrorEvent dto) {
        // dto pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyGameStarted(GameStartedEvent dto) {
        // dto pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyGameState(GameStateEvent dto) {
        // dto pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyGamePhaseChanged(GamePhaseChangedEvent dto) {
        // dto pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyActionResolved(ActionResolvedEvent dto) {
        // dto pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyTotemPlaced(TotemPlacedEvent dto) {
        // dto pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyExtraAcquisitionResolved(ExtraAcquisitionResolvedEvent dto) {
        // dto pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyEventResolved(EventResolvedEvent dto) {
        // dto pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyPlayerDisconnected(PlayerDisconnectedEvent dto) {
        // dto pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyPlayerReconnected(PlayerReconnectedEvent dto) {
        // dto pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyGameSuspended() {
        // game suspended dto created and pushed
        sendMessageToClient(new GameSuspendedEvent());
    }

    @Override
    public void notifyGameResumed() {
        // game resumed dto created and pushed
        sendMessageToClient(new GameResumedEvent());
    }

    @Override
    public void notifyGameError(GameErrorEvent dto) {
        // dto pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyRoundEnded(RoundEndedEvent dto) {
        // dto pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void notifyGameEnded(GameEndedEvent dto) {
        // dto pushed to client
        sendMessageToClient(dto);
    }

    @Override
    public void handleDisconnect() {
        if (isRunning.compareAndSet(true, false)) {
            // heartbeat stopped
            stopHeartbeat();

            if (onConnectionClosed != null) {
                // connection closed callback invoked for network drop
                onConnectionClosed.accept(true);
            }
        }
    }

    @Override
    public void destroy() {
        if (isRunning.compareAndSet(true, false)) {
            // heartbeat stopped
            stopHeartbeat();

            if (onConnectionClosed != null) {
                // connection closed callback invoked for clean closure
                onConnectionClosed.accept(false);
            }
        }
    }

    /**
     * Ping received from the client processed.
     */
    public void onPingReceivedFromClient() {
        // last ping received timestamp updated
        this.lastPingReceived.set(System.currentTimeMillis());

        if (isFirstPing.get()) {
            isFirstPing.set(false);
        }
    }

    /**
     * Heartbeat and liveness routines stopped, and the message queue cleared.
     */
    public void stopHeartbeat() {
        if (pingTask != null) {
            // ping task canceled
            pingTask.cancel(true);
        }
        if (timeoutTask != null) {
            // timeout task canceled
            timeoutTask.cancel(true);
        }
        if (messageConsumerTask != null) {
            // consumer task canceled
            messageConsumerTask.cancel(true);
        }
        if (messageQueue != null) {
            // queue cleared
            messageQueue.clear();
        }
    }

    /**
     * Liveness routine started.
     */
    private void startClientHeartbeat() {
        // ping task scheduled
        this.pingTask = sharedScheduler.scheduleAtFixedRate(() -> {
            if (isRunning.get()) {
                try {
                    // ping sent to client node
                    clientNode.sendPingToClient();
                } catch (NetworkException e) {
                    // ping failure logged
                    LogManager.getInstance().log(LogLevel.ERROR, "[NETWORK] Ping failed for connection: " + connectionID + ". Client dropped.");
                    // disconnect handled
                    handleDisconnect();
                }
            }
        }, 0, PING_PERIOD_MS, TimeUnit.MILLISECONDS);

        // timeout task scheduled
        this.timeoutTask = sharedScheduler.scheduleAtFixedRate(() -> {
            if (isRunning.get()) {
                long currentTime = System.currentTimeMillis();
                long currentTimeout = isFirstPing.get() ? INITIAL_TIMEOUT_MS : TIMEOUT_MS;

                if (currentTime - lastPingReceived.get() > currentTimeout) {
                    // fatal timeout logged
                    LogManager.getInstance().log(LogLevel.ERROR, "[SYSTEM] Fatal timeout triggered for connection: " + connectionID);
                    LogManager.getInstance().log(LogLevel.ERROR, "[SYSTEM] Time elapsed since last pong: " + (currentTime - lastPingReceived.get()) + "ms.");
                    handleDisconnect();
                }
            }
        }, TIMEOUT_MS, PING_PERIOD_MS, TimeUnit.MILLISECONDS);

        // consumer task submitted
        this.messageConsumerTask = sharedScheduler.submit(this::messageConsumerLoop);
    }

    /**
     * Messages consumed from the queue and pushed to the client.
     */
    private void messageConsumerLoop() {
        while (isRunning.get()) {
            try {
                // message taken from queue
                MessageDTO message = messageQueue.take();
                // message pushed to client
                clientNode.sendMessage(message);
            } catch (InterruptedException e) {
                // thread interrupted
                Thread.currentThread().interrupt();
                break;
            } catch (NetworkException e) {
                // disconnect triggered by network failure
                handleDisconnect();
                break;
            }
        }
    }

    /**
     * Generic message enqueued to be sent to the client asynchronously.
     *
     * @param message the generic message dto to be sent
     */
    private void sendMessageToClient(MessageDTO message) {
        // message added to queue
        messageQueue.offer(message);
    }
}
