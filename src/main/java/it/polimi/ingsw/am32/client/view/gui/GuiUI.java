package it.polimi.ingsw.am32.client.view.gui;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.view.UI;
import it.polimi.ingsw.am32.client.view.gui.controllers.ViewController;
import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * JavaFX implementation of the UI interface.
 * It receives network events from the ClientController and safely routes them
 * to the JavaFX Application Thread using Platform.runLater(), delegating
 * the specific rendering logic to the current active ViewController.
 */
public class GuiUI implements UI {

    private final ClientController controller;

    /**
     * Instantiates the GUI and links it to the main controller.
     *
     * @param controller the main ClientController.
     */
    public GuiUI(ClientController controller) {
        this.controller = controller;
        this.controller.setUI(this);
    }

    // --- CONNECTION PHASE ---

    @Override
    public void showConnectionResult(ConnectionResultEvent event) {
        Platform.runLater(() -> {
            if (event.isSuccess()) {
                System.out.println("[GUI] Connection successful!");
                SceneManager.switchScene(ViewPaths.LOBBY);
                ViewController ctrl = SceneManager.getCurrentController();
                if (ctrl != null) ctrl.onConnectionResult(event);
            } else {
                System.err.println("[GUI] Connection failed: " + event.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Connection Error");
                alert.setHeaderText("Failed to connect to the server");
                alert.setContentText(event.getMessage());
                alert.showAndWait();
            }
        });
    }

    @Override
    public void showLoginResult(LoginResultEvent event) {

    }

    @Override
    public void showConnectionLost(String message) {

    }

    @Override
    public void showQuitConfirmation() {

    }

    @Override
    public void updateAvailableLobbies(AvailableLobbiesEvent event) {

    }

    @Override
    public void updateLobbyRoom(LobbyRoomUpdateEvent event) {

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
            ViewController ctrl = SceneManager.getCurrentController();
            if (ctrl != null) ctrl.onGameResults(event);
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
