package it.polimi.ingsw.am32.network.client;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.NetworkAction;
import it.polimi.ingsw.am32.network.ServerConnection;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;

import java.util.concurrent.atomic.AtomicBoolean;

public class VirtualServer implements ServerConnection {

    private final ServerNode serverNode;
    private final Runnable onServerCrashCallback;
    private final AtomicBoolean isConnected;
    private Thread heartbeatThread;

    private volatile ClientController clientController;

    private VirtualServer(ServerNode serverNode, Runnable onServerCrashCallback) {
        this.serverNode = serverNode;
        this.onServerCrashCallback = onServerCrashCallback;
        this.isConnected = new AtomicBoolean(true);
    }

    public static VirtualServer create(ServerNode serverNode, Runnable onServerCrashCallback) {
        VirtualServer instance = new VirtualServer(serverNode, onServerCrashCallback);
        instance.startHeartbeat();
        return instance;
    }

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }

    private void startHeartbeat() {
        this.heartbeatThread = new Thread(() -> {
            try {
                while (isConnected.get() && !Thread.currentThread().isInterrupted()) {
                    Thread.sleep(5000);
                    tryInvoke(serverNode::ping);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[CLIENT] Heartbeat thread stopped cleanly.");
            }
        }, "ServerHeartbeat-" + System.currentTimeMillis());

        this.heartbeatThread.setDaemon(true);
        this.heartbeatThread.start();
    }

    public void handleServerDisconnect() {
        if (isConnected.compareAndSet(true, false)) {
            if (heartbeatThread != null) {
                heartbeatThread.interrupt();
            }
            serverNode.disconnect();

            if (onServerCrashCallback != null) {
                onServerCrashCallback.run();
            }
        }
    }

    public void disconnectGracefully() {
        if (isConnected.compareAndSet(true, false)) {
            if (heartbeatThread != null) {
                heartbeatThread.interrupt();
            }
            serverNode.disconnect();
        }
    }

    @Override
    public void connect(String nickname) {
        tryInvoke(() -> serverNode.connect(nickname));
    }

    @Override
    public void sendLobbyCommand(LobbyCommand command, String nickname) {
        tryInvoke(() -> serverNode.sendLobbyCommand(command, nickname));
    }

    @Override
    public void sendGameCommand(GameCommand command, String nickname) {
        tryInvoke(() -> serverNode.sendGameCommand(command, nickname));
    }

    private void tryInvoke(NetworkAction action) {
        if (!isConnected.get()) {
            System.out.println("[CLIENT] Command ignored: disconnected from server.");
            return;
        }
        try {
            action.execute();
        } catch (NetworkException e) {
            handleServerDisconnect();
        }
    }

    /**
     * Receives a generic message from the network and forwards it to the client controller.
     *
     * @param message The message received.
     */
    public void receiveMessage(MessageDTO message) {
        // console log is printed
        System.out.println("[CLIENT] message received from server.");

        if (clientController != null) {
            // message is passed to client controller for dispatching
            clientController.onMessageReceived(message);
        }
    }
}
