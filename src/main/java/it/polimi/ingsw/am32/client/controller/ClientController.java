package it.polimi.ingsw.am32.client.controller;

import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.view.UI;
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

    private final ExecutorService networkExecutor;

    /**
     * Client controller is instantiated.
     *
     * @param virtualServer the virtual server instance.
     */
    public ClientController(VirtualServer virtualServer) {
        this.virtualServer = virtualServer;
        this.dispatcher = new MessageDispatcher(this);
        this.networkExecutor = Executors.newSingleThreadExecutor();
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
        networkExecutor.submit(() -> virtualServer.sendLobbyCommand(cmd, myNickname));
    }

    /**
     * Game join is requested.
     *
     * @param gameId the id of the game.
     */
    public void requestJoinGame(int gameId) {
        JoinGameCommand cmd = new JoinGameCommand(gameId);
        networkExecutor.submit(() -> virtualServer.sendLobbyCommand(cmd, myNickname));
    }

    /**
     * Totem placement is requested.
     *
     * @param tileId the id of the tile.
     */
    public void requestPlaceTotem(String tileId) {
        PlaceTotemCommand cmd = new PlaceTotemCommand(tileId);
        networkExecutor.submit(() -> virtualServer.sendGameCommand(cmd, myNickname));
    }

    /**
     * Action resolution is requested.
     *
     * @param characters the character cards.
     * @param buildings the building cards.
     */
    public void requestResolveAction(List<String> characters, List<String> buildings) {
        ResolveActionCommand cmd = new ResolveActionCommand(characters, buildings);
        networkExecutor.submit(() -> virtualServer.sendGameCommand(cmd, myNickname));
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

    public void onLobbyError(LobbyErrorEvent event) {
        runOnUIThread(() -> {
            if (ui != null) ui.showLobbyError(event);
        });
    }

    public void onConnectionResult(ConnectionResultEvent event) {
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
     * Task is executed on the ui thread.
     *
     * @param task the runnable task.
     */
    private void runOnUIThread(Runnable task) {
        task.run();
    }

    public LightGameState getLightGameState() {
        return this.lightGameState;
    }

    public String getMyNickname() {
        return this.myNickname;
    }

    public void shutdown() {
        if (networkExecutor != null && !networkExecutor.isShutdown()) {
            networkExecutor.shutdownNow();
        }
    }
}
