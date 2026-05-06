package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller for the Login/Connection scene.
 * Handles user input for the nickname and requests connection via the ClientController.
 */
public class LoginViewController {

    private final ClientController controller;

    @FXML
    private TextField nicknameField;

    @FXML
    private Button connectButton;

    @FXML
    private Label statusLabel;

    /**
     * Constructor used by the custom Controller Factory in SceneManager.
     * The SceneManager automatically injects the main ClientController.
     *
     * @param controller the main client controller used for network communication.
     */
    public LoginViewController(ClientController controller) {
        this.controller = controller;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * by JavaFX after the FXML file has been loaded and the @FXML fields are injected.
     */
    @FXML
    public void initialize() {
        // Clear the status label on startup
        statusLabel.setText("");
    }

    /**
     * Triggered when the user clicks the "Connect" button or presses Enter
     * inside the text field (since defaultButton is true in FXML).
     */
    @FXML
    public void onConnectButtonClicked() {
        String nickname = nicknameField.getText().trim();

        // 1. Local Validation
        if (nickname.isEmpty()) {
            statusLabel.setText("Nickname cannot be empty!");
            statusLabel.setStyle("-fx-text-fill: #d93025;"); // Red color for errors
            return;
        }

        // 2. Prevent multiple connection attempts by disabling inputs
        nicknameField.setDisable(true);
        connectButton.setDisable(true);

        // 3. Update visual status
        statusLabel.setText("Connecting to server...");
        statusLabel.setStyle("-fx-text-fill: #76EEC6;"); // Acquamarine 2 color for normal status messages

        // 4. Send connection request to the server
        controller.requestConnect(nickname);
    }

    /**
     * Restores the UI to its active state.
     * This can be called by the main GuiUI if the connection fails,
     * allowing the user to try a different nickname.
     *
     * @param errorMessage the error message to display to the user.
     */
    public void showConnectionError(String errorMessage) {
        nicknameField.setDisable(false);
        connectButton.setDisable(false);
        statusLabel.setText(errorMessage);
        statusLabel.setStyle("-fx-text-fill: #d93025;"); // Red color for errors
    }
}
