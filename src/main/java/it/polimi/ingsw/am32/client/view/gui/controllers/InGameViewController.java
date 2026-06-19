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

/**
 * The main in-game scene controller is represented.
 */
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

    // opponent focus overlay nodes
    @FXML private StackPane opponentFocusOverlay;
    @FXML private Label focusNameLabel;
    @FXML private HBox focusCardsContainer;

    @FXML private BoardViewController boardController;

    // helper card nodes
    @FXML private ImageView helperCardImageView;
    @FXML private StackPane helperCardOverlay;
    @FXML private ImageView largeHelperImageView;

    // helper card flip state is tracked
    private Image helperImgFront;
    private Image helperImgBack;
    private boolean isHelperFront = true;

    /**
     * Dependency injection is handled through the constructor.
     *
     * @param clientController the main client controller
     */
    public InGameViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    /**
     * In-game child controllers, overlays, and helper card assets are initialized.
     */
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

    /**
     * All visible in-game areas are rendered from the current light state.
     *
     * @param state the light game state to render
     */
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

    /**
     * All child containers and transient overlays are reset.
     */
    private void clearAllContainers() {
        if (notificationLabel != null) notificationLabel.setVisible(false);
        if (toastContainer != null) toastContainer.setVisible(false);
        if (opponentFocusOverlay != null) opponentFocusOverlay.setVisible(false);

        if (topBarController != null) topBarController.initialize();
        if (boardController != null) boardController.clearContainers();
        if (playerDashboardController != null) playerDashboardController.clearDashboard();
        if (opponentsSidebarController != null) opponentsSidebarController.clearSidebar();
    }

    /**
     * A transient in-game notification is displayed.
     *
     * @param message the notification message
     */
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

    // opponent focus overlay

    /**
     * The opponent focus overlay is hidden.
     *
     * @param event the triggering mouse event
     */
    @FXML
    public void handleCloseFocus(MouseEvent event) {
        if (opponentFocusOverlay != null) {
            opponentFocusOverlay.setVisible(false);
        }
    }

    /**
     * The selected opponent's acquired cards are displayed.
     *
     * @param nickname the player nickname to focus on, or null to clear focus
     */
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
            focusNameLabel.setText(nickname);
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

    // helper card event handlers

    /**
     * The helper card zoom overlay is opened.
     *
     * @param event the triggering mouse event
     */
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

    /**
     * The helper card zoom overlay is closed.
     *
     * @param event the triggering mouse event
     */
    @FXML
    public void handleCloseHelperZoom(MouseEvent event) {
        if (helperCardOverlay != null) {
            helperCardOverlay.setVisible(false);
        }
    }

    /**
     * The large helper card image is flipped between front and back.
     *
     * @param event the triggering mouse event
     */
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

    /**
     * A mouse click is consumed to keep overlay clicks local.
     *
     * @param event the triggering mouse event
     */
    @FXML
    public void consumeClick(MouseEvent event) {
        event.consume();
    }

    // event routing

    /**
     * The game start event is rendered.
     *
     * @param event the game started event
     */
    @Override
    public void onGameStarted(GameStartedEvent event) {
        fullRender(clientController.getLightGameState());
    }

    /**
     * The restored game state is rendered and the suspension state is cleared.
     *
     * @param event the game state event
     */
    @Override
    public void onGameStateRestored(GameStateEvent event) {
        if (suspensionOverlay != null) {
            suspensionOverlay.setVisible(false);
        }
        fullRender(clientController.getLightGameState());
    }

    /**
     * The updated game phase is rendered.
     *
     * @param event the game phase changed event
     */
    @Override
    public void onGamePhaseUpdated(GamePhaseChangedEvent event) {
        GuiLogger.info("[GUI] phase changed to: " + event.getPhase());

        if (boardController != null) {
            boardController.resetPhaseSelection(event.getPhase());
        }
        fullRender(clientController.getLightGameState());
    }

    /**
     * The new round state is rendered.
     *
     * @param event the round-ended event
     */
    @Override
    public void onRoundSummary(RoundEndedEvent event) {
        fullRender(clientController.getLightGameState());
        showNotification("Round " + event.getNewRound() + " started.");
    }

    /**
     * Totem placement updates are rendered.
     *
     * @param event the totem placed event
     */
    @Override
    public void onTotemPlaced(TotemPlacedEvent event) {
        fullRender(clientController.getLightGameState());
        if (event.isAutoPlayed()) {
            showNotification("Totem placed by " + event.getPlayer() + " [action autoplayed].");
        }
    }

    /**
     * Action resolution updates are rendered.
     *
     * @param event the action resolved event
     */
    @Override
    public void onActionResolved(ActionResolvedEvent event) {
        fullRender(clientController.getLightGameState());
        if (event.isAutoPlayed()) {
            showNotification("Action resolved by " + event.getPlayer() + " [action autoplayed].");
        }
    }

    /**
     * Event resolution updates are rendered.
     *
     * @param event the event resolved event
     */
    @Override
    public void onEventResolved(EventResolvedEvent event) {
        fullRender(clientController.getLightGameState());
    }

    /**
     * Extra acquisition updates are rendered.
     *
     * @param event the extra acquisition resolved event
     */
    @Override
    public void onExtraAcquisition(ExtraAcquisitionResolvedEvent event) {
        fullRender(clientController.getLightGameState());
        if (event.isAutoPlayed()) {
            showNotification("Extra card acquired by " + event.getPlayer() + " [action autoplayed].");
        }
    }

    /**
     * Game errors are displayed as toast messages.
     *
     * @param event the game error event
     */
    @Override
    public void onError(GameErrorEvent event) {
        String formattedError = event.getReason() + ": " + event.getErrorMessage();
        if (toastLabel != null && toastContainer != null) {
            toastLabel.setText("⚠️ " + formattedError.toUpperCase());
            AnimationUtils.playToastAnimation(toastContainer);
        }
        GuiLogger.error("[GUI] server rejected action: " + formattedError);
    }

    /**
     * Player disconnection updates are displayed.
     *
     * @param event the player disconnected event
     */
    @Override
    public void onPlayerDisconnected(PlayerDisconnectedEvent event) {
        showNotification(event.getPlayer() + " disconnected.");
        LightGameState state = clientController.getLightGameState();
        if (state != null && opponentsSidebarController != null) {
            opponentsSidebarController.updateOpponents(state.getPlayers(), state.getMyNickname());
        }
    }

    /**
     * Player reconnection updates are displayed.
     *
     * @param event the player reconnected event
     */
    @Override
    public void onPlayerReconnected(PlayerReconnectedEvent event) {
        showNotification(event.getPlayer() + " reconnected.");
        LightGameState state = clientController.getLightGameState();
        if (state != null && opponentsSidebarController != null) {
            opponentsSidebarController.updateOpponents(state.getPlayers(), state.getMyNickname());
        }
    }

    /**
     * Game suspension state is displayed.
     *
     * @param event the game suspended event
     */
    @Override
    public void onGameSuspended(GameSuspendedEvent event) {
        if (suspensionOverlay != null) {
            suspensionOverlay.setVisible(true);
        }
        if (suspensionMessageLabel != null) {
            suspensionMessageLabel.setText("Game Suspended. Waiting for players to reconnect...");
        }
        showNotification("game suspended due to missing players.");
    }

    /**
     * Game resumed state is displayed and the board is rendered.
     *
     * @param event the game resumed event
     */
    @Override
    public void onGameResumed(GameResumedEvent event) {
        if (suspensionOverlay != null) {
            suspensionOverlay.setVisible(false);
        }
        showNotification("game resumed.");
        fullRender(clientController.getLightGameState());
    }
}
