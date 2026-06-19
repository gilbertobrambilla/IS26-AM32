package it.polimi.ingsw.am32.client.view.gui.controllers;

import it.polimi.ingsw.am32.client.controller.ClientController;
import it.polimi.ingsw.am32.client.view.gui.SceneManager;
import it.polimi.ingsw.am32.client.view.gui.utils.GuiLogger;
import it.polimi.ingsw.am32.dto.messages.game.events.GameEndedEvent;
import it.polimi.ingsw.am32.dto.messages.game.events.LeaderboardEvent;
import it.polimi.ingsw.am32.dto.messages.game.models.LeaderboardEntryDTO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * The end-game results and leaderboard view controller is represented.
 */
public class EndGameViewController implements ViewController {

    private static final String CURRENT_PLAYER_CLASS = "current-player-row";
    private static final int ROW_HEIGHT = 30;
    private static final int HEADER_HEIGHT = 35;

    private final ClientController clientController;

    @FXML private Label winnersLabel;

    // local table
    @FXML private TableView<LocalScoreRow> localScoreTable;
    @FXML private TableColumn<LocalScoreRow, String> localPlayerCol;
    @FXML private TableColumn<LocalScoreRow, Integer> localScoreCol;

    @FXML private VBox loadingBox;
    @FXML private VBox errorBox;
    @FXML private VBox dataBox;

    // global table
    @FXML private TableView<LeaderboardEntryDTO> globalLeaderboardTable;
    @FXML private TableColumn<LeaderboardEntryDTO, Integer> globalPosCol;
    @FXML private TableColumn<LeaderboardEntryDTO, String> globalPlayerCol;
    @FXML private TableColumn<LeaderboardEntryDTO, Integer> globalScoreCol;
    @FXML private TableColumn<LeaderboardEntryDTO, String> globalDateCol;

    @FXML private Label overflowLabel;
    @FXML private Label globalPositionLabel;

    /**
     * Dependency injection is handled through the constructor.
     *
     * @param clientController the main client controller
     */
    public EndGameViewController(ClientController clientController) {
        this.clientController = clientController;
    }

    /**
     * Result tables, row factories, and initial visibility states are configured.
     */
    @FXML
    public void initialize() {
        // setup local table columns
        localPlayerCol.setCellValueFactory(new PropertyValueFactory<>("player"));
        localScoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));

        // setup global table columns
        globalPosCol.setCellValueFactory(new PropertyValueFactory<>("position"));
        globalPlayerCol.setCellValueFactory(new PropertyValueFactory<>("player"));
        globalScoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        globalDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        // row factory is configured to highlight the local player's row
        String myNickname = clientController != null ? clientController.getMyNickname() : null;

        localScoreTable.setRowFactory(tv -> new TableRow<>() {
            /**
             * The local score row style is refreshed.
             *
             * @param item the rendered local score row
             * @param empty true if the row is empty, false otherwise
             */
            @Override
            protected void updateItem(LocalScoreRow item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll(CURRENT_PLAYER_CLASS);
                if (!empty && item != null && myNickname != null && item.getPlayer().equals(myNickname)) {
                    getStyleClass().add(CURRENT_PLAYER_CLASS);
                }
            }
        });

        globalLeaderboardTable.setRowFactory(tv -> new TableRow<>() {
            /**
             * The global leaderboard row style is refreshed.
             *
             * @param item the rendered leaderboard row
             * @param empty true if the row is empty, false otherwise
             */
            @Override
            protected void updateItem(LeaderboardEntryDTO item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll(CURRENT_PLAYER_CLASS);
                if (!empty && item != null && myNickname != null && item.getPlayer().equals(myNickname)) {
                    getStyleClass().add(CURRENT_PLAYER_CLASS);
                }
            }
        });

        // tables are configured to auto-size based on content
        localScoreTable.setFixedCellSize(ROW_HEIGHT);
        localScoreTable.prefHeightProperty().bind(
                Bindings.size(localScoreTable.getItems()).multiply(ROW_HEIGHT).add(HEADER_HEIGHT)
        );
        localScoreTable.setMinHeight(HEADER_HEIGHT);

        globalLeaderboardTable.setFixedCellSize(ROW_HEIGHT);
        globalLeaderboardTable.prefHeightProperty().bind(
                Bindings.size(globalLeaderboardTable.getItems()).multiply(ROW_HEIGHT).add(HEADER_HEIGHT)
        );
        globalLeaderboardTable.setMinHeight(HEADER_HEIGHT);

        loadingBox.setVisible(true);
        loadingBox.setManaged(true);
        errorBox.setVisible(false);
        errorBox.setManaged(false);
        dataBox.setVisible(false);
        dataBox.setManaged(false);
    }

    /**
     * Local match results are displayed.
     *
     * @param event the game ended event
     */
    @Override
    public void onGameResults(GameEndedEvent event) {
        GuiLogger.info("[GUI] onGameResults received");

        List<String> winners = event.getResults().getWinners();
        if (winners == null || winners.isEmpty()) {
            winnersLabel.setText("Winners: None");
        } else {
            winnersLabel.setText("Winners: " + String.join(", ", winners));
        }

        localScoreTable.getItems().clear();
        Map<String, int[]> scores = event.getResults().getMatchScores();

        if (scores != null) {
            List<Map.Entry<String, int[]>> sortedScores = new ArrayList<>(scores.entrySet());
            // sort by points descending
            sortedScores.sort((a, b) -> Integer.compare(b.getValue()[0], a.getValue()[0]));

            for (Map.Entry<String, int[]> entry : sortedScores) {
                String nick = entry.getKey();
                int points = entry.getValue()[0];
                localScoreTable.getItems().add(new LocalScoreRow(nick, points));
            }
        }
    }

    /**
     * Global leaderboard results are displayed.
     *
     * @param event the leaderboard event
     */
    @Override
    public void onLeaderboard(LeaderboardEvent event) {
        GuiLogger.info("[GUI] onLeaderboard received. DB available: " + event.isDbAvailable());

        loadingBox.setVisible(false);
        loadingBox.setManaged(false);

        if (!event.isDbAvailable()) {
            errorBox.setVisible(true);
            errorBox.setManaged(true);
            return;
        }

        dataBox.setVisible(true);
        dataBox.setManaged(true);

        globalLeaderboardTable.getItems().clear();
        List<LeaderboardEntryDTO> leaderboard = event.getGlobalLeaderboard();

        if (leaderboard != null) {
            int displayLimit = 10;
            int visibleEntries = Math.min(leaderboard.size(), displayLimit);

            for (int i = 0; i < visibleEntries; i++) {
                globalLeaderboardTable.getItems().add(leaderboard.get(i));
            }

            if (leaderboard.size() > displayLimit) {
                int otherPlayers = leaderboard.size() - displayLimit;
                overflowLabel.setText("... and " + otherPlayers + " other players worldwide!");
                overflowLabel.setVisible(true);
                overflowLabel.setManaged(true);
            } else {
                overflowLabel.setVisible(false);
                overflowLabel.setManaged(false);
            }
        }

        int globalPos = event.getGlobalPosition();
        if (globalPos > 0) {
            globalPositionLabel.setText("Your global rank: #" + globalPos);
        } else {
            globalPositionLabel.setText("Your global rank: N/A");
        }
    }

    /**
     * The application is closed from the end-game screen.
     */
    @FXML
    public void handleExit() {
        GuiLogger.info("[GUI] Exiting from EndGameScene");
        SceneManager.exitApplication();
    }

    /**
     * A local score table row is represented.
     * JavaFX properties are used for compatibility with cell value factories.
     */
    public static class LocalScoreRow {
        private final SimpleStringProperty player;
        private final SimpleIntegerProperty score;

        /**
         * A local score row is created.
         *
         * @param player the player nickname
         * @param score the final score
         */
        public LocalScoreRow(String player, int score) {
            this.player = new SimpleStringProperty(player);
            this.score = new SimpleIntegerProperty(score);
        }

        /**
         * The player nickname is returned.
         *
         * @return the player nickname
         */
        public String getPlayer() {
            return player.get();
        }

        /**
         * The final score is returned.
         *
         * @return the final score
         */
        public int getScore() {
            return score.get();
        }
    }
}
