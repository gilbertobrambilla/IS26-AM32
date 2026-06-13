package it.polimi.ingsw.am32.model.receipts;

/**
 * A receipt for a player's statistics change is represented.
 */
public class PlayerStatsChangeReceipt {
    private final String playerNickname;
    private final int foodChange;
    private final int prestigeChange;

    /**
     * A player statistics change receipt is initialized with the given values.
     *
     * @param playerNickname the nickname of the player
     * @param foodChange the change in food amount
     * @param prestigeChange the change in prestige amount
     */
    public PlayerStatsChangeReceipt(String playerNickname, int foodChange, int prestigeChange) {
        this.playerNickname = playerNickname;
        this.foodChange = foodChange;
        this.prestigeChange = prestigeChange;
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
}
