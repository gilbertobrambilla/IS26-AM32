package it.polimi.ingsw.am32.client;

import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;
import it.polimi.ingsw.am32.network.client.MessageDispatcher;
import it.polimi.ingsw.am32.network.client.VirtualServer;
import it.polimi.ingsw.am32.dto.messages.MessageDTO;

/**
 * Controller handling incoming network events and updating the user interface.
 */
public class ClientController {

    private final VirtualServer virtualServer;
    private final MessageDispatcher dispatcher;

    // todo UI: private UI ui;

    /**
     * Constructs a new ClientController.
     *
     * @param virtualServer The virtual server instance handling network communications.
     */
    public ClientController(VirtualServer virtualServer) {
        this.virtualServer = virtualServer;
        this.dispatcher = new MessageDispatcher(this);
    }

    /**
     * Processes a generic message received from the network.
     *
     * @param message The message received from the server.
     */
    public void onMessageReceived(MessageDTO message) {
        // double dispatch is triggered
        message.accept(this.dispatcher);
    }

    // =========================================================================
    // LOBBY PHASE METHODS
    // =========================================================================

    /**
     * Handles the result of a connection attempt.
     *
     * @param event The connection result event.
     */
    public void onConnectionResult(ConnectionResultEvent event) {
        runOnUIThread(() -> {
            // ui.showConnectionResult(event.isSuccessful(), event.getMessage());
            System.out.println("[CONTROLLER] Connection result: " + event.isSuccess() + " - " + event.getMessage());
        });
    }

    /**
     * Updates the available games list in the lobby.
     *
     * @param event The available games list event.
     */
    public void onAvailableGamesListUpdate(AvailableGamesListEvent event) {
        runOnUIThread(() -> {
            // ui.updateAvailableGames(event.getAvailableGames());
            System.out.println("[CONTROLLER] Available games list is updated.");
        });
    }

    /**
     * Updates the state of a specific lobby room.
     *
     * @param event The lobby state updated event.
     */
    public void onLobbyRoomUpdated(LobbyStateUpdatedEvent event) {
        runOnUIThread(() -> {
            // ui.updateLobbyRoom(event);
            System.out.println("[CONTROLLER] Room " + event.getGameId() + " updated. Players: " + event.getCurrentPlayers() + "/" + event.getTargetPlayers());
        });
    }

    // =========================================================================
    // GAME LIFECYCLE METHODS
    // =========================================================================

    /**
     * Handles the start of a new game.
     *
     * @param event The game started event.
     */
    public void onGameStarted(GameStartedEvent event) {
        runOnUIThread(() -> {
            // ui.showGameScreen(event.getInitialState());
            System.out.println("[CONTROLLER] Game started. First turn: " + event.getNextPlayer());
        });
    }

    /**
     * Updates the full state of the game.
     *
     * @param event The game state event containing the full snapshot.
     */
    public void onGameStateUpdate(GameStateEvent event) {
        runOnUIThread(() -> {
            // ui.refreshFullBoard(event.getGameState());
            System.out.println("[CONTROLLER] Full game state is restored.");
        });
    }

    /**
     * Handles the transition to a new game phase.
     *
     * @param event The game phase changed event.
     */
    public void onGamePhaseChanged(GamePhaseChangedEvent event) {
        runOnUIThread(() -> {
            // ui.updateGamePhase(event.getNewPhase(), event.getNextPlayer());
            System.out.println("[CONTROLLER] Phase changed to: " + event.getPhase() + ". Turn of: " + event.getNextPlayer());
        });
    }

    /**
     * Handles the end of a round.
     *
     * @param event The round ended event.
     */
    public void onRoundEnded(RoundEndedEvent event) {
        runOnUIThread(() -> {
            // ui.processRoundEnd(event);
            System.out.println("[CONTROLLER] Round " + event.getRound() + " ended. Moving to era " + event.getEra());
        });
    }

    /**
     * Handles the end of the game and displays results.
     *
     * @param event The game ended event.
     */
    public void onGameEnded(GameEndedEvent event) {
        runOnUIThread(() -> {
            // ui.showEndGameScreen(event.getResult());
            System.out.println("[CONTROLLER] Game is over. Winners: " + event.getResults().getWinners());
        });
    }

    // =========================================================================
    // GAMEPLAY ACTION METHODS
    // =========================================================================

    /**
     * Processes a totem placement action.
     *
     * @param event The totem placed event.
     */
    public void onTotemPlaced(TotemPlacedEvent event) {
        runOnUIThread(() -> {
            // ui.updateTotem(event);
            System.out.println("[CONTROLLER] Totem placed by " + event.getPlayer() + " on tile " + event.getOfferTileId());
        });
    }

    /**
     * Processes the resolution of a standard action.
     *
     * @param event The action resolved event.
     */
    public void onActionResolved(ActionResolvedEvent event) {
        runOnUIThread(() -> {
            // ui.updatePlayerBoard(event);
            System.out.println("[CONTROLLER] Action resolved for " + event.getPlayer());
        });
    }

    /**
     * Processes the resolution of an event card.
     *
     * @param event The event resolved event.
     */
    public void onEventResolved(EventResolvedEvent event) {
        runOnUIThread(() -> {
            // ui.showEventEffect(event);
            System.out.println("[CONTROLLER] Event card " + event.getEventCardId() + " resolved.");
        });
    }

    /**
     * Processes an extra acquisition action.
     *
     * @param event The extra acquisition resolved event.
     */
    public void onExtraAcquisitionResolved(ExtraAcquisitionResolvedEvent event) {
        runOnUIThread(() -> {
            // ui.updateExtraAcquisition(event);
            System.out.println("[CONTROLLER] Extra acquisition resolved for " + event.getPlayer() + ". Card: " + event.getAcquiredCardId());
        });
    }

    // =========================================================================
    // RESILIENCE AND ERROR METHODS
    // =========================================================================

    /**
     * Handles an invalid action error sent by the server.
     *
     * @param event The invalid action event.
     */
    public void onInvalidAction(InvalidActionEvent event) {
        runOnUIThread(() -> {
            // ui.showErrorMessage(event.getErrorMessage());
            System.err.println("[CONTROLLER] Action denied: " + event.getErrorMessage());
        });
    }

    /**
     * Handles the disconnection of a player.
     *
     * @param event The player disconnected event.
     */
    public void onPlayerDisconnected(PlayerDisconnectedEvent event) {
        runOnUIThread(() -> {
            // ui.showPlayerDisconnectedNotification(event.getNickname());
            System.out.println("[CONTROLLER] Player disconnected: " + event.getPlayer());
        });
    }

    /**
     * Handles the reconnection of a player.
     *
     * @param event The player reconnected event.
     */
    public void onPlayerReconnected(PlayerReconnectedEvent event) {
        runOnUIThread(() -> {
            // ui.showPlayerReconnectedNotification(event.getNickname());
            System.out.println("[CONTROLLER] Player reconnected: " + event.getPlayer());
        });
    }

    /**
     * Handles the suspension of the current game.
     *
     * @param event The game suspended event.
     */
    public void onGameSuspended(GameSuspendedEvent event) {
        runOnUIThread(() -> {
            // ui.showSuspendedScreen();
            System.out.println("[CONTROLLER] Game suspended. Waiting for players...");
        });
    }

    /**
     * Handles the resumption of a suspended game.
     *
     * @param event The game resumed event.
     */
    public void onGameResumed(GameResumedEvent event) {
        runOnUIThread(() -> {
            // ui.hideSuspendedScreen();
            System.out.println("[CONTROLLER] Players threshold restored. Game resumed.");
        });
    }

    // =========================================================================
    // THREAD MANAGEMENT HELPERS
    // =========================================================================

    /**
     * Executes the given task on the main user interface thread.
     *
     * @param task The runnable task to be executed.
     */
    private void runOnUIThread(Runnable task) {
        // task is executed synchronously for terminal testing
        task.run();
    }
}
