package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Platform;

/**
 * Controller for the Login view.
 * Handles user input for the initial connection and delegates the action to the ClientController.
 */
public class LoginViewController {

    private final ClientController clientController;

    @FXML private TextField addressField;
    @FXML private TextField portField;
    @FXML private TextField usernameField;
    @FXML private Label errorLabel;

    /**
     * Constructor used by the SceneManager's Controller Factory to automatically
     * inject the ClientController dependency.
     *
     * @param clientController The main controller managing game and network state.
     */
    public LoginViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    /**
     * Initializes the view state after FXML fields are injected.
     */
    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
    }

    /**
     * Triggered when the user clicks the connect button or presses Enter.
     * Validates input and sends the connection request to the server.
     */
    @FXML
    private void handleConnect() {
        String username = usernameField.getText().trim();
        String address = addressField.getText().trim();
        String portStr = portField.getText().trim();

        // 1. Basic validation
        if (username.isEmpty()) {
            showErrorMessage("Nickname is required");
            return;
        }

        if (address.isEmpty() || portStr.isEmpty()) {
            showErrorMessage("Address and Port are required");
            return;
        }

        try {
            int port = Integer.parseInt(portStr);

            // --- IMPORTANT ARCHITECTURAL NOTE ---
            // If your VirtualServer requires address and port to be set dynamically from the GUI,
            // you might need to add a method to ClientController, e.g.:
            // clientController.initializeNetwork(address, port);

            System.out.println("[GUI] Requesting connection for nickname: " + username + " at " + address + ":" + port);

            // 2. Delegate the request to the ClientController (matching your actual method)
            clientController.requestConnect(username);

        } catch (NumberFormatException e) {
            showErrorMessage("Port must be a valid number");
        }
    }

    /**
     * Displays an error message on the UI safely from any thread.
     *
     * @param message The error message to display.
     */
    private void showErrorMessage(String message) {
        Platform.runLater(() -> {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        });
    }
}
