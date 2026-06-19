package it.polimi.ingsw.am32.client.view.gui;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.view.UI;
import it.polimi.ingsw.am32.client.view.gui.utils.GuiLogger;
import it.polimi.ingsw.am32.client.view.gui.controllers.ViewController;
import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;
import javafx.application.Platform;

/**
 * JavaFX implementation of the ui interface is represented.
 * Network events are safely routed to the JavaFX application thread.
 * Rendering logic is delegated to the current active view controller.
 */
public class GuiUI implements UI {

    private final ClientController controller;

    /**
     * The GUI is instantiated and linked to the main controller.
     *
     * @param controller the main ClientController
     */
    public GuiUI(ClientController controller) {
        this.controller = controller;
        this.controller.setUI(this);
    }

    // connection phase

    /**
     * Connection result is routed to the active scene.
     *
     * @param event the connection result event
     */
    @Override
    public void showConnectionResult(ConnectionResultEvent event) {
        Platform.runLater(() -> {
            if (event.isSuccess()) {
                GuiLogger.info("[GUI] Connection successful");
                SceneManager.switchScene(ViewPaths.LOGIN, ctrl -> ctrl.onConnectionResult(event));
            } else {
                GuiLogger.error("[GUI] Connection failed: " + event.getMessage());
                ViewController ctrl = SceneManager.getCurrentController();
                if (ctrl != null) ctrl.onConnectionResult(event);
            }
        });
    }

    /**
     * Login result is routed to the active scene.
     *
     * @param event the login result event
     */
    @Override
    public void showLoginResult(LoginResultEvent event) {
        Platform.runLater(() -> {
            if (event.isSuccess()) {
                GuiLogger.info("[GUI] Login successful");
                SceneManager.switchScene(ViewPaths.LOBBY_SELECTION, ctrl -> ctrl.onLoginResult(event));
            } else {
                GuiLogger.error("[GUI] Login failed: " + event.getMessage());
                ViewController ctrl = SceneManager.getCurrentController();
                if (ctrl != null) ctrl.onLoginResult(event);
            }
        });
    }

    /**
     * Fatal connection loss is displayed, and the connection scene is restored.
     *
     * @param message the disconnection reason
     */
    @Override
    public void showConnectionLost(String message) {
        SceneManager.forceDisconnect(message);
    }

    /**
     * User-requested application termination is performed.
     */
    @Override
    public void showQuitConfirmation() {
        Platform.runLater(() -> {
            GuiLogger.info("[GUI] Application closed by user");
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Available lobbies are routed to the active scene.
     *
     * @param event the available lobbies event
     */
    @Override
    public void updateAvailableLobbies(AvailableLobbiesEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) {
                ctrl.onAvailableLobbies(event);
            }
        });
    }

    /**
     * Lobby room updates are routed to the active scene.
     *
     * @param event the lobby room update event
     */
    @Override
    public void updateLobbyRoom(LobbyRoomUpdateEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) {
                ctrl.onLobbyRoomUpdate(event);
            }
        });
    }

    // lobby phase

    /**
     * Lobby errors are routed to the active scene.
     *
     * @param event the lobby error event
     */
    @Override
    public void showLobbyError(LobbyErrorEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onLobbyError(event);
        });
    }

    // game lifecycle

    /**
     * Game start is shown by switching to the in-game scene.
     *
     * @param event the game started event
     */
    @Override
    public void showGameStarted(GameStartedEvent event) {
        SceneManager.switchScene(ViewPaths.IN_GAME);
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onGameStarted(event);
        });
    }

    /**
     * A restored game state is shown by switching to the in-game scene.
     *
     * @param event the restored game state event
     */
    @Override
    public void restoreGameState(GameStateEvent event) {
        SceneManager.switchScene(ViewPaths.IN_GAME);
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onGameStateRestored(event);
        });
    }

    /**
     * Game phase updates are routed to the active scene.
     *
     * @param event the game phase changed event
     */
    @Override
    public void updateGamePhase(GamePhaseChangedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onGamePhaseUpdated(event);
        });
    }

    /**
     * Round summary updates are routed to the active scene.
     *
     * @param event the round-ended event
     */
    @Override
    public void showRoundEnded(RoundEndedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onRoundSummary(event);
        });
    }

    /**
     * Game results are shown by switching to the end-game scene.
     *
     * @param event the game ended event
     */
    @Override
    public void showGameEnded(GameEndedEvent event) {
        Platform.runLater(() -> {
            SceneManager.switchScene(ViewPaths.END_GAME, ctrl -> ctrl.onGameResults(event));
        });
    }

    /**
     * Leaderboard results are routed to the active scene.
     *
     * @param event the leaderboard event
     */
    @Override
    public void showLeaderboard(LeaderboardEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onLeaderboard(event);
        });
    }

    // gameplay actions

    /**
     * Totem placement updates are routed to the active scene.
     *
     * @param event the totem placed event
     */
    @Override
    public void showTotemPlaced(TotemPlacedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onTotemPlaced(event);
        });
    }

    /**
     * Action resolution updates are routed to the active scene.
     *
     * @param event the action resolved event
     */
    @Override
    public void showActionResolved(ActionResolvedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onActionResolved(event);
        });
    }

    /**
     * Event resolution updates are routed to the active scene.
     *
     * @param event the event resolved event
     */
    @Override
    public void showEventResolved(EventResolvedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onEventResolved(event);
        });
    }

    /**
     * Extra acquisition updates are routed to the active scene.
     *
     * @param event the extra acquisition resolved event
     */
    @Override
    public void showExtraAcquisition(ExtraAcquisitionResolvedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onExtraAcquisition(event);
        });
    }

    /**
     * Opponent focus requests are routed to the active scene.
     *
     * @param nickname the player nickname to focus on, or null to clear focus
     */
    @Override
    public void showPlayerFocus(String nickname) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onPlayerFocus(nickname);
        });
    }

    // resilience and errors

    /**
     * Game errors are routed to the active scene.
     *
     * @param event the game error event
     */
    @Override
    public void showGameError(GameErrorEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onError(event);
        });
    }

    /**
     * Player disconnection events are routed to the active scene.
     *
     * @param event the player disconnected event
     */
    @Override
    public void showPlayerDisconnected(PlayerDisconnectedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onPlayerDisconnected(event);
        });
    }

    /**
     * Player reconnection events are routed to the active scene.
     *
     * @param event the player reconnected event
     */
    @Override
    public void showPlayerReconnected(PlayerReconnectedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onPlayerReconnected(event);
        });
    }

    /**
     * Game suspension events are routed to the active scene.
     *
     * @param event the game suspended event
     */
    @Override
    public void showGameSuspended(GameSuspendedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onGameSuspended(event);
        });
    }

    /**
     * Game resume events are routed to the active scene.
     *
     * @param event the game resumed event
     */
    @Override
    public void showGameResumed(GameResumedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onGameResumed(event);
        });
    }
}
