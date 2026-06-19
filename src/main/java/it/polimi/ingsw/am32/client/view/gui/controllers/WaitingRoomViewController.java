package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.dto.messages.game.events.GameErrorEvent;
import it.polimi.ingsw.am32.dto.messages.lobby.events.LobbyRoomUpdateEvent;
import it.polimi.ingsw.am32.dto.messages.lobby.models.LobbyInfoDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/**
 * The waiting room view controller is represented.
 * Current players are displayed while the game start is awaited.
 */
public class WaitingRoomViewController implements ViewController {

    @FXML private Label lobbyTitleLabel;
    @FXML private Label statusLabel;
    @FXML private ListView<String> playersListView;

    @FXML
    public void initialize() {
    }

    /**
     * Lobby room structural updates are handled polymorphically.
     * This method is triggered dynamically whenever a player joins or leaves the room.
     *
     * @param event the event containing updated information about the lobby room state
     */
    @Override
    public void onLobbyRoomUpdate(LobbyRoomUpdateEvent event) {
        if (event == null || event.getLobbyInfo() == null) return;

        LobbyInfoDTO lobbyInfo = event.getLobbyInfo();

        Platform.runLater(() -> {
            // top header is updated with the current game id
            if (lobbyTitleLabel != null) {
                lobbyTitleLabel.setText(String.format("Lobby Room #%d", lobbyInfo.getGameId()));
            }

            // active players list is refreshed
            if (playersListView != null) {
                playersListView.getItems().clear();
                if (lobbyInfo.getCurrentPlayers() != null) {
                    playersListView.getItems().addAll(lobbyInfo.getCurrentPlayers());
                }
            }

            if (statusLabel != null) {
                statusLabel.setText(String.format("Players: %d / %d. Waiting for match setup...",
                        lobbyInfo.getCurrentPlayersCount(),
                        lobbyInfo.getTargetPlayers()));
            }
        });
    }

    /**
     * Waiting room errors are displayed in the status label.
     *
     * @param event the game error event
     */
    @Override
    public void onError(GameErrorEvent event) {
        Platform.runLater(() -> {
            if (statusLabel != null) {
                statusLabel.setText(event.getReason() + ": " + event.getErrorMessage());
            }
        });
    }
}
