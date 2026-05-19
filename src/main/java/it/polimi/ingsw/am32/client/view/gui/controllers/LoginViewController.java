package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.dto.messages.lobby.events.LoginResultEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginViewController implements ViewController {

    private final ClientController clientController;

    @FXML private TextField usernameField;
    @FXML private Label errorLabel;

    public LoginViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
        usernameField.setOnAction(this::handleLogin);
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String nickname = usernameField.getText().trim();

        if (nickname.isEmpty()) {
            showError("Nickname cannot be empty.");
            return;
        }

        errorLabel.setVisible(false);
        usernameField.setDisable(true);

        System.out.println("[GUI] Requesting login for nickname: " + nickname);

        clientController.requestLogin(nickname);
    }

    @Override
    public void onLoginResult(LoginResultEvent event) {
        Platform.runLater(() -> {
            usernameField.setDisable(false);

            if (!event.isSuccess()) {
                showError("Login failed: " + event.getMessage());
                usernameField.requestFocus();
            }
        });
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
