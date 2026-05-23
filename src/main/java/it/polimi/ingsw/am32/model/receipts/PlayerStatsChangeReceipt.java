package it.polimi.ingsw.am32.model.receipts;

/**
 * Represents a receipt for a player's statistics change.
 */
public class PlayerStatsChangeReceipt {
    private final String playerNickname;
    private final int foodChange;
    private final int prestigeChange;
    private final int currentFood;
    private final int currentPrestige;
    private final String reason;

    /**
     * Initializes the player statistics change receipt with the given values.
     *
     * @param playerNickname the nickname of the player
     * @param foodChange the change in food amount
     * @param prestigeChange the change in prestige amount
     * @param currentFood the current amount of food
     * @param currentPrestige the current amount of prestige
     * @param reason the reason for the statistics change
     */
    public PlayerStatsChangeReceipt(String playerNickname, int foodChange, int prestigeChange, int currentFood, int currentPrestige, String reason) {
        this.playerNickname = playerNickname;
        this.foodChange = foodChange;
        this.prestigeChange = prestigeChange;
        this.currentFood = currentFood;
        this.currentPrestige = currentPrestige;
        this.reason = reason;
    }

    /**
     * Gets the nickname of the player.
     *
     * @return the player nickname
     */
    public String getPlayerNickname() {
        return this.playerNickname;
    }

    /**
     * Gets the change in food amount.
     *
     * @return the food change
     */
    public int getFoodChange() {
        return this.foodChange;
    }

    /**
     * Gets the change in prestige amount.
     *
     * @return the prestige change
     */
    public int getPrestigeChange() {
        return this.prestigeChange;
    }

    /**
     * Gets the current amount of food.
     *
     * @return the current food
     */
    public int getCurrentFood() {
        return this.currentFood;
    }

    /**
     * Gets the current amount of prestige.
     *
     * @return the current prestige
     */
    public int getCurrentPrestige() {
        return this.currentPrestige;
    }

    /**
     * Gets the reason for the statistics change.
     *
     * @return the reason
     */
    public String getReason() {
        return this.reason;
    }
}
