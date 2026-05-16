package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.dao.DatabaseConnection;
import it.polimi.ingsw.am32.dao.LeaderboardDAO;
import it.polimi.ingsw.am32.dto.messages.game.events.GameEndedEvent;
import it.polimi.ingsw.am32.dto.messages.game.models.GameResultDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.LeaderboardEntryDTO;
import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;
import it.polimi.ingsw.am32.model.GameResult;
import it.polimi.ingsw.am32.model.Player;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Asynchronous worker used to handle database operations and network notifications at the end of a game.
 */
public class LeaderboardWorker implements Runnable {
    private final int gameId;
    private final Map<String, PlayerSession> activeClients;
    private final GameResult results;
    private final int numOfPlayers;
    private final List<String> winners;
    private final GameLifecycleCallback lifecycleCallback;

    /**
     * The worker is instantiated with the necessary context to process the end of the game.
     *
     * @param gameId the unique identifier of the game
     * @param activeClients the map containing the active player sessions
     * @param results the raw results calculated by the game state
     * @param numOfPlayers the total number of players in the game
     * @param winners the list containing the nicknames of the winners
     * @param lifecycleCallback the callback used to notify the lobby about the game termination
     */
    public LeaderboardWorker(int gameId, Map<String, PlayerSession> activeClients, GameResult results, int numOfPlayers, List<String> winners, GameLifecycleCallback lifecycleCallback) {
        this.gameId = gameId;
        this.activeClients = activeClients;
        this.results = results;
        this.numOfPlayers = numOfPlayers;
        this.winners = winners;
        this.lifecycleCallback = lifecycleCallback;
    }

    /**
     * Database operations and network notifications are executed asynchronously.
     */
    @Override
    public void run() {
        Map<String, int[]> fullMatchScores = new HashMap<>();
        Map<String, Integer> dbScores = new HashMap<>();

        // raw scores are extracted and the database scores are calculated
        for (Map.Entry<Player, int[]> entry : this.results.getLeaderboard().entrySet()) {
            String nickname = entry.getKey().getNickname();
            int prestige = entry.getValue()[0];
            int food = entry.getValue()[1];

            fullMatchScores.put(nickname, entry.getValue());

            if (this.activeClients.containsKey(nickname)) {
                int calculatedScore = (prestige * 1000 + food) * 10 * this.numOfPlayers;
                dbScores.put(nickname, calculatedScore);
            }
        }

        List<LeaderboardEntryDTO> globalRanking = new ArrayList<>();
        boolean dbAvailable = true;
        Connection conn = null;

        try {
            // database connection is retrieved
            conn = DatabaseConnection.getInstance().getConnection();
            LeaderboardDAO leaderboardDAO = new LeaderboardDAO(conn);

            // match results are saved into the database
            leaderboardDAO.saveMatchResults(dbScores, this.numOfPlayers);

            // global leaderboard is fetched
            globalRanking = leaderboardDAO.fetchLeaderboard(this.numOfPlayers);

        } catch (Exception e) {
            // database error is intercepted and flagged
            dbAvailable = false;
            // database error is logged
            LogManager.getInstance().log(LogLevel.ERROR, "[LeaderboardWorker] failed to save or fetch match results for game " + this.gameId, e);
        } finally {
            if (conn != null) {
                try {
                    // database connection is closed safely
                    conn.close();
                } catch (SQLException e) {
                    // database closure error is logged
                    LogManager.getInstance().log(LogLevel.ERROR, "[LeaderboardWorker] failed to close database connection for game " + this.gameId, e);
                }
            }
        }

        // network notifications are sent to active clients
        for (PlayerSession session : this.activeClients.values()) {
            String currentNickname = session.getPlayer().getNickname();
            int globalPos = -1;

            if (dbAvailable) {
                // global position is calculated based on the fetched ranking
                for (int i = 0; i < globalRanking.size(); i++) {
                    if (globalRanking.get(i).getPlayer().equals(currentNickname)) {
                        globalPos = i + 1;
                        break;
                    }
                }
            }

            // game result dto is built and sent
            GameResultDTO resultDTO = new GameResultDTO(this.winners, fullMatchScores, globalRanking, globalPos);
            GameEndedEvent eventDTO = new GameEndedEvent(resultDTO);
            session.getView().notifyGameEnded(eventDTO);
        }

        // lobby is notified to clear resources
        this.lifecycleCallback.endGame(this.gameId, this.results);
    }
}
