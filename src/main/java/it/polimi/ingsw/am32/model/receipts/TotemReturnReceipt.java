package it.polimi.ingsw.am32.model.receipts;

/**
 * Represents a receipt for a totem return action.
 */
public class TotemReturnReceipt {
    private final String playerNickname;
    private final int turnOrderPosition;
    private final PlayerStatsChangeReceipt statsChange;
    private final String nextPlayerNickname;

    /**
     * Initializes the totem return receipt with the specified details.
     *
     * @param playerNickname the nickname of the player
     * @param turnOrderPosition the position in the turn order
     * @param statsChange the statistics change receipt
     * @param nextPlayerNickname the nickname of the next player
     */
    public TotemReturnReceipt(String playerNickname, int turnOrderPosition, PlayerStatsChangeReceipt statsChange, String nextPlayerNickname) {
        this.playerNickname = playerNickname;
        this.turnOrderPosition = turnOrderPosition;
        this.statsChange = statsChange;
        this.nextPlayerNickname = nextPlayerNickname;
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
     * Gets the position in turn order.
     *
     * @return the turn order position
     */
    public int getTurnOrderPosition() {
        return this.turnOrderPosition;
    }

    /**
     * Gets the statistics change receipt.
     *
     * @return the statistics change
     */
    public PlayerStatsChangeReceipt getStatsChange() {
        return this.statsChange;
    }

    /**
     * Gets the nickname of the next player.
     *
     * @return the next player nickname
     */
    public String getNextPlayerNickname() {
        return this.nextPlayerNickname;
    }
}
