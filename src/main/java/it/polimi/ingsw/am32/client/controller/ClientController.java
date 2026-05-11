package it.polimi.ingsw.am32.client.controller;

import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.view.UI;
import it.polimi.ingsw.am32.client.view.tui.TuiUI;
import it.polimi.ingsw.am32.controller.commands.game.*;
import it.polimi.ingsw.am32.controller.commands.lobby.*;
import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;
import it.polimi.ingsw.am32.network.client.MessageDispatcher;
import it.polimi.ingsw.am32.network.client.VirtualServer;
import it.polimi.ingsw.am32.dto.messages.MessageDTO;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller handling incoming network events and outgoing user requests.
 */
public class ClientController {
    private final VirtualServer virtualServer;
    private final MessageDispatcher dispatcher;
    private UI ui;
    private LightGameState lightGameState;
    private String myNickname;
    private final ExecutorService uiExecutor;

    private final ExecutorService networkExecutor;

    /**
     * Client controller is instantiated.
     *
     * @param virtualServer the virtual server instance.
     */
    public ClientController(VirtualServer virtualServer) {
        this.virtualServer = virtualServer;
        this.dispatcher = new MessageDispatcher(this);
<<<<<<< feature-gui
        this.networkExecutor = Executors.newSingleThreadExecutor();
=======

        // ui executor is initialized as a single thread daemon
        this.uiExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            // thread is set as daemon
            t.setDaemon(true);
            return t;
        });
>>>>>>> master
    }

    /**
     * Ui reference is set.
     *
     * @param ui the user interface.
     */
    public void setUI(UI ui) {
        this.ui = ui;
    }


    /**
     * Connection is requested.
     *
     * @param nickname the chosen nickname.
     */
    public void requestConnect(String nickname) {
        this.myNickname = nickname;
        this.lightGameState = new LightGameState(nickname);
        networkExecutor.submit(() -> virtualServer.connect(nickname));
    }

    /**
     * Game creation is requested.
     *
     * @param numOfPlayers the target number of players.
     */
    public void requestCreateGame(int numOfPlayers) {
        CreateGameCommand cmd = new CreateGameCommand(numOfPlayers);
<<<<<<< feature-gui
        networkExecutor.submit(() -> virtualServer.sendLobbyCommand(cmd, myNickname));
=======
        // command is sent
        virtualServer.sendLobbyCommand(cmd);
>>>>>>> master
    }

    /**
     * Game join is requested.
     *
     * @param gameId the id of the game.
     */
    public void requestJoinGame(int gameId) {
        JoinGameCommand cmd = new JoinGameCommand(gameId);
<<<<<<< feature-gui
        networkExecutor.submit(() -> virtualServer.sendLobbyCommand(cmd, myNickname));
=======
        // command is sent
        virtualServer.sendLobbyCommand(cmd);
    }

    /**
     * Lobby error event is processed.
     *
     * @param event the lobby error event.
     */
    public void onLobbyError(LobbyErrorEvent event) {
        runOnUIThread(() -> {
            // ui is updated
            if (ui != null) ui.showLobbyError(event);
        });
>>>>>>> master
    }

    /**
     * Totem placement is requested.
     *
     * @param tileId the id of the tile.
     */
    public void requestPlaceTotem(String tileId) {
        PlaceTotemCommand cmd = new PlaceTotemCommand(tileId);
<<<<<<< feature-gui
        networkExecutor.submit(() -> virtualServer.sendGameCommand(cmd, myNickname));
=======
        // command is sent
        virtualServer.sendGameCommand(cmd);
>>>>>>> master
    }

    /**
     * Action resolution is requested.
     *
     * @param characters the character cards.
     * @param buildings the building cards.
     */
    public void requestResolveAction(List<String> characters, List<String> buildings) {
        ResolveActionCommand cmd = new ResolveActionCommand(characters, buildings);
<<<<<<< feature-gui
        networkExecutor.submit(() -> virtualServer.sendGameCommand(cmd, myNickname));
=======
        // command is sent
        virtualServer.sendGameCommand(cmd);
>>>>>>> master
    }

    /**
     * Local visual focus on a specific player is requested.
     *
     * @param nickname the player nickname, or null to clear focus.
     */
    public void requestPlayerFocus(String nickname) {
        runOnUIThread(() -> {
            if (ui != null) ui.showPlayerFocus(nickname);
        });
    }


    /**
     * Generic message is processed.
     *
     * @param message the network message.
     */
    public void onMessageReceived(MessageDTO message) {
        message.accept(this.dispatcher);
    }

<<<<<<< feature-gui
    public void onLobbyError(LobbyErrorEvent event) {
        runOnUIThread(() -> {
            if (ui != null) ui.showLobbyError(event);
        });
    }

=======
    /**
     * Connection result event is processed.
     *
     * @param event the connection result event.
     */
>>>>>>> master
    public void onConnectionResult(ConnectionResultEvent event) {
        if (event.isSuccess()) {
            // liveness routine is started on successful connection
            this.virtualServer.startLivenessRoutine();
        }

        runOnUIThread(() -> {
            if (ui != null) ui.showConnectionResult(event);
        });
    }

    public void onAvailableGamesListUpdate(AvailableLobbiesEvent event) {
        runOnUIThread(() -> {
            if (ui != null) ui.updateAvailableGames(event);
        });
    }

    public void onLobbyRoomUpdated(LobbyStateUpdatedEvent event) {
        runOnUIThread(() -> {
            if (ui != null) ui.updateLobbyRoom(event);
        });
    }

    public void onGameStarted(GameStartedEvent event) {
        runOnUIThread(() -> {
            lightGameState.initializeFromDTO(event.getGameState());
            if (ui != null) ui.showGameStarted(event);
        });
    }

    public void onGameStateUpdate(GameStateEvent event) {
        runOnUIThread(() -> {
            lightGameState.initializeFromDTO(event.getGameState());
            if (ui != null) ui.restoreGameState(event);
        });
    }

    public void onGamePhaseChanged(GamePhaseChangedEvent event) {
        runOnUIThread(() -> {
            lightGameState.updatePhaseChange(event.getPhase(), event.getNextPlayer());
            if (ui != null) ui.updateGamePhase(event);
        });
    }

    public void onRoundEnded(RoundEndedEvent event) {
        runOnUIThread(() -> {
            if (ui != null) ui.showRoundSummary(event);
        });
    }

    public void onGameEnded(GameEndedEvent event) {
        runOnUIThread(() -> {
            if (ui != null) ui.showGameResults(event);
        });
    }

    public void onTotemPlaced(TotemPlacedEvent event) {
        runOnUIThread(() -> {
            lightGameState.updateTotemPlacement(event.getPlayer(), event.getOfferTileId(), event.getNextPlayer());
            if (ui != null) ui.showTotemPlaced(event);
        });
    }

    public void onActionResolved(ActionResolvedEvent event) {
        runOnUIThread(() -> {
            lightGameState.updateActionResolution(event.getPlayer(), event.getAcquiredCharacterCardIds(), event.getAcquiredBuildingCardIds(), event.getStatsChanges(), event.getNextPlayer());
            if (ui != null) ui.showActionResolved(event);
        });
    }

    public void onEventResolved(EventResolvedEvent event) {
        runOnUIThread(() -> {
            lightGameState.updateStats(event.getStatsChanges());
            if (ui != null) ui.showEventResolved(event);
        });
    }

    public void onExtraAcquisitionResolved(ExtraAcquisitionResolvedEvent event) {
        runOnUIThread(() -> {
            lightGameState.updateStats(event.getStatsChanges());
            if (ui != null) ui.showExtraAcquisition(event);
        });
    }

    public void onInvalidAction(GameErrorEvent event) {
        runOnUIThread(() -> {
            if (ui != null) ui.showError(event);
        });
    }

    public void onPlayerDisconnected(PlayerDisconnectedEvent event) {
        runOnUIThread(() -> {
            lightGameState.updatePlayerConnection(event.getPlayer(), false);
            if (ui != null) ui.showPlayerDisconnected(event);
        });
    }

    public void onPlayerReconnected(PlayerReconnectedEvent event) {
        runOnUIThread(() -> {
            lightGameState.updatePlayerConnection(event.getPlayer(), true);
            if (ui != null) ui.showPlayerReconnected(event);
        });
    }

    public void onGameSuspended(GameSuspendedEvent event) {
        runOnUIThread(() -> {
            if (ui != null) ui.showGameSuspended(event);
        });
    }

    public void onGameResumed(GameResumedEvent event) {
        runOnUIThread(() -> {
            if (ui != null) ui.showGameResumed(event);
        });
    }

    /**
     * Fatal error is processed.
     *
     * @param message the error message.
     */
    public void onFatalError(String message) {
        runOnUIThread(() -> {
            // ui is notified of the disconnection
            if (ui != null) ui.notifyDisconnection(message);
        });
    }

    /**
     * Task is executed on the ui thread.
     *
     * @param task the runnable task.
     */
    private void runOnUIThread(Runnable task) {
<<<<<<< feature-gui
        task.run();
=======
        // task is submitted to the ui executor queue
        this.uiExecutor.submit(task);
>>>>>>> master
    }

    public LightGameState getLightGameState() {
        return this.lightGameState;
    }

    public String getMyNickname() {
        return this.myNickname;
    }

<<<<<<< feature-gui
    public void shutdown() {
        if (networkExecutor != null && !networkExecutor.isShutdown()) {
            networkExecutor.shutdownNow();
        }

        // virtual server is disconnected gracefully so the server detects a clean exit
        // rather than a crash, which is relevant for the resilience FA
        if (virtualServer != null) {
            virtualServer.disconnectGracefully();
=======
    /**
     * The user requests application termination.
     */
    public void requestExit() {
        runOnUIThread(() -> {
            // exit is triggered if ui is tui
            if (ui instanceof TuiUI) {
                ((TuiUI) ui).triggerUserQuit();
            } else {
                // fallback is executed for gui
                shutdown();
                System.exit(0);
            }
        });
    }

    /**
     * Resources are released and the connection is gracefully closed.
     */
    public void shutdown() {
        // ui executor is shut down
        if (this.uiExecutor != null && !this.uiExecutor.isShutdown()) {
            this.uiExecutor.shutdownNow();
        }

        // graceful disconnect is forwarded
        if (this.virtualServer != null) {
            this.virtualServer.disconnectGracefully();
>>>>>>> master
        }
    }
}
