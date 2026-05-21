package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.network.NetworkFactory;
import it.polimi.ingsw.am32.client.view.gui.GuiApp;
import it.polimi.ingsw.am32.client.view.gui.GuiUI;
import it.polimi.ingsw.am32.client.view.gui.SceneManager;
import it.polimi.ingsw.am32.client.view.gui.utils.GuiLogger;
import it.polimi.ingsw.am32.dto.messages.lobby.events.ConnectionResultEvent;
import it.polimi.ingsw.am32.network.client.VirtualServer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.concurrent.CompletableFuture;

public class ConnectionViewController implements ViewController {

    @FXML private Label errorLabel;
    @FXML private Button btnRmi;
    @FXML private Button btnSocket;

    public ConnectionViewController() {
    }

    @FXML
    public void initialize() {
    }

    @FXML
    public void handleConnectRMI(ActionEvent event) {
        attemptConnection(true);
    }

    @FXML
    public void handleConnectSocket(ActionEvent event) {
        attemptConnection(false);
    }

    private void attemptConnection(boolean isRMI) {
        String ip = GuiApp.getServerAddress();

        if (ip == null || ip.equals("unknown")) {
            showError("Server IP address not configured properly.");
            return;
        }

        errorLabel.setVisible(false);
        setButtonsDisabled(true);

        Runnable onFatalError = () -> Platform.runLater(() ->
                SceneManager.forceDisconnect("Connection lost with the server.")
        );

        CompletableFuture.supplyAsync(() -> {
            GuiLogger.info("[GUI] Attempting background connection to " + ip);
            VirtualServer vs = isRMI ? NetworkFactory.createRmiConnection(ip, onFatalError)
                    : NetworkFactory.createSocketConnection(ip, onFatalError);

            if (vs == null) {
                throw new RuntimeException("Server unreachable at address " + ip);
            }

            ClientController controller = new ClientController(vs);
            vs.setClientController(controller);
            vs.startServerHeartbeat();

            return controller;

        }).thenAcceptAsync(controller -> {
            GuiLogger.info("[GUI] Connection initialized correctly");
            GuiUI guiUI = new GuiUI(controller);
            controller.setUI(guiUI);
            SceneManager.setClientController(controller);

            controller.requestConnect();

        }, Platform::runLater).exceptionally(ex -> {
            String errorMsg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();

            Platform.runLater(() -> {
                setButtonsDisabled(false);
                showError("Connection failed: " + errorMsg);
            });

            return null;
        });
    }

    @Override
    public void onConnectionResult(ConnectionResultEvent event) {
        Platform.runLater(() -> {
            if (!event.isSuccess()) {
                setButtonsDisabled(false);
                showError("Server refused connection: " + event.getMessage());
            }
        });
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void setButtonsDisabled(boolean disabled) {
        btnRmi.setDisable(disabled);
        btnSocket.setDisable(disabled);
    }
}
