package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.dto.messages.lobby.models.LobbyInfoDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * Controller for the Menu/Lobby view.
 * Manages game creation and joining available lobbies.
 */
public class LobbyViewController {

    private final ClientController clientController;

    @FXML private ListView<LobbyInfoDTO> lobbyListView;
    @FXML private Spinner<Integer> playerCountSpinner;
    @FXML private Label statusLabel;
    @FXML private Button joinButton;

    /**
     * Dependency injection via constructor (handled by SceneManager).
     */
    public LobbyViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    /**
     * Set up the view components, including custom cell factory for the ListView.
     */
    @FXML
    public void initialize() {
        statusLabel.setText("Welcome, " + clientController.getMyNickname() + "!");

        // Configure how each lobby item is displayed in the list
        lobbyListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(LobbyInfoDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("Lobby #%d | Players: %d/%d",
                            item.getGameId(),
                            item.getCurrentPlayersCount(),
                            item.getTargetPlayers()));
                }
            }
        });
    }

    /**
     * Triggered by the "Create Game" button.
     */
    @FXML
    private void handleCreateGame() {
        int players = playerCountSpinner.getValue();
        statusLabel.setText("Creating game for " + players + " players...");
        clientController.requestCreateGame(players);
    }

    /**
     * Triggered by the "Join Selected Game" button.
     */
    @FXML
    private void handleJoinGame() {
        LobbyInfoDTO selectedLobby = lobbyListView.getSelectionModel().getSelectedItem();

        if (selectedLobby == null) {
            statusLabel.setText("Please select a game from the list.");
            return;
        }

        statusLabel.setText("Joining game #" + selectedLobby.getGameId() + "...");
        clientController.requestJoinGame(selectedLobby.getGameId());
    }

    /**
     * Updates the UI list with available games received from the network.
     * This method should be called via GuiUI (UI interface) and Platform.runLater().
     *
     * @param lobbies The list of available lobbies.
     */
    public void updateLobbyList(List<LobbyInfoDTO> lobbies) {
        Platform.runLater(() -> {
            lobbyListView.setItems(FXCollections.observableArrayList(lobbies));
            statusLabel.setText("Lobby list updated.");
        });
    }
}
