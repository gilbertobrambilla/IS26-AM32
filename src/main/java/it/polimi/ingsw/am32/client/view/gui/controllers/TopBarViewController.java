package it.polimi.ingsw.am32.client.view.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for the top status bar shown during a game session.
 *
 * <p>Displays the current era, round number, and game phase. This controller
 * carries no client-side state and requires no external dependencies — it only
 * reflects values pushed to it by its parent {@link InGameViewController}.</p>
 */
public class TopBarViewController {

    @FXML private Label eraLabel;
    @FXML private Label roundLabel;
    @FXML private Label phaseLabel;

    @FXML
    public void initialize() {
        // Labels are already populated with placeholder text in the FXML;
        // nothing extra is needed at construction time.
    }

    /**
     * Refreshes all three status labels.
     *
     * @param era   the current era number (1-based)
     * @param round the current round number within the era
     * @param phase the raw enum name of the current {@code GamePhase}
     */
    public void updateStatusBar(int era, int round, String phase) {
        eraLabel.setText(String.valueOf(era));
        roundLabel.setText(String.valueOf(round));
        phaseLabel.setText(phase.replace('_', ' ').toUpperCase());
    }
}
