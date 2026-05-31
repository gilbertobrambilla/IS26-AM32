package it.polimi.ingsw.am32.model.receipts;

/**
 * A receipt for an action resolution is represented.
 */
public class ActionReceipt {
    private final PlayerStatsChangeReceipt statsChange;
    private final int turnOrderPosition;

    /**
     * An action receipt is initialized with its resulting statistics change and turn order position.
     *
     * @param statsChange the resulting statistics change
     * @param turnOrderPosition the position assigned on the turn order tile
     */
    public ActionReceipt(PlayerStatsChangeReceipt statsChange, int turnOrderPosition) {
        this.statsChange = statsChange;
        this.turnOrderPosition = turnOrderPosition;
    }

    /**
     * The resulting statistics change is returned.
     *
     * @return the resulting statistics change
     */
    public PlayerStatsChangeReceipt getStatsChange() {
        return statsChange;
    }

    /**
     * The assigned turn order position is returned.
     *
     * @return the turn order position
     */
    public int getTurnOrderPosition() {
        return turnOrderPosition;
    }
}
