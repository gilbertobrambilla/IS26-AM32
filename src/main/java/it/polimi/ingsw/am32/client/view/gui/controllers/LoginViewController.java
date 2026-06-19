package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.view.gui.utils.GuiLogger;
import it.polimi.ingsw.am32.dto.messages.lobby.events.LoginResultEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * The login view controller is represented.
 */
public class LoginViewController implements ViewController {

    private final ClientController clientController;

    @FXML private TextField usernameField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    /**
     * Dependency injection is handled through the constructor.
     *
     * @param clientController the main client controller
     */
    public LoginViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    /**
     * Login form callbacks are initialized.
     */
    @FXML
    public void initialize() {
        usernameField.setOnAction(this::handleLogin);
    }

    /**
     * The entered nickname is validated and submitted.
     *
     * @param event the triggering action event
     */
    @FXML
    public void handleLogin(ActionEvent event) {
        String nickname = usernameField.getText().trim();

        if (nickname.isEmpty()) {
            showError("Nickname cannot be empty.");
            return;
        }

        errorLabel.setVisible(false);
        setLoginDisabled(true);

        GuiLogger.info("[GUI] Requesting login for nickname: " + nickname);

        clientController.requestLogin(nickname);
    }

    /**
     * Login rejection is shown when authentication fails.
     *
     * @param event the login result event
     */
    @Override
    public void onLoginResult(LoginResultEvent event) {
        Platform.runLater(() -> {
            if (!event.isSuccess()) {
                setLoginDisabled(false);
                showError("Login failed: " + event.getMessage());
                usernameField.requestFocus();
            }
        });
    }

    /**
     * A login error message is displayed.
     *
     * @param message the error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void setLoginDisabled(boolean disabled) {
        usernameField.setDisable(disabled);
        if (loginButton != null) {
            loginButton.setDisable(disabled);
        }
    }
}
