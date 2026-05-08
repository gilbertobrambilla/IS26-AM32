package it.polimi.ingsw.am32.client.view.gui;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.view.UI;
import it.polimi.ingsw.am32.dto.messages.game.events.*;
import it.polimi.ingsw.am32.dto.messages.lobby.events.*;
import javafx.application.Platform;

/**
 * javaFX implementation of the UI interface.
 * it receives network events from the ClientController and safely routes them
 * to the JavaFX Application Thread using Platform.runLater().
 */
public class GuiUI implements UI {

    private final ClientController controller;

    /**
     * instantiates the GUI and links it to the main controller.
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
                // Switch to the Main Menu scene
                SceneManager.switchScene("/fxml/lobby-view.fxml");
            } else {
                System.err.println("[GUI] Connection failed: " + event.getMessage());
                // Show a standard JavaFX Error Alert
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Connection Error");
                alert.setHeaderText("Failed to connect to the server");
                alert.setContentText(event.getMessage());
                alert.showAndWait();
            }
        });
    }

    // --- LOBBY PHASE ---

    @Override
    public void updateAvailableGames(AvailableLobbiesEvent event) {
        Platform.runLater(() -> {
            System.out.println("[GUI] Updating available games list...");
            // TODO: Send data (event.getLobbies()) to the Menu scene controller
        });
    }

    @Override
    public void updateLobbyRoom(LobbyStateUpdatedEvent event) {
        Platform.runLater(() -> {
            System.out.println("[GUI] Lobby room updated. Players: " + event.getLobbyInfo().getCurrentPlayersCount());
            // TODO: Switch to the LobbyRoom scene (if not already there) and update player names
        });
    }

    @Override
    public void showLobbyError(LobbyErrorEvent event) {
        Platform.runLater(() -> {
            System.err.println("[GUI] Lobby error: " + event.getErrorMessage());
            // TODO: Show a Warning/Error Alert dialog
        });
    }

    // --- GAME LIFECYCLE ---

    @Override
    public void showGameStarted(GameStartedEvent event) {
        Platform.runLater(() -> {
            System.out.println("[GUI] Game Started! Next player: " + event.getNextPlayer());
            // TODO: Initialize the Board, request SceneManager to show the InGameScene
        });
    }

    @Override
    public void restoreGameState(GameStateEvent event) {
        Platform.runLater(() -> {
            System.out.println("[GUI] Restoring game state...");
            // TODO: Visually update the entire game board (cards, tokens, track)
        });
    }

    @Override
    public void updateGamePhase(GamePhaseChangedEvent event) {
        Platform.runLater(() -> {
            System.out.println("[GUI] Game phase changed to: " + event.getPhase());
            // TODO: Update UI indicators (e.g., "Initial Phase", "X's Turn" banner)
        });
    }

    @Override
    public void showRoundSummary(RoundEndedEvent event) {
        Platform.runLater(() -> {
            System.out.println("[GUI] Round " + event.getRound() + " ended.");
            // TODO: Show a summary panel/popup or update round/era visual counters
        });
    }

    @Override
    public void showGameResults(GameEndedEvent event) {
        Platform.runLater(() -> {
            System.out.println("[GUI] Game ended!");
            // TODO: Request SceneManager to switch to the EndGameScene with the leaderboard
        });
    }

    // --- GAMEPLAY ACTIONS ---

    @Override
    public void showTotemPlaced(TotemPlacedEvent event) {
        Platform.runLater(() -> {
            System.out.println("[GUI] Totem placed by " + event.getPlayer() + " on " + event.getOfferTileId());
            // TODO: Trigger the Totem placement animation on the board
        });
    }

    @Override
    public void showActionResolved(ActionResolvedEvent event) {
        Platform.runLater(() -> {
            System.out.println("[GUI] Action resolved by " + event.getPlayer());
            // TODO: Update the player's acquired cards and resource inventory
        });
    }

    @Override
    public void showEventResolved(EventResolvedEvent event) {
        Platform.runLater(() -> {
            System.out.println("[GUI] Event resolved: " + event.getEventCardId());
            // TODO: Show visual feedback (animation or popup) for the resolved event
        });
    }

    @Override
    public void showExtraAcquisition(ExtraAcquisitionResolvedEvent event) {
        Platform.runLater(() -> {
            System.out.println("[GUI] Extra acquisition by " + event.getPlayer());
            // TODO: Animate the extra card moving to the player's area
        });
    }

    @Override
    public void showPlayerFocus(String nickname) {
        Platform.runLater(() -> {
            System.out.println("[GUI] Focusing on player: " + nickname);
            // TODO: Open a modal or side panel with the details of this specific player
        });
    }

    // --- RESILIENCE AND ERRORS ---

    @Override
    public void showError(GameErrorEvent event) {
        Platform.runLater(() -> {
            System.err.println("[GUI] Game Error: " + event.getErrorMessage());
            // TODO: Show a toast notification or snackbar with the error message
        });
    }

    @Override
    public void showPlayerDisconnected(PlayerDisconnectedEvent event) {
        Platform.runLater(() -> {
            System.out.println("[GUI] Player disconnected: " + event.getPlayer());
            // TODO: Grayscale the player's avatar or add an 'offline' icon
        });
    }

    @Override
    public void showPlayerReconnected(PlayerReconnectedEvent event) {
        Platform.runLater(() -> {
            System.out.println("[GUI] Player reconnected: " + event.getPlayer());
            // TODO: Restore the player's avatar colors
        });
    }

    @Override
    public void showGameSuspended(GameSuspendedEvent event) {
        Platform.runLater(() -> {
            System.out.println("[GUI] Game suspended! Waiting for players...");
            // TODO: Show a transparent overlay blocking interaction with a "Game Suspended" text
        });
    }

    @Override
    public void showGameResumed(GameResumedEvent event) {
        Platform.runLater(() -> {
            System.out.println("[GUI] Game resumed!");
            // TODO: Remove the suspension overlay and restore interactions
        });
    }

    @Override
    public void notifyDisconnection(String message) {
        Platform.runLater(() -> {
            System.err.println("[GUI] Fatal Disconnection: " + message);
            // TODO: Show a fatal blocking Alert, then close the app or force return to Login
        });
    }
}
