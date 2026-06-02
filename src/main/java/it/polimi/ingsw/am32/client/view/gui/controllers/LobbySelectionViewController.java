package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.view.gui.SceneManager;
import it.polimi.ingsw.am32.client.view.gui.ViewPaths;
import it.polimi.ingsw.am32.client.view.gui.utils.GuiLogger;
import it.polimi.ingsw.am32.dto.messages.lobby.events.AvailableLobbiesEvent;
import it.polimi.ingsw.am32.dto.messages.lobby.events.LobbyErrorEvent;
import it.polimi.ingsw.am32.dto.messages.lobby.events.LobbyRoomUpdateEvent;
import it.polimi.ingsw.am32.dto.messages.lobby.models.LobbyInfoDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * The lobby selection view controller is represented.
 * Game creation and lobby joining are managed through a tab pane layout.
 */
public class LobbySelectionViewController implements ViewController {

    private final ClientController clientController;

    @FXML private ListView<LobbyInfoDTO> lobbyListView;
    @FXML private Spinner<Integer> playerCountSpinner;
    @FXML private Button joinButton;

    @FXML private Label errorLabelJoin;
    @FXML private Label errorLabelCreate;

    /**
     * Dependency injection is handled through the constructor.
     */
    public LobbySelectionViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    /**
     * View components are set up with a custom cell factory.
     */
    @FXML
    public void initialize() {
        if (joinButton != null) {
            joinButton.setDisable(true);
            lobbyListView.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> joinButton.setDisable(newValue == null)
            );
        }

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
     * The creation game action is triggered by the corresponding button.
     */
    @FXML
    private void handleCreateGame() {
        if (playerCountSpinner == null || playerCountSpinner.getValue() == null) return;

        hideErrors();
        int players = playerCountSpinner.getValue();
        clientController.requestCreateGame(players);
    }

    /**
     * The join selected game action is triggered by the corresponding button.
     */
    @FXML
    private void handleJoinGame() {
        LobbyInfoDTO selectedLobby = lobbyListView.getSelectionModel().getSelectedItem();

        if (selectedLobby == null) {
            showErrorJoin("Please select a game from the list.");
            return;
        }

        hideErrors();
        clientController.requestJoinGame(selectedLobby.getGameId());
    }


    @Override
    public void onAvailableLobbies(AvailableLobbiesEvent event) {
        Platform.runLater(() -> {
            lobbyListView.getItems().clear();
            lobbyListView.getItems().addAll(event.getLobbies());
        });
    }

    @Override
    public void onLobbyRoomUpdate(LobbyRoomUpdateEvent event) {
        GuiLogger.info("[GUI] Successfully entered a room. Transitioning to Waiting Room");
        SceneManager.switchScene(ViewPaths.WAITING_ROOM, nextCtrl -> nextCtrl.onLobbyRoomUpdate(event));
    }

    @Override
    public void onLobbyError(LobbyErrorEvent event) {
        Platform.runLater(() -> {
            showErrorJoin(event.getErrorMessage());
            showErrorCreate(event.getErrorMessage());
        });
    }


    private void showErrorJoin(String message) {
        if (errorLabelJoin != null) {
            errorLabelJoin.setText(message);
            errorLabelJoin.setVisible(true);
        }
    }

    private void showErrorCreate(String message) {
        if (errorLabelCreate != null) {
            errorLabelCreate.setText(message);
            errorLabelCreate.setVisible(true);
        }
    }

    private void hideErrors() {
        if (errorLabelJoin != null) errorLabelJoin.setVisible(false);
        if (errorLabelCreate != null) errorLabelCreate.setVisible(false);
    }
}
