package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TopBarViewController {

    private final ClientController clientController;

    @FXML private Label eraLabel;
    @FXML private Label roundLabel;
    @FXML private Label phaseLabel;

    public TopBarViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    @FXML
    public void initialize() {
        // top bar labels are initialized
    }

    public void updateStatusBar(int era, int round, String phase) {
        // era label is updated
        eraLabel.setText(String.valueOf(era));

        // round label is updated
        roundLabel.setText(String.valueOf(round));

        // phase label is formatted and updated
        phaseLabel.setText(phase.replace("_", " ").toUpperCase());
    }
}
