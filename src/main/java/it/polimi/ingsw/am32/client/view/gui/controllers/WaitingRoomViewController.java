package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.dto.messages.lobby.events.LobbyRoomUpdateEvent;
import it.polimi.ingsw.am32.dto.messages.lobby.models.LobbyInfoDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/**
 * Controller for the Waiting Room view.
 * Displays the current players inside the joined lobby room and waits for the game to start.
 */
public class WaitingRoomViewController implements ViewController {

    private final ClientController clientController;

    @FXML private Label lobbyTitleLabel;
    @FXML private Label statusLabel;
    @FXML private ListView<String> playersListView;

    /**
     * Dependency injection via constructor (automatically handled by SceneManager's reflection mechanism).
     *
     * @param clientController The main client controller.
     */
    public WaitingRoomViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
    }

    /**
     * Polimorphic handling of the lobby room structural update.
     * This method is triggered dynamically by GuiUI whenever a player joins or leaves the room.
     *
     * @param event The event containing updated information about the lobby room state.
     */
    @Override
    public void onLobbyRoomUpdate(LobbyRoomUpdateEvent event) {
        if (event == null || event.getLobbyInfo() == null) return;

        LobbyInfoDTO lobbyInfo = event.getLobbyInfo();

        Platform.runLater(() -> {
            // Update the Top Header with the current Game ID
            if (lobbyTitleLabel != null) {
                lobbyTitleLabel.setText(String.format("Lobby Room #%d", lobbyInfo.getGameId()));
            }

            // Refresh the list of active players in the room
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
}
