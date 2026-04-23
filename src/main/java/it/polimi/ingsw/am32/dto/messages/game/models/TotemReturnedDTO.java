package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;

public class TotemReturnedDTO implements Serializable {
    private final String player;
    private final int turnOrderPosition;
    private final PlayerStatsChangedDTO statsChanges;
    private final String nextPlayer;

    public TotemReturnedDTO(String player, int turnOrderPosition, PlayerStatsChangedDTO statsChanges, String nextPlayer) {
        this.player = player;
        this.turnOrderPosition = turnOrderPosition;
        this.statsChanges = statsChanges;
        this.nextPlayer = nextPlayer;
    }

    public String getPlayer() {
        return player;
    }

    public int getTurnOrderPosition() {
        return turnOrderPosition;
    }

    public PlayerStatsChangedDTO getStatsChanges() {
        return statsChanges;
    }

    public String getNextPlayer() {
        return nextPlayer;
    }
}
