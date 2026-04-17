package it.polimi.ingsw.am32.network.server;

import it.polimi.ingsw.am32.controller.VirtualView;
import it.polimi.ingsw.am32.dto.results.GameResultDTO;
import it.polimi.ingsw.am32.dto.state.GameInfoDTO;
import it.polimi.ingsw.am32.model.enums.InvalidActionReason;
import it.polimi.ingsw.am32.model.observers.GameEvent;
import it.polimi.ingsw.am32.network.ClientNode;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class VirtualViewImpl implements VirtualView {

    private final String clientNickname;
    private final ClientNode clientNode;
    private final Runnable onDisconnectCallback;
    private final AtomicBoolean isConnected;
    private Thread heartbeatThread;


    public VirtualViewImpl(String clientNickname, ClientNode clientNode, Runnable onDisconnectCallback) {
        this.clientNickname = clientNickname;
        this.clientNode = clientNode;
        this.onDisconnectCallback = onDisconnectCallback;
        this.isConnected = new AtomicBoolean(true);
    }

    public static VirtualViewImpl create(String clientNickname, ClientNode clientNode,
                                         Runnable onDisconnectCallback) {
        VirtualViewImpl instance = new VirtualViewImpl(clientNickname, clientNode, onDisconnectCallback);
        instance.startHeartbeat();
        return instance;
    }

    private void startHeartbeat() {
        this.heartbeatThread = new Thread(() -> {
            try {
                while (isConnected.get() && !Thread.currentThread().isInterrupted()) {
                    Thread.sleep(5000);
                    clientNode.ping();
                }
            } catch (InterruptedException | NetworkException e) {
                handleDisconnect();
            }
        }, "Heartbeat-" + clientNickname);

        this.heartbeatThread.setDaemon(true);
        this.heartbeatThread.start();
    }

    public void handleDisconnect() {
        if (isConnected.compareAndSet(true, false)) {
            System.err.println("[SERVER] connection lost with: " + clientNickname);

            if (heartbeatThread != null) {
                heartbeatThread.interrupt();
            }

            if (onDisconnectCallback != null) {
                onDisconnectCallback.run();
            }
        }
    }

    @Override
    public void notifyConnectionResult(boolean successful, String message) {
        tryInvoke(() -> clientNode.notifyConnectionResult(successful, message));

        if (!successful) {
            handleDisconnect();
        }
    }

    @Override
    public void notifyLobbyState(List<GameInfoDTO> availableGames) {
        tryInvoke(() -> clientNode.notifyLobbyState(availableGames));
    }

    @Override
    public void notifyGameStarted(int gameId) {
        tryInvoke(() -> clientNode.notifyGameStarted(gameId));
    }

    @Override
    public void notifyError(InvalidActionReason reason, String message) {
        tryInvoke(() -> clientNode.notifyError(reason, message));
    }

    @Override
    public void update(GameEvent event) {
        tryInvoke(() -> clientNode.receiveEvent(event));
    }

    @Override
    public void notifyGameSuspended() {
        tryInvoke(clientNode::notifyGameSuspended);
    }

    @Override
    public void notifyGameResumed() {
        tryInvoke(clientNode::notifyGameResumed);
    }

    @Override
    public void notifyLeaderboard(LeaderboardDTO leaderboard) {
        tryInvoke(() -> clientNode.notifyLeaderboard(leaderboard));
    }

    @Override
    public void notifyGameEnded(GameResultDTO resultDTO) {
        tryInvoke(() -> clientNode.notifyGameEnded(resultDTO));
        System.out.println("[SERVER] Game ended for " + clientNickname + ". Connection kept alive.");
    }

    private void tryInvoke(NetworkAction action) {
        if (!isConnected.get()) return;
        try {
            action.execute();
        } catch (NetworkException e) {
            handleDisconnect();
        }
    }

    @FunctionalInterface
    private interface NetworkAction {
        void execute() throws NetworkException;
    }
}