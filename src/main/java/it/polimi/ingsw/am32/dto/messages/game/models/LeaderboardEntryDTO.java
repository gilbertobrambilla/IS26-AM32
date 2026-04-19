package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;

public class LeaderboardEntryDTO implements Serializable {
    private final int position;
    private final String nickname;
    private final int score;
    private final String date;

    public LeaderboardEntryDTO(int position, String nickname, int score, String date) {
        this.position = position;
        this.nickname = nickname;
        this.score = score;
        this.date = date;
    }

    public int getPosition() {
        return this.position;
    }

    public String getNickname() {
        return this.nickname;
    }

    public int getScore() {
        return this.score;
    }

    public String getDate() {
        return this.date;
    }
}
