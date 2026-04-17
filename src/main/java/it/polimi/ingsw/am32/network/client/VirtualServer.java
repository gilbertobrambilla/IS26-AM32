package it.polimi.ingsw.am32.network.client;

import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.network.ServerConnection;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;

import java.util.concurrent.atomic.AtomicBoolean;

public class VirtualServer implements ServerConnection {

    private final ServerNode serverNode;
    private final Runnable onServerCrashCallback;
    private final AtomicBoolean isConnected;


    public VirtualServer(ServerNode serverNode, Runnable onServerCrashCallback) {
        this.serverNode = serverNode;
        this.onServerCrashCallback = onServerCrashCallback;
        this.isConnected = new AtomicBoolean(true);
    }

    public synchronized void handleServerDisconnect() {
        if (isConnected.compareAndSet(true, false)) {
            serverNode.disconnect();

            if (onServerCrashCallback != null) {
                onServerCrashCallback.run();
            }
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
            System.out.println("[CLIENT] Comando ignorato: sei disconnesso dal server.");
            return;
        }
        try {
            action.execute();
        } catch (NetworkException e) {
            handleServerDisconnect();
        }
    }

    public void receiveLeaderboard(LeaderboardDTO leaderboard) {
        // leaderboard data is forwarded to the client controller
        // todo: if (clientController != null) clientController.onLeaderboardUpdated(leaderboard);

        System.out.println("[CLIENT] leaderboard update received from server.");
    }

    @FunctionalInterface
    private interface NetworkAction {
        void execute() throws NetworkException;
    }
}