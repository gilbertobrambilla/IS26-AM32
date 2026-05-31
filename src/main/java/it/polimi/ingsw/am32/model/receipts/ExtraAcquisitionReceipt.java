package it.polimi.ingsw.am32.model.receipts;

/**
 * A receipt for an extra acquisition resolution is represented.
 */
public class ExtraAcquisitionReceipt {
    private final PlayerStatsChangeReceipt statsChange;

    /**
     * An extra acquisition receipt is initialized with its resulting statistics change.
     *
     * @param statsChange the resulting statistics change
     */
    public ExtraAcquisitionReceipt(PlayerStatsChangeReceipt statsChange) {
        this.statsChange = statsChange;
    }

    /**
     * The resulting statistics change is returned.
     *
     * @return the resulting statistics change
     */
    public PlayerStatsChangeReceipt getStatsChange() {
        return this.statsChange;
    }
}
