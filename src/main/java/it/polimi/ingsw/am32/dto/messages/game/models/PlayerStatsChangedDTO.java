package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;

public class PlayerStatsChangedDTO implements Serializable {
    private final String player;
    private final int foodChange;
    private final int prestigeChange;
    private final int food;
    private final int prestige;
    private final String reason;

    public PlayerStatsChangedDTO(String player, int foodChange, int prestigeChange, int food, int prestige, String reason) {
        this.player = player;
        this.foodChange = foodChange;
        this.prestigeChange = prestigeChange;
        this.food = food;
        this.prestige = prestige;
        this.reason = reason;
    }

    public String getPlayer() {
        return player;
    }

    public int getFoodChange() {
        return foodChange;
    }

    public int getPrestigeChange() {
        return prestigeChange;
    }

    public int getFood() {
        return food;
    }

    public int getPrestige() {
        return prestige;
    }

    public String getReason() {
        return reason;
    }
}
