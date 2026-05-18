package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.model.LightPlayer;
import it.polimi.ingsw.am32.client.view.gui.components.CardView;
import it.polimi.ingsw.am32.client.view.gui.components.OpponentView;
import it.polimi.ingsw.am32.client.view.gui.utils.AnimationUtils;
import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.setup.CharacterCardDTO;
import it.polimi.ingsw.am32.model.enums.GamePhase;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;

import java.util.*;

public class InGameViewController implements ViewController {

    private final ClientController clientController;

    // fxml components injected from the main layout
    @FXML private TopBarViewController topBarController;
    @FXML private PlayerDashboardViewController playerDashboardController;
    @FXML private OpponentsSidebarViewController opponentsSidebarController;

    @FXML private Label notificationLabel;

    // suspension overlay components
    @FXML private VBox suspensionOverlay;
    @FXML private Label suspensionMessageLabel;
    @FXML private HBox toastContainer;
    @FXML private Label toastLabel;

    // nested controller injected automatically by javafx
    @FXML private BoardViewController boardController;

    public InGameViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
        // ui is reset to blank state upon initialization
        clearAllContainers();
    }

    public void fullRender(LightGameState state) {
        if (state == null) return;

        // top bar update is delegated
        if (topBarController != null) {
            topBarController.updateStatusBar(state.getCurrentEra(), state.getCurrentRound(), state.getCurrentPhase().name());
        }

        String myNick = state.getMyNickname();

        // opponents rendering is delegated
        if (opponentsSidebarController != null) {
            opponentsSidebarController.updateOpponents(state.getPlayers(), myNick);
        }

        // dashboard rendering is delegated
        if (playerDashboardController != null) {
            playerDashboardController.updateDashboard(state.getPlayers().get(myNick));
        }

        // complex board rendering is delegated
        if (boardController != null) {
            boardController.wipeAndRebuild(state);
        }
    }


    private void clearAllContainers() {
        if (notificationLabel != null) notificationLabel.setVisible(false);

        // nested controllers caches are cleared
        if (topBarController != null) topBarController.initialize();
        if (boardController != null) boardController.clearContainers();
        if (playerDashboardController != null) playerDashboardController.clearDashboard();
        if (opponentsSidebarController != null) opponentsSidebarController.clearSidebar();
    }

    public void showNotification(String message) {
        System.out.println("[gui] " + message);

        if (notificationLabel != null) {
            notificationLabel.setText("📢 " + message);
            notificationLabel.setVisible(true);

            // transition is handled safely on the ui thread
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(5));
            pause.setOnFinished(e -> notificationLabel.setVisible(false));
            pause.play();
        }
    }

    // --- event routing ---

    @Override
    public void onGameStarted(GameStartedEvent event) {
        fullRender(clientController.getLightGameState());
    }

    @Override
    public void onGameStateRestored(GameStateEvent event) {
        // suspension overlay is hidden in case we reconnected to a running game
        if (suspensionOverlay != null) {
            suspensionOverlay.setVisible(false);
        }
        fullRender(clientController.getLightGameState());
    }

    @Override
    public void onGamePhaseUpdated(GamePhaseChangedEvent event) {
        System.out.println("[gui] phase changed to: " + event.getPhase());

        // local selections are cleared if phase changes
        if (boardController != null) {
            boardController.resetPhaseSelection(event.getPhase());
        }
        fullRender(clientController.getLightGameState());
    }

    @Override
    public void onTotemPlaced(TotemPlacedEvent event) {
        fullRender(clientController.getLightGameState());
    }

    @Override
    public void onActionResolved(ActionResolvedEvent event) {
        fullRender(clientController.getLightGameState());
    }

    @Override
    public void onEventResolved(EventResolvedEvent event) {
        fullRender(clientController.getLightGameState());
    }

    @Override
    public void onExtraAcquisition(ExtraAcquisitionResolvedEvent event) {
        fullRender(clientController.getLightGameState());
    }

    @Override
    public void onError(GameErrorEvent event) {
        if (toastLabel != null && toastContainer != null) {
            // error message from server is extracted and assigned to the label
            toastLabel.setText("⚠️ " + event.getErrorMessage().toUpperCase());

            // toast animation sequence is triggered
            AnimationUtils.playToastAnimation(toastContainer);
        }

        // console fallback logging is executed
        System.err.println("[GUI-ERROR] server rejected action: " + event.getErrorMessage());
    }

    @Override
    public void onPlayerDisconnected(PlayerDisconnectedEvent event) {
        showNotification(event.getPlayer() + " disconnected.");
        LightGameState state = clientController.getLightGameState();
        if (state != null && opponentsSidebarController != null) {
            opponentsSidebarController.updateOpponents(state.getPlayers(), state.getMyNickname());
        }
    }

    @Override
    public void onPlayerReconnected(PlayerReconnectedEvent event) {
        showNotification(event.getPlayer() + " reconnected.");
        LightGameState state = clientController.getLightGameState();
        if (state != null && opponentsSidebarController != null) {
            opponentsSidebarController.updateOpponents(state.getPlayers(), state.getMyNickname());
        }
    }

    @Override
    public void onGameSuspended(GameSuspendedEvent event) {
        // interaction is blocked by showing the overlay
        if (suspensionOverlay != null) {
            suspensionOverlay.setVisible(true);
        }
        showNotification("game suspended due to missing players.");
    }

    @Override
    public void onGameResumed(GameResumedEvent event) {
        // interaction is restored by hiding the overlay
        if (suspensionOverlay != null) {
            suspensionOverlay.setVisible(false);
        }
        showNotification("game resumed.");
        fullRender(clientController.getLightGameState());
    }
}
