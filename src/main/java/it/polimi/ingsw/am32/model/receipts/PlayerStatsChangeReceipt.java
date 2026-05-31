package it.polimi.ingsw.am32.model.receipts;

/**
 * A receipt for a player's statistics change is represented.
 */
public class PlayerStatsChangeReceipt {
    private final String playerNickname;
    private final int foodChange;
    private final int prestigeChange;
    private final int currentFood;
    private final int currentPrestige;

    /**
     * A player statistics change receipt is initialized with the given values.
     *
     * @param playerNickname the nickname of the player
     * @param foodChange the change in food amount
     * @param prestigeChange the change in prestige amount
     * @param currentFood the current amount of food
     * @param currentPrestige the current amount of prestige
     */
    public PlayerStatsChangeReceipt(String playerNickname, int foodChange, int prestigeChange, int currentFood, int currentPrestige) {
        this.playerNickname = playerNickname;
        this.foodChange = foodChange;
        this.prestigeChange = prestigeChange;
        this.currentFood = currentFood;
        this.currentPrestige = currentPrestige;
    }

    /**
     * The nickname of the player is returned.
     *
     * @return the player nickname
     */
    public String getPlayerNickname() {
        return this.playerNickname;
    }

    /**
     * The food amount change is returned.
     *
     * @return the food change
     */
    public int getFoodChange() {
        return this.foodChange;
    }

    /**
     * The prestige amount change is returned.
     *
     * @return the prestige change
     */
    public int getPrestigeChange() {
        return this.prestigeChange;
    }

    /**
     * The current food amount is returned.
     *
     * @return the current food
     */
    public int getCurrentFood() {
        return this.currentFood;
    }

    /**
     * The current prestige amount is returned.
     *
     * @return the current prestige
     */
    public int getCurrentPrestige() {
        return this.currentPrestige;
    }
}
