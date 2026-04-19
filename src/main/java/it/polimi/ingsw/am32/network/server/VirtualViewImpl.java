package it.polimi.ingsw.am32.network.server;

import it.polimi.ingsw.am32.controller.VirtualView;
import it.polimi.ingsw.am32.network.ClientNode;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;
import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.game.models.*;

import java.util.function.Consumer;

public class VirtualViewImpl implements VirtualView {

    private final String nickname;
    private final ClientNode clientNode;

    private final Consumer<Boolean> onConnectionClosed;

    private volatile boolean isRunning;
    private Thread heartbeatThread;

    private VirtualViewImpl(String nickname, ClientNode clientNode, Consumer<Boolean> onConnectionClosed) {
        this.nickname = nickname;
        this.clientNode = clientNode;
        this.onConnectionClosed = onConnectionClosed;
        this.isRunning = true;
    }

    public static VirtualViewImpl create(String nickname, ClientNode clientNode, Consumer<Boolean> onConnectionClosed) {
        VirtualViewImpl view = new VirtualViewImpl(nickname, clientNode, onConnectionClosed);
        view.startHeartbeat();
        return view;
    }

    private void startHeartbeat() {
        heartbeatThread = new Thread(() -> {
            while (isRunning) {
                try {
                    Thread.sleep(5000);
                    if (isRunning) {
                        clientNode.ping();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (NetworkException e) {
                    System.err.println("[VIRTUAL VIEW] Heartbeat failed for " + nickname + ". Client dropped.");
                    handleDisconnect();
                    break;
                }
            }
        });
        heartbeatThread.setName("Heartbeat-" + nickname);
        heartbeatThread.start();
    }

    public void stopHeartbeat() {
        isRunning = false;
        if (heartbeatThread != null) {
            heartbeatThread.interrupt();
        }
    }

    public void handleDisconnect() {
        if (!isRunning) return;

        stopHeartbeat();
        if (onConnectionClosed != null) {
            onConnectionClosed.accept(true);
        }
    }

    @Override
    public boolean checkAndHandleLiveness() {
        return isRunning;
    }

    @Override
    public void notifyConnectionResult(boolean successful, String message) {
        try {
            clientNode.notifyConnectionResult(successful, message);
        } catch (Exception e) {
            handleDisconnect();
        }

        if (!successful) {
            stopHeartbeat();
            if (onConnectionClosed != null) {
                onConnectionClosed.accept(false);
            }
        }
    }

    @Override
    public void notifyLobbyState(AvailableGamesListEvent dto) {
        try {
            clientNode.notifyLobbyState(dto.getAvailableGames());
        } catch (Exception e) { handleDisconnect(); }
    }

    @Override
    public void notifyGameStarted(int gameId) {
        try {
            clientNode.notifyGameStarted(gameId);
        } catch (Exception e) { handleDisconnect(); }
    }

    @Override
    public void notifyError(InvalidActionEvent dto) {
        try { clientNode.notifyError(dto.getReason(), dto.getMessage()); } catch (Exception e) { handleDisconnect(); }
    }

    @Override
    public void notifyGameSuspended() {
        try { clientNode.notifyGameSuspended(); } catch (Exception e) { handleDisconnect(); }
    }

    @Override
    public void notifyGameResumed() {
        try { clientNode.notifyGameResumed(); } catch (Exception e) { handleDisconnect(); }
    }

    @Override
    public void notifyLobbyRoomUpdated(LobbyStateUpdatedEvent dto) {
        try { clientNode.receiveEvent(dto); } catch (Exception e) { handleDisconnect(); }
    }

    @Override
    public void notifyGameStarted(GameStartedEvent dto) {
        try { clientNode.receiveEvent(dto); } catch (Exception e) { handleDisconnect(); }
    }

    @Override
    public void notifyGameState(GameStateDTO dto) {
        try { clientNode.receiveEvent(dto); } catch (Exception e) { handleDisconnect(); }
    }

    @Override
    public void notifyGameEnded(GameEndedEvent dto) {
        try { clientNode.receiveEvent(dto); } catch (Exception e) { handleDisconnect(); }
    }

    @Override
    public void notifyPlayerDisconnected(PlayerDisconnectedEvent dto) {
        try { clientNode.receiveEvent(dto); } catch (Exception e) { handleDisconnect(); }
    }

    @Override
    public void notifyPlayerReconnected(PlayerReconnectedEvent dto) {
        try { clientNode.receiveEvent(dto); } catch (Exception e) { handleDisconnect(); }
    }

    @Override
    public void notifyTotemPlaced(TotemPlacedEvent dto) {
        try { clientNode.receiveEvent(dto); } catch (Exception e) { handleDisconnect(); }
    }

    @Override
    public void notifyActionResolved(ActionResolvedEvent dto) {
        try { clientNode.receiveEvent(dto); } catch (Exception e) { handleDisconnect(); }
    }

    @Override
    public void notifyExtraAcquisitionResolved(ExtraAcquisitionResolvedEvent dto) {
        try { clientNode.receiveEvent(dto); } catch (Exception e) { handleDisconnect(); }
    }

    @Override
    public void notifyEventResolved(EventResolvedEvent dto) {
        try { clientNode.receiveEvent(dto); } catch (Exception e) { handleDisconnect(); }
    }

    @Override
    public void notifyRoundEnded(RoundEndedEvent dto) {
        try { clientNode.receiveEvent(dto); } catch (Exception e) { handleDisconnect(); }
    }

    @Override
    public void notifyGamePhaseChanged(GamePhaseChangedEvent dto) {
        try { clientNode.receiveEvent(dto); } catch (Exception e) { handleDisconnect(); }
    }
}