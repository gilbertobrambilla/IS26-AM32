package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.view.gui.GuiApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller for the Login view.
 * Handles nickname input and delegates the connection request to ClientController.
 * Note: server address and port are configured interactively at startup — the network
 * connection is already established before the GUI launches.
 */
public class LoginViewController implements ViewController {

    private final ClientController clientController;

    @FXML private TextField usernameField;
    @FXML private Label serverInfoLabel;
    @FXML private Label errorLabel;

    public LoginViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        serverInfoLabel.setText("Connected to: " + GuiApp.getServerAddress());
    }

    @FXML
    private void handleConnect() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            showErrorMessage("Nickname is required");
            return;
        }

        errorLabel.setVisible(false);
        clientController.requestConnect(username);
    }

    private void showErrorMessage(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
