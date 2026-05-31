package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;

/**
 * A leaderboard entry data transfer object is represented.
 */
public class LeaderboardEntryDTO implements Serializable {
    private final int position;
    private final String player;
    private final int score;
    private final String date;

    /**
     * A leaderboard entry data transfer object is initialized with ranking data.
     *
     * @param position the leaderboard position
     * @param player the player nickname
     * @param score the player score
     * @param date the formatted score date
     */
    public LeaderboardEntryDTO(int position, String player, int score, String date) {
        this.position = position;
        this.player = player;
        this.score = score;
        this.date = date;
    }

    /**
     * The leaderboard position is returned.
     *
     * @return the leaderboard position
     */
    public int getPosition() {
        return position;
    }

    /**
     * The player nickname is returned.
     *
     * @return the player nickname
     */
    public String getPlayer() {
        return player;
    }

    /**
     * The player score is returned.
     *
     * @return the player score
     */
    public int getScore() {
        return score;
    }

    /**
     * The formatted score date is returned.
     *
     * @return the formatted score date
     */
    public String getDate() {
        return date;
    }
}
