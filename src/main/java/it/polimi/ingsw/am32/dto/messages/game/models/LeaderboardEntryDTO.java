package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;

public class LeaderboardEntryDTO implements Serializable {
    private final int position;
    private final String player;
    private final int score;
    private final String date;

    public LeaderboardEntryDTO(int position, String player, int score, String date) {
        this.position = position;
        this.player = player;
        this.score = score;
        this.date = date;
    }

    public int getPosition() {
        return position;
    }

    public String getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    public String getDate() {
        return date;
    }
}
