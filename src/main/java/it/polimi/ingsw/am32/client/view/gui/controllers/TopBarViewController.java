package it.polimi.ingsw.am32.client.view.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * The top status bar controller is represented during a game session.
 *
 * <p>The current era, round number, and game phase are displayed. No client-side
 * state is carried and no external dependencies are required. Values pushed by
 * the parent {@link InGameViewController} are reflected.</p>
 */
public class TopBarViewController {

    @FXML private Label eraLabel;
    @FXML private Label roundLabel;
    @FXML private Label phaseLabel;

    @FXML
    public void initialize() {
        // labels are already populated with placeholder text in the fxml
        // no additional setup is required at construction time
    }

    /**
     * All three status labels are refreshed.
     *
     * @param era the current era number (1-based)
     * @param round the current round number within the era
     * @param phase the raw enum name of the current {@code GamePhase}
     */
    public void updateStatusBar(int era, int round, String phase) {
        eraLabel.setText(String.valueOf(era));
        roundLabel.setText(String.valueOf(round));
        phaseLabel.setText(phase.replace('_', ' ').toUpperCase());
    }
}
