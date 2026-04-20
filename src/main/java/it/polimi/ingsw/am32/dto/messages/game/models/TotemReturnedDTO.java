package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;

public class TotemReturnedDTO implements Serializable {
    private final String playerNickname;
    private final int turnOrderPosition;
    private final PlayerStatsChangedDTO statsChange;
    private final String nextPlayerNickname;

    public TotemReturnedDTO(String playerNickname, int turnOrderPosition, PlayerStatsChangedDTO statsChange, String nextPlayerNickname) {
        this.playerNickname = playerNickname;
        this.turnOrderPosition = turnOrderPosition;
        this.statsChange = statsChange;
        this.nextPlayerNickname = nextPlayerNickname;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public int getTurnOrderPosition() {
        return turnOrderPosition;
    }

    public PlayerStatsChangedDTO getStatsChange() {
        return statsChange;
    }

    public String getNextPlayerNickname() {
        return nextPlayerNickname;
    }
}
