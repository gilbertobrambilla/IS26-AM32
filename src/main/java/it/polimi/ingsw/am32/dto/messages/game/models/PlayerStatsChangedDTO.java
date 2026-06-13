package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;

/**
 * A player statistics change data transfer object is represented.
 */
public class PlayerStatsChangedDTO implements Serializable {
    private final String player;
    private final int foodChange;
    private final int prestigeChange;

    /**
     * A player statistics change data transfer object is initialized with delta data.
     *
     * @param player the player nickname
     * @param foodChange the food delta
     * @param prestigeChange the prestige delta
     */
    public PlayerStatsChangedDTO(String player, int foodChange, int prestigeChange) {
        this.player = player;
        this.foodChange = foodChange;
        this.prestigeChange = prestigeChange;
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
     * The food delta is returned.
     *
     * @return the food delta
     */
    public int getFoodChange() {
        return foodChange;
    }

    /**
     * The prestige delta is returned.
     *
     * @return the prestige delta
     */
    public int getPrestigeChange() {
        return prestigeChange;
    }
}
