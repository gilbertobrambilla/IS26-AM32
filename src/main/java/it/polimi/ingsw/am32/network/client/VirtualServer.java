package it.polimi.ingsw.am32.network.client;

import it.polimi.ingsw.am32.client.ClientController;
import it.polimi.ingsw.am32.controller.commands.game.GameCommand;
import it.polimi.ingsw.am32.controller.commands.lobby.LobbyCommand;
import it.polimi.ingsw.am32.enums.InvalidActionReason;
import it.polimi.ingsw.am32.network.ServerConnection;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;
import it.polimi.ingsw.am32.observers.GameEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class VirtualServer implements ServerConnection {

    private final ServerNode serverNode;
    private final Runnable onServerCrashCallback;
    private final AtomicBoolean isConnected;

    private ClientController clientController;

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }


    public VirtualServer(ServerNode serverNode, Runnable onServerCrashCallback) {
        this.serverNode = serverNode;
        this.onServerCrashCallback = onServerCrashCallback;
        this.isConnected = new AtomicBoolean(true);
    }

    public void handleServerDisconnect() {
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
            System.out.println("[CLIENT] Command ignored: disconnected from server.");
            return;
        }
        try {
            action.execute();
        } catch (NetworkException e) {
            handleServerDisconnect();
        }
    }

    public void receiveConnectionResult(boolean successful, String message) {
        System.out.println("[CLIENT] connection result: " + successful + " - " + message);
        if (clientController != null) {
            clientController.onConnectionResult(successful, message);
        }
    }

    public void receiveLobbyState(List<GameInfoDTO> availableGames) {
        System.out.println("[CLIENT] lobby state updated.");
        if (clientController != null) {
            clientController.onLobbyStateUpdate(availableGames);
        }
    }

    public void receiveGameStarted(int gameId) {
        System.out.println("[CLIENT] game " + gameId + " started!");
        if (clientController != null) {
            clientController.onGameStarted(gameId);
        }
    }

    public void receiveError(InvalidActionReason reason, String message) {
        System.err.println("[CLIENT] action error: " + message);
        if (clientController != null) {
            clientController.onError(reason, message);
        }
    }

    public void receiveEvent(GameEvent event) {
        System.out.println("[CLIENT] game event received.");
        if (clientController != null) {
            clientController.onGameEvent(event);
        }
    }

    public void receiveGameSuspended() {
        System.out.println("[CLIENT] game suspended waiting for reconnections.");
        if (clientController != null) {
            clientController.onGameSuspended();
        }
    }

    public void receiveGameResumed() {
        System.out.println("[CLIENT] game resumed.");
        if (clientController != null) {
            clientController.onGameResumed();
        }
    }

    public void receiveGameEnded(GameResultDTO resultDTO) {
        System.out.println("[CLIENT] game ended.");
        if (clientController != null) {
            clientController.onGameEnded(resultDTO);
        }
    }

    public void receiveLeaderboard(LeaderboardDTO leaderboard) {
        System.out.println("[CLIENT] leaderboard update received from server.");
        if (clientController != null) {
            clientController.onLeaderboardUpdated(leaderboard);
        }
    }

    @FunctionalInterface
    private interface NetworkAction {
        void execute() throws NetworkException;
    }
}