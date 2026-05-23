package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.model.LightGameState;
import it.polimi.ingsw.am32.client.model.LightPlayer;
import it.polimi.ingsw.am32.client.view.gui.components.CardView;
import it.polimi.ingsw.am32.client.view.gui.utils.AnimationUtils;
import it.polimi.ingsw.am32.client.view.gui.utils.GuiLogger;
import it.polimi.ingsw.am32.client.view.gui.utils.ImageCache;
import it.polimi.ingsw.am32.dto.messages.game.events.*;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class InGameViewController implements ViewController {

    private final ClientController clientController;

    @FXML private TopBarViewController topBarController;
    @FXML private PlayerDashboardViewController playerDashboardController;
    @FXML private OpponentsSidebarViewController opponentsSidebarController;

    @FXML private Label notificationLabel;

    @FXML private VBox suspensionOverlay;
    @FXML private Label suspensionMessageLabel;
    @FXML private HBox toastContainer;
    @FXML private Label toastLabel;

    // Opponent focus overlay nodes.
    @FXML private StackPane opponentFocusOverlay;
    @FXML private Label focusNameLabel;
    @FXML private HBox focusCardsContainer;

    @FXML private BoardViewController boardController;

    // --- HELPER CARD NODES ---
    @FXML private ImageView helperCardImageView;
    @FXML private StackPane helperCardOverlay;
    @FXML private ImageView largeHelperImageView;

    // Nuove variabili per gestire il flip della carta helper:
    private Image helperImgFront;
    private Image helperImgBack;
    private boolean isHelperFront = true;

    public InGameViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
        clearAllContainers();
        if (opponentFocusOverlay != null) {
            opponentFocusOverlay.setVisible(false);
        }

        if (helperCardOverlay != null) {
            helperCardOverlay.setVisible(false);
        }

       
        helperImgFront = ImageCache.getInstance().getImage("/assets/images/ui_assets/helper_card.png");
        helperImgBack = ImageCache.getInstance().getImage("/assets/images/ui_assets/helper_card_back.png");

        if (helperImgFront != null && helperCardImageView != null && largeHelperImageView != null) {
            helperCardImageView.setImage(helperImgFront);
            largeHelperImageView.setImage(helperImgFront);
        }
    }

    public void fullRender(LightGameState state) {
        if (state == null) return;

        if (topBarController != null) {
            String phase = state.getCurrentPhase() == null ? "WAITING" : state.getCurrentPhase().name();
            topBarController.updateStatusBar(state.getCurrentEra(), state.getCurrentRound(), phase);
        }

        String myNick = state.getMyNickname();

        if (opponentsSidebarController != null) {
            opponentsSidebarController.updateOpponents(state.getPlayers(), myNick);
        }

        if (playerDashboardController != null) {
            playerDashboardController.updateDashboard(state.getPlayers().get(myNick));
        }

        if (boardController != null) {
            boardController.wipeAndRebuild(state);
        }
    }

    private void clearAllContainers() {
        if (notificationLabel != null) notificationLabel.setVisible(false);
        if (toastContainer != null) toastContainer.setVisible(false);
        if (opponentFocusOverlay != null) opponentFocusOverlay.setVisible(false);

        if (topBarController != null) topBarController.initialize();
        if (boardController != null) boardController.clearContainers();
        if (playerDashboardController != null) playerDashboardController.clearDashboard();
        if (opponentsSidebarController != null) opponentsSidebarController.clearSidebar();
    }

    public void showNotification(String message) {
        GuiLogger.info("[GUI] " + message);

        if (notificationLabel != null) {
            notificationLabel.setText("📢 " + message);
            notificationLabel.setVisible(true);

            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(5));
            pause.setOnFinished(e -> notificationLabel.setVisible(false));
            pause.play();
        }
    }

    // --- OPPONENT FOCUS OVERLAY ---

    @FXML
    public void handleCloseFocus(MouseEvent event) {
        if (opponentFocusOverlay != null) {
            opponentFocusOverlay.setVisible(false);
        }
    }

    @Override
    public void onPlayerFocus(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            handleCloseFocus(null);
            return;
        }

        LightGameState state = clientController.getLightGameState();
        if (state == null) return;

        LightPlayer player = state.getPlayers().get(nickname);
        if (player == null) return;

        if (focusNameLabel != null) {
            focusNameLabel.setText(nickname.toUpperCase());
        }

        if (focusCardsContainer != null) {
            focusCardsContainer.getChildren().clear();

            for (String cardId : player.getCharacterCards()) {
                CardView cardView = new CardView(cardId, 100.0, 150.0);
                focusCardsContainer.getChildren().add(cardView);
            }

            for (String cardId : player.getBuildingCards()) {
                CardView cardView = new CardView(cardId, 100.0, 150.0);
                focusCardsContainer.getChildren().add(cardView);
            }

            if (focusCardsContainer.getChildren().isEmpty()) {
                Label emptyLabel = new Label("No cards acquired yet");
                emptyLabel.getStyleClass().add("text-dim");
                focusCardsContainer.getChildren().add(emptyLabel);
            }
        }

        if (opponentFocusOverlay != null) {
            opponentFocusOverlay.setVisible(true);
        }
    }

    // --- HELPER CARD EVENT HANDLERS ---

    @FXML
    public void handleOpenHelperZoom(MouseEvent event) {
        if (helperCardOverlay != null) {

            isHelperFront = true;
            if (largeHelperImageView != null && helperImgFront != null) {
                largeHelperImageView.setImage(helperImgFront);
            }
            helperCardOverlay.setVisible(true);
        }
    }

    @FXML
    public void handleCloseHelperZoom(MouseEvent event) {
        if (helperCardOverlay != null) {
            helperCardOverlay.setVisible(false);
        }
    }

    @FXML
    public void handleFlipHelperCard(MouseEvent event) {

        event.consume();

        if (largeHelperImageView == null || helperImgFront == null || helperImgBack == null) return;


        ScaleTransition shrink = new ScaleTransition(Duration.millis(150), largeHelperImageView);
        shrink.setToX(0);

        ScaleTransition expand = new ScaleTransition(Duration.millis(150), largeHelperImageView);
        expand.setToX(1);


        shrink.setOnFinished(e -> {
            isHelperFront = !isHelperFront;
            largeHelperImageView.setImage(isHelperFront ? helperImgFront : helperImgBack);
            expand.play();
        });

        shrink.play();
    }

    @FXML
    public void consumeClick(MouseEvent event) {
        event.consume();
    }

    // --- EVENT ROUTING ---

    @Override
    public void onGameStarted(GameStartedEvent event) {
        fullRender(clientController.getLightGameState());
    }

    @Override
    public void onGameStateRestored(GameStateEvent event) {
        if (suspensionOverlay != null) {
            suspensionOverlay.setVisible(false);
        }
        fullRender(clientController.getLightGameState());
    }

    @Override
    public void onGamePhaseUpdated(GamePhaseChangedEvent event) {
        GuiLogger.info("[GUI] phase changed to: " + event.getPhase());

        if (boardController != null) {
            boardController.resetPhaseSelection(event.getPhase());
        }
        fullRender(clientController.getLightGameState());
    }

    @Override
    public void onRoundSummary(RoundEndedEvent event) {
        fullRender(clientController.getLightGameState());
        showNotification("Round " + event.getRound() + " started.");
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
            toastLabel.setText("⚠️ " + event.getErrorMessage().toUpperCase());
            AnimationUtils.playToastAnimation(toastContainer);
        }
        GuiLogger.error("[GUI] server rejected action: " + event.getErrorMessage());
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
        if (suspensionOverlay != null) {
            suspensionOverlay.setVisible(true);
        }
        showNotification("game suspended due to missing players.");
    }

    @Override
    public void onGameResumed(GameResumedEvent event) {
        if (suspensionOverlay != null) {
            suspensionOverlay.setVisible(false);
        }
        showNotification("game resumed.");
        fullRender(clientController.getLightGameState());
    }
}