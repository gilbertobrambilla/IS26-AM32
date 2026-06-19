package it.polimi.ingsw.am32.client.controller;

import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.view.UI;
import it.polimi.ingsw.am32.controller.commands.game.*;
import it.polimi.ingsw.am32.controller.commands.lobby.*;
import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.game.models.CardDTO;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;
import it.polimi.ingsw.am32.network.client.MessageDispatcher;
import it.polimi.ingsw.am32.network.client.VirtualServer;
import it.polimi.ingsw.am32.dto.messages.MessageDTO;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

    /**
     * Client controller is instantiated.
     *
     * @param virtualServer the virtual server instance
     */
    public ClientController(VirtualServer virtualServer) {
        this.virtualServer = virtualServer;
        this.dispatcher = new MessageDispatcher(this);

        // ui executor is initialized as a single thread daemon
        this.uiExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            // thread is set as daemon
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Ui reference is set.
     *
     * @param ui the user interface
     */
    public void setUI(UI ui) {
        this.ui = ui;
    }

    // outbound commands (ui -> controller -> network)

    /**
     * Physical connection is requested.
     */
    public void requestConnect() {
        // connection is forwarded to virtual server
        virtualServer.connect();
    }

    /**
     * Login is requested.
     *
     * @param nickname the chosen nickname
     */
    public void requestLogin(String nickname) {
        // temporary nickname is stored
        this.myNickname = nickname;
        // authentication is forwarded to virtual server
        virtualServer.login(nickname);
    }

    /**
     * Game creation is requested.
     *
     * @param numOfPlayers the target number of players
     */
    public void requestCreateGame(int numOfPlayers) {
        CreateGameCommand cmd = new CreateGameCommand(numOfPlayers);
        // command is sent
        virtualServer.sendLobbyCommand(cmd);
    }

    /**
     * Game join is requested.
     *
     * @param gameId the id of the game
     */
    public void requestJoinGame(int gameId) {
        JoinGameCommand cmd = new JoinGameCommand(gameId);
        // command is sent
        virtualServer.sendLobbyCommand(cmd);
    }

    /**
     * Lobby error event is processed.
     *
     * @param event the lobby error event
     */
    public void onLobbyError(LobbyErrorEvent event) {
        runOnUIThread(() -> {
            // ui is updated
            if (ui != null) ui.showLobbyError(event);
        });
    }

    /**
     * Totem placement is requested.
     *
     * @param tileId the id of the tile
     */
    public void requestPlaceTotem(String tileId) {
        PlaceTotemCommand cmd = new PlaceTotemCommand(tileId);
        // command is sent
        virtualServer.sendGameCommand(cmd);
    }

    /**
     * Action resolution is requested.
     *
     * @param characters the character cards
     * @param buildings the building cards
     */
    public void requestResolveAction(List<String> characters, List<String> buildings) {
        ResolveActionCommand cmd = new ResolveActionCommand(characters, buildings);
        // command is sent
        virtualServer.sendGameCommand(cmd);
    }

    /**
     * Extra acquisition is requested.
     *
     * @param cardId the id of the acquired card, or null to skip the action
     */
    public void requestExtraAcquisition(String cardId) {
        // command object is instantiated
        ExtraAcquisitionCommand cmd = new ExtraAcquisitionCommand(cardId);
        // command is sent
        virtualServer.sendGameCommand(cmd);
    }

    /**
     * Local visual focus on a specific player is requested.
     *
     * @param nickname the player nickname, or null to clear focus
     */
    public void requestPlayerFocus(String nickname) {
        // focus request is dispatched to ui thread
        runOnUIThread(() -> {
            if (ui != null) ui.showPlayerFocus(nickname);
        });
    }

    // inbound events (network -> controller -> ui)

    /**
     * Generic message is processed.
     *
     * @param message the network message
     */
    public void onMessageReceived(MessageDTO message) {
        // double dispatch is triggered
        message.accept(this.dispatcher);
    }

    /**
     * Connection result event is processed.
     *
     * @param event the connection result event
     */
    public void onConnectionResult(ConnectionResultEvent event) {
        if (event.isSuccess()) {
            // liveness routine is started on successful connection
            this.virtualServer.startServerHeartbeat();
        }

        runOnUIThread(() -> {
            // ui is updated to ask for nickname
            if (ui != null) ui.showConnectionResult(event);
        });
    }

    /**
     * Login result event is processed.
     *
     * @param event the login result event
     */
    public void onLoginResult(LoginResultEvent event) {
        if (event.isSuccess()) {
            // light game state is initialized upon success
            this.lightGameState = new LightGameState(this.myNickname);
        } else {
            // rejected nickname is cleared
            this.myNickname = null;
        }

        runOnUIThread(() -> {
            // ui is updated with login outcome
            if (ui != null) {
                ui.showLoginResult(event);
            }
        });
    }

    /**
     * Available games list updates are processed.
     *
     * @param event the available lobbies event
     */
    public void onAvailableGamesListUpdate(AvailableLobbiesEvent event) {
        runOnUIThread(() -> {
            // ui is updated
            if (ui != null) ui.updateAvailableLobbies(event);
        });
    }

    /**
     * Lobby room update events are processed.
     *
     * @param event the lobby room update event
     */
    public void onLobbyRoomUpdated(LobbyRoomUpdateEvent event) {
        runOnUIThread(() -> {
            // ui is updated
            if (ui != null) ui.updateLobbyRoom(event);
        });
    }

    /**
     * Game start events are processed.
     *
     * @param event the game started event
     */
    public void onGameStarted(GameStartedEvent event) {
        runOnUIThread(() -> {
            // state is initialized
            lightGameState.initializeFromDTO(event.getGameState());
            // ui is updated
            if (ui != null) ui.showGameStarted(event);
        });
    }

    /**
     * Full game state update events are processed.
     *
     * @param event the game state event
     */
    public void onGameStateUpdate(GameStateEvent event) {
        runOnUIThread(() -> {
            // state is initialized
            lightGameState.initializeFromDTO(event.getGameState());
            // ui is updated
            if (ui != null) ui.restoreGameState(event);
        });
    }

    /**
     * Game phase change events are processed.
     *
     * @param event the game phase changed event
     */
    public void onGamePhaseChanged(GamePhaseChangedEvent event) {
        runOnUIThread(() -> {
            // state is updated
            lightGameState.updatePhaseChange(event.getPhase(), event.getCurrentPlayer());
            // ui is updated
            if (ui != null) ui.updateGamePhase(event);
        });
    }

    /**
     * Round end events are processed.
     *
     * @param event the round-ended event
     */
    public void onRoundEnded(RoundEndedEvent event) {
        runOnUIThread(() -> {
            List<String> upperTribe = event.getUpperTribeRow().stream().map(CardDTO::getId).collect(Collectors.toList());
            List<String> lowerTribe = event.getLowerTribeRow().stream().map(CardDTO::getId).collect(Collectors.toList());
            List<String> upperBuilding = event.getUpperBuildingRow().stream().map(CardDTO::getId).collect(Collectors.toList());
            List<String> lowerBuilding = event.getLowerBuildingRow().stream().map(CardDTO::getId).collect(Collectors.toList());

            lightGameState.updateRound(
                    event.getNewRound(),
                    event.getNewEra(),
                    upperTribe,
                    lowerTribe,
                    upperBuilding,
                    lowerBuilding,
                    event.getTribeDeckSize(),
                    event.getTribeDeckTopCardEra()
            );

            // ui is updated
            if (ui != null) ui.showRoundEnded(event);
        });
    }

    /**
     * Game end events are processed.
     *
     * @param event the game ended event
     */
    public void onGameEnded(GameEndedEvent event) {
        runOnUIThread(() -> {
            // ui is updated
            if (ui != null) ui.showGameEnded(event);
        });
    }

    /**
     * Leaderboard event is processed.
     *
     * @param event the leaderboard event
     */
    public void onLeaderboardReceived(LeaderboardEvent event) {
        runOnUIThread(() -> {
            // ui is updated
            if (ui != null) ui.showLeaderboard(event);
        });
    }

    /**
     * Totem placement events are processed.
     *
     * @param event the totem placed event
     */
    public void onTotemPlaced(TotemPlacedEvent event) {
        runOnUIThread(() -> {
            // state is updated
            lightGameState.updateTotemPlacement(event.getPlayer(), event.getOfferTileId(), event.getNextPlayer());
            // ui is updated
            if (ui != null) ui.showTotemPlaced(event);
        });
    }

    /**
     * Action resolution event is processed.
     *
     * @param event the action resolved event
     */
    public void onActionResolved(ActionResolvedEvent event) {
        runOnUIThread(() -> {
            // card acquisitions and main stats are updated
            lightGameState.updateActionResolution(
                    event.getPlayer(),
                    event.getAcquiredCharacterCardIds(),
                    event.getAcquiredBuildingCardIds(),
                    event.getStatsChange()
            );

            // totem placement is updated
            lightGameState.returnTotem(event.getPlayer(), event.getTurnOrderPosition());

            // ui is updated
            if (ui != null) ui.showActionResolved(event);
        });
    }

    /**
     * Event card resolution events are processed.
     *
     * @param event the event resolved event
     */
    public void onEventResolved(EventResolvedEvent event) {
        runOnUIThread(() -> {
            // state is updated
            lightGameState.updateStats(event.getStatsChanges());
            // ui is updated
            if (ui != null) ui.showEventResolved(event);
        });
    }

    /**
     * Extra acquisition resolution events are processed.
     *
     * @param event the extra acquisition resolved event
     */
    public void onExtraAcquisitionResolved(ExtraAcquisitionResolvedEvent event) {
        runOnUIThread(() -> {
            // state is updated
            lightGameState.updateExtraAcquisitionResolution(
                    event.getPlayer(),
                    event.getAcquiredCardId(),
                    event.getStatsChange()
            );

            // ui is updated
            if (ui != null) ui.showExtraAcquisition(event);
        });
    }

    /**
     * Invalid action errors are processed.
     *
     * @param event the game error event
     */
    public void onInvalidAction(GameErrorEvent event) {
        runOnUIThread(() -> {
            // ui is updated
            if (ui != null) ui.showGameError(event);
        });
    }

    /**
     * Player disconnection events are processed.
     *
     * @param event the player disconnected event
     */
    public void onPlayerDisconnected(PlayerDisconnectedEvent event) {
        runOnUIThread(() -> {
            // state is updated
            lightGameState.updatePlayerConnection(event.getPlayer(), false);
            // ui is updated
            if (ui != null) ui.showPlayerDisconnected(event);
        });
    }

    /**
     * Player reconnection events are processed.
     *
     * @param event the player reconnected event
     */
    public void onPlayerReconnected(PlayerReconnectedEvent event) {
        runOnUIThread(() -> {
            // state is updated
            lightGameState.updatePlayerConnection(event.getPlayer(), true);
            // ui is updated
            if (ui != null) ui.showPlayerReconnected(event);
        });
    }

    /**
     * Game suspension events are processed.
     *
     * @param event the game suspended event
     */
    public void onGameSuspended(GameSuspendedEvent event) {
        runOnUIThread(() -> {
            // ui is updated
            if (ui != null) ui.showGameSuspended(event);
        });
    }

    /**
     * Game resume events are processed.
     *
     * @param event the game resumed event
     */
    public void onGameResumed(GameResumedEvent event) {
        runOnUIThread(() -> {
            // ui is updated
            if (ui != null) ui.showGameResumed(event);
        });
    }

    /**
     * Fatal error is processed.
     *
     * @param message the error message
     */
    public void onFatalError(String message) {
        runOnUIThread(() -> {
            // ui is notified of the disconnection
            if (ui != null) ui.showConnectionLost(message);
        });
    }

    /**
     * Task is executed on the ui thread.
     *
     * @param task the runnable task
     */
    private void runOnUIThread(Runnable task) {
        // task is submitted to the ui executor queue
        this.uiExecutor.submit(task);
    }

    /**
     * Light game state is retrieved.
     *
     * @return the current light game state
     */
    public LightGameState getLightGameState() {
        // light game state is returned
        return this.lightGameState;
    }

    /**
     * Local player nickname is retrieved.
     *
     * @return the local player nickname
     */
    public String getMyNickname() {
        // my nickname is returned
        return this.myNickname;
    }

    /**
     * Application termination is requested.
     */
    public void requestExit() {
        // resources are released and the connection is gracefully closed
        shutdown();

        if (ui != null) {
            // termination is delegated to the specific ui implementation
            ui.showQuitConfirmation();
        }
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
        }
    }
}
