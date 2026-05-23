package it.polimi.ingsw.am32.model.receipts;

import java.util.List;

/**
 * Represents a receipt for an extra acquisition phase.
 */
public class ExtraAcquisitionReceipt {
    private final List<PlayerStatsChangeReceipt> statsChanges;

    /**
     * Initializes the extra acquisition receipt with the given statistics changes.
     *
     * @param statsChanges the list of player statistics changes
     */
    public ExtraAcquisitionReceipt(List<PlayerStatsChangeReceipt> statsChanges) {
        this.statsChanges = statsChanges;
    }

    /**
     * Gets the list of player statistics changes.
     *
     * @return the list of statistics changes
     */
    public List<PlayerStatsChangeReceipt> getStatsChanges() {
        return this.statsChanges;
    }
}
