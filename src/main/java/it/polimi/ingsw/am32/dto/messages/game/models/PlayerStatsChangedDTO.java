package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;

public class PlayerStatsChangedDTO implements Serializable {
    private final String playerNickname;
    private final int foodChange;
    private final int prestigeChange;
    private final int currentFood;
    private final int currentPrestige;
    private final String reason;

    public PlayerStatsChangedDTO(String playerNickname, int foodChange, int prestigeChange, int currentFood, int currentPrestige, String reason) {
        this.playerNickname = playerNickname;
        this.foodChange = foodChange;
        this.prestigeChange = prestigeChange;
        this.currentFood = currentFood;
        this.currentPrestige = currentPrestige;
        this.reason = reason;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public int getFoodChange() {
        return foodChange;
    }

    public int getPrestigeChange() {
        return prestigeChange;
    }

    public int getCurrentFood() {
        return currentFood;
    }

    public int getCurrentPrestige() {
        return currentPrestige;
    }

    public String getReason() {
        return reason;
    }
}
