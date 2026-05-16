package it.polimi.ingsw.am32.dao;

import it.polimi.ingsw.am32.dto.messages.game.models.LeaderboardEntryDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data access object used to manage operations on the game results database table.
 */
public class LeaderboardDAO {

    private final Connection connection;

    /**
     * The data access object is instantiated with the provided database connection.
     *
     * @param connection the SQL connection to be used for queries
     */
    public LeaderboardDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * The calculated match scores are inserted into the database.
     *
     * @param scores the map containing the nicknames and the calculated scores
     * @param numOfPlayers the total number of players in the match
     * @throws SQLException thrown if a database access error occurs
     */
    public void saveMatchResults(Map<String, Integer> scores, int numOfPlayers) throws SQLException {
        String sql = "INSERT INTO mesos_leaderboard (nickname, final_score, players_count) VALUES (?, ?, ?)";

        PreparedStatement stmt = connection.prepareStatement(sql);

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            stmt.setString(1, entry.getKey());
            stmt.setInt(2, entry.getValue());
            stmt.setInt(3, numOfPlayers);

            // batch execution is prepared
            stmt.addBatch();
        }

        // batch is executed and statement is closed
        stmt.executeBatch();
        stmt.close();
    }

    /**
     * The complete leaderboard for a specific number of players is fetched from the database.
     *
     * @param numOfPlayers the number of players used to filter the leaderboard
     * @return the list containing all the leaderboard entries ordered by score
     * @throws SQLException thrown if a database access error occurs
     */
    public List<LeaderboardEntryDTO> fetchLeaderboard(int numOfPlayers) throws SQLException {
        String sql = "SELECT nickname, MAX(final_score) as best_score, MAX(game_date) as latest_date " +
                "FROM mesos_leaderboard " +
                "WHERE players_count = ? " +
                "GROUP BY nickname " +
                "ORDER BY best_score DESC";

        List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, numOfPlayers);

        ResultSet rs = stmt.executeQuery();

        int currentPosition = 1;
        while (rs.next()) {
            String nickname = rs.getString("nickname");
            int score = rs.getInt("best_score");
            Timestamp date = rs.getTimestamp("latest_date");

            // entry is instantiated and added to the list
            leaderboard.add(new LeaderboardEntryDTO(
                    currentPosition,
                    nickname,
                    score,
                    date.toString()
            ));

            currentPosition++;
        }

        // resources are closed
        rs.close();
        stmt.close();

        return leaderboard;
    }
}
