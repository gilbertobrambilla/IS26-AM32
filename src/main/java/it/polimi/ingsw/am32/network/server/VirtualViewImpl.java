package it.polimi.ingsw.am32.network.server;

import it.polimi.ingsw.am32.controller.VirtualView;
import it.polimi.ingsw.am32.network.ClientNode;
import it.polimi.ingsw.am32.network.exceptions.NetworkException;
import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;
import it.polimi.ingsw.am32.dto.messages.game.events.*;

import java.util.function.Consumer;

/**
 * Virtual view implementation acting as an adapter between the controller and the network.
 */
public class VirtualViewImpl implements VirtualView, NetworkNode {

    private final String nickname;
    private final ClientNode clientNode;
    private final Consumer<Boolean> onConnectionClosed;

    private boolean isRunning;
    private Thread heartbeatThread;

    /**
     * Constructs a new virtual view implementation.
     *
     * @param nickname           The nickname of the player.
     * @param clientNode         The client network node.
     * @param onConnectionClosed The callback to invoke when connection drops.
     */
    private VirtualViewImpl(String nickname, ClientNode clientNode, Consumer<Boolean> onConnectionClosed) {
        this.nickname = nickname;
        this.clientNode = clientNode;
        this.onConnectionClosed = onConnectionClosed;
        this.isRunning = true;
    }

    /**
     * Creates and starts a new virtual view implementation.
     *
     * @param nickname           The nickname of the player.
     * @param clientNode         The client network node.
     * @param onConnectionClosed The callback to invoke when connection drops.
     * @return The created virtual view instance.
     */
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
                        // ping is sent to client node
                        clientNode.ping();
                    }
                } catch (InterruptedException e) {
                    // heartbeat thread is interrupted
                    Thread.currentThread().interrupt();
                    break;
                } catch (NetworkException e) {
                    // client drop is handled
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
            // heartbeat thread is interrupted
            heartbeatThread.interrupt();
        }
    }

    @Override
    public void handleDisconnect() {
        if (!isRunning) return;

        // heartbeat is stopped
        stopHeartbeat();

        if (onConnectionClosed != null) {
            // connection closed callback is invoked
            onConnectionClosed.accept(true);
        }
    }

    @Override
    public void destroy() {
        if (!isRunning) return;

        // heartbeat is stopped
        stopHeartbeat();

        if (onConnectionClosed != null) {
            // connection closed callback is invoked cleanly
            onConnectionClosed.accept(false);
        }
    }

    @Override
    public boolean checkAndHandleLiveness() {
        return isRunning;
    }

    /**
     * Pushes a generic message dto to the client catching potential network exceptions.
     *
     * @param message The generic message dto to be sent.
     */
    private void sendMessageToClient(MessageDTO message) {
        try {
            // generic message is pushed to client
            clientNode.sendMessage(message);
        } catch (Exception e) {
            // disconnect is triggered on failure
            handleDisconnect();
        }
    }

    @Override
    public void notifyConnectionResult(ConnectionResultEvent dto) {
        // connection result dto is created and pushed
        sendMessageToClient(dto);

        if (!dto.isSuccess()) {
            // resources are cleaned up on failed connection
            stopHeartbeat();
            if (onConnectionClosed != null) {
                // connection closed callback is invoked
                onConnectionClosed.accept(false);
            }
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
