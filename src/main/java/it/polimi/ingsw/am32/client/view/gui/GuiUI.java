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

    /**
     * The GUI is instantiated and linked to the main controller.
     *
     * @param controller the main ClientController
     */
    public GuiUI(ClientController controller) {
        controller.setUI(this);
    }

    // --- CONNECTION PHASE ---

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
    @Override
    public void showConnectionLost(String message) {
        SceneManager.forceDisconnect(message);
    }

    @Override
    public void showQuitConfirmation() {
        GuiLogger.info("[GUI] Application closed by user");
        SceneManager.exitApplication();
    }

    @Override
    public void updateAvailableLobbies(AvailableLobbiesEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) {
                ctrl.onAvailableLobbies(event);
            }
        });
    }

    @Override
    public void updateLobbyRoom(LobbyRoomUpdateEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) {
                ctrl.onLobbyRoomUpdate(event);
            }
        });
    }

    // --- LOBBY PHASE ---

    @Override
    public void showLobbyError(LobbyErrorEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onLobbyError(event);
        });
    }

    // --- GAME LIFECYCLE ---

    @Override
    public void showGameStarted(GameStartedEvent event) {
        SceneManager.switchScene(ViewPaths.IN_GAME);
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onGameStarted(event);
        });
    }

    @Override
    public void restoreGameState(GameStateEvent event) {
        SceneManager.switchScene(ViewPaths.IN_GAME);
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onGameStateRestored(event);
        });
    }

    @Override
    public void updateGamePhase(GamePhaseChangedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onGamePhaseUpdated(event);
        });
    }

    @Override
    public void showRoundEnded(RoundEndedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onRoundSummary(event);
        });
    }

    @Override
    public void showGameEnded(GameEndedEvent event) {
        Platform.runLater(() -> {
            SceneManager.switchScene(ViewPaths.END_GAME, ctrl -> ctrl.onGameResults(event));
        });
    }

    @Override
    public void showLeaderboard(LeaderboardEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onLeaderboard(event);
        });
    }

    // --- GAMEPLAY ACTIONS ---

    @Override
    public void showTotemPlaced(TotemPlacedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onTotemPlaced(event);
        });
    }

    @Override
    public void showActionResolved(ActionResolvedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onActionResolved(event);
        });
    }

    @Override
    public void showEventResolved(EventResolvedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onEventResolved(event);
        });
    }

    @Override
    public void showExtraAcquisition(ExtraAcquisitionResolvedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onExtraAcquisition(event);
        });
    }

    @Override
    public void showPlayerFocus(String nickname) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onPlayerFocus(nickname);
        });
    }

    // --- RESILIENCE AND ERRORS ---

    @Override
    public void showGameError(GameErrorEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onError(event);
        });
    }

    @Override
    public void showPlayerDisconnected(PlayerDisconnectedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onPlayerDisconnected(event);
        });
    }

    @Override
    public void showPlayerReconnected(PlayerReconnectedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onPlayerReconnected(event);
        });
    }

    @Override
    public void showGameSuspended(GameSuspendedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onGameSuspended(event);
        });
    }

    @Override
    public void showGameResumed(GameResumedEvent event) {
        Platform.runLater(() -> {
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onGameResumed(event);
        });
    }
}
