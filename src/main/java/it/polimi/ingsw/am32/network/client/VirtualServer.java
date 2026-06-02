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
 * A client-side virtual server is represented.
 */
public class VirtualServer implements ServerConnection {

    private static final long PING_PERIOD_MS = 1000;
    private static final long TIMEOUT_MS = 5000;
    private static final long INITIAL_TIMEOUT_MS = 10000;

    // thread pool initialized with daemon threads
    private static final ScheduledExecutorService sharedScheduler = Executors.newScheduledThreadPool(2, r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        // thread set as daemon
        t.setDaemon(true);
        return t;
    });

    private final ServerNode serverNode;
    private final Runnable onServerCrashCallback;
    private final AtomicBoolean isConnected;
    private final AtomicReference<ClientController> clientController = new AtomicReference<>();
    private final AtomicLong lastPingReceived = new AtomicLong(System.currentTimeMillis());
    private final AtomicBoolean isFirstPing = new AtomicBoolean(true);

    private ScheduledFuture<?> pingTask;
    private ScheduledFuture<?> timeoutTask;

    /**
     * A virtual server instance is constructed.
     *
     * @param serverNode the server node representation
     * @param onServerCrashCallback the callback to execute on a server crash
     */
    private VirtualServer(ServerNode serverNode, Runnable onServerCrashCallback) {
        this.serverNode = serverNode;
        this.onServerCrashCallback = onServerCrashCallback;
        this.isConnected = new AtomicBoolean(true);
    }

    /**
     * A new virtual server instance is created.
     *
     * @param serverNode the server node representation
     * @param onServerCrashCallback the callback to execute on a server crash
     * @return the created virtual server instance
     */
    public static VirtualServer create(ServerNode serverNode, Runnable onServerCrashCallback) {
        return new VirtualServer(serverNode, onServerCrashCallback);
    }

    @Override
    public void connect() {
        // connection requested
        tryInvoke(serverNode::connect);
    }

    @Override
    public void login(String nickname) {
        // login requested
        tryInvoke(() -> serverNode.login(nickname));
    }

    @Override
    public void sendLobbyCommand(LobbyCommand command) {
        // lobby command forwarded
        tryInvoke(() -> serverNode.sendLobbyCommand(command));
    }

    @Override
    public void sendGameCommand(GameCommand command) {
        // game command forwarded
        tryInvoke(() -> serverNode.sendGameCommand(command));
    }

    /**
     * The client controller reference is set.
     *
     * @param clientController the client controller to be set
     */
    public void setClientController(ClientController clientController) {
        // client controller reference updated
        this.clientController.set(clientController);
    }

    /**
     * The liveness routine for ping and timeout monitoring is started.
     */
    public void startServerHeartbeat() {
        // ping task scheduled
        this.pingTask = sharedScheduler.scheduleAtFixedRate(() -> {
            if (isConnected.get()) {
                // ping sent to server
                tryInvoke(serverNode::sendPingToServer);
            }
        }, 0, PING_PERIOD_MS, TimeUnit.MILLISECONDS);

        // timeout task scheduled
        this.timeoutTask = sharedScheduler.scheduleAtFixedRate(() -> {
            if (isConnected.get()) {
                long currentTime = System.currentTimeMillis();
                // current timeout calculated
                long currentTimeout = isFirstPing.get() ? INITIAL_TIMEOUT_MS : TIMEOUT_MS;

                if (currentTime - lastPingReceived.get() > currentTimeout) {
                    // server disconnect handled
                    handleServerDisconnect();
                }
            }
        }, TIMEOUT_MS, PING_PERIOD_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * The sudden disconnection of the server is handled.
     */
    public void handleServerDisconnect() {
        if (isConnected.compareAndSet(true, false)) {
            // heartbeat tasks canceled
            cancelTasks();
            // server node disconnected
            serverNode.disconnect();

            if (onServerCrashCallback != null) {
                // server crash callback executed
                onServerCrashCallback.run();
            }
        }
    }

    /**
     * The connection to the server is closed gracefully.
     */
    public void disconnectGracefully() {
        if (isConnected.compareAndSet(true, false)) {
            // heartbeat tasks canceled
            cancelTasks();
            // server node disconnected
            serverNode.disconnect();
        }
    }

    /**
     * A ping received from the server is processed.
     */
    public void onPingReceivedFromServer() {
        // last ping received timestamp updated
        this.lastPingReceived.set(System.currentTimeMillis());

        if (isFirstPing.get()) {
            // first ping flag updated
            isFirstPing.set(false);
        }
    }

    /**
     * A generic message from the network is forwarded to the client controller.
     *
     * @param message the message received
     */
    public void receiveMessage(MessageDTO message) {
        ClientController controller = clientController.get();
        if (controller != null) {
            // message passed to client controller
            controller.onMessageReceived(message);
        }
    }

    /**
     * The active heartbeat tasks are canceled.
     */
    private void cancelTasks() {
        if (pingTask != null) {
            // ping task canceled
            pingTask.cancel(true);
        }
        if (timeoutTask != null) {
            // timeout task canceled
            timeoutTask.cancel(true);
        }
    }

    /**
     * A network action is invoked while potential disconnections are handled.
     *
     * @param action the network action to execute
     */
    private void tryInvoke(NetworkAction action) {
        if (!isConnected.get()) {
            return;
        }
        try {
            // network action executed
            action.execute();
        } catch (NetworkException e) {
            // server disconnect handled
            handleServerDisconnect();
        }
    }
}
