package it.polimi.ingsw.am32.network.client;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.NetworkAction;
import it.polimi.ingsw.am32.network.ServerConnection;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Client side virtual server representation.
 */
public class VirtualServer implements ServerConnection {
    private final ServerNode serverNode;
    private final Runnable onServerCrashCallback;
    private final AtomicBoolean isConnected;

    private final AtomicReference<ClientController> clientController = new AtomicReference<>();
    private final AtomicLong lastPingReceived = new AtomicLong(System.currentTimeMillis());

    // thread pool is initialized with daemon threads
    private static final ScheduledExecutorService sharedScheduler = Executors.newScheduledThreadPool(2, r -> {
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
     * Constructs a virtual server instance.
     *
     * @param serverNode The server node representation.
     * @param onServerCrashCallback The callback to execute on server crash.
     */
    private VirtualServer(ServerNode serverNode, Runnable onServerCrashCallback) {
        this.serverNode = serverNode;
        this.onServerCrashCallback = onServerCrashCallback;
        this.isConnected = new AtomicBoolean(true);
    }

    /**
     * Creates a new virtual server instance.
     *
     * @param serverNode The server node representation.
     * @param onServerCrashCallback The callback to execute on server crash.
     * @return The created virtual server instance.
     */
    public static VirtualServer create(ServerNode serverNode, Runnable onServerCrashCallback) {
        return new VirtualServer(serverNode, onServerCrashCallback);
    }

    /**
     * Sets the client controller reference.
     *
     * @param clientController The client controller to be set.
     */
    public void setClientController(ClientController clientController) {
        // client controller reference is updated
        this.clientController.set(clientController);
    }

    /**
     * Starts the liveness routine.
     */
    public void startLivenessRoutine() {
        // ping task is scheduled
        this.pingTask = sharedScheduler.scheduleAtFixedRate(() -> {
            if (isConnected.get()) {
                // ping is sent to server
                tryInvoke(serverNode::ping);
            }
        }, 0, PING_PERIOD_MS, TimeUnit.MILLISECONDS);

        // timeout task is scheduled
        this.timeoutTask = sharedScheduler.scheduleAtFixedRate(() -> {
            if (isConnected.get()) {
                long currentTime = System.currentTimeMillis();

                // current timeout is calculated
                long currentTimeout = isFirstPing.get() ? INITIAL_TIMEOUT_MS : TIMEOUT_MS;

                if (currentTime - lastPingReceived.get() > currentTimeout) {
                    // server disconnect is handled
                    handleServerDisconnect();
                }
            }
        }, TIMEOUT_MS, PING_PERIOD_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * Handles the sudden disconnection of the server.
     */
    public void handleServerDisconnect() {
        if (isConnected.compareAndSet(true, false)) {
            // heartbeat tasks are canceled
            cancelTasks();

            // server node is disconnected
            serverNode.disconnect();

            if (onServerCrashCallback != null) {
                // server crash callback is executed
                onServerCrashCallback.run();
            }
        }
    }

    /**
     * Disconnects from the server gracefully.
     */
    public void disconnectGracefully() {
        if (isConnected.compareAndSet(true, false)) {
            // heartbeat tasks are canceled
            cancelTasks();

            // server node is disconnected
            serverNode.disconnect();
        }
    }

    private void cancelTasks() {
        if (pingTask != null) {
            // ping task is canceled
            pingTask.cancel(true);
        }
        if (timeoutTask != null) {
            // timeout task is canceled
            timeoutTask.cancel(true);
        }
    }

    /**
     * Notifies that a ping was received from the server.
     */
    public void notifyPingReceived() {
        // last ping received timestamp is updated
        this.lastPingReceived.set(System.currentTimeMillis());

        if (isFirstPing.get()) {
            // first ping flag is updated
            isFirstPing.set(false);
        }
    }

    @Override
    public void connect(String nickname) {
        // connection is requested
        tryInvoke(() -> serverNode.connect(nickname));
    }

    @Override
    public void sendLobbyCommand(LobbyCommand command) {
        // lobby command is forwarded
        tryInvoke(() -> serverNode.sendLobbyCommand(command));
    }

    @Override
    public void sendGameCommand(GameCommand command) {
        // game command is forwarded
        tryInvoke(() -> serverNode.sendGameCommand(command));
    }

    private void tryInvoke(NetworkAction action) {
        if (!isConnected.get()) {
            return;
        }
        try {
            // network action is executed
            action.execute();
        } catch (NetworkException e) {
            // server disconnect is handled
            handleServerDisconnect();
        }
    }

    /**
     * Receives a generic message from the network and forwards it to the client controller.
     *
     * @param message The message received.
     */
    public void receiveMessage(MessageDTO message) {
        ClientController controller = clientController.get();
        if (controller != null) {
            // message is passed to client controller
            controller.onMessageReceived(message);
        }
    }
}
