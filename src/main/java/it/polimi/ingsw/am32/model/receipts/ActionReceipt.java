package it.polimi.ingsw.am32.model.receipts;

import java.util.List;

/**
 * Represents a receipt for an action containing statistics changes and totem details.
 */
public class ActionReceipt {
    private final List<PlayerStatsChangeReceipt> statsChanges;
    private final TotemReturnReceipt totemDetails;

    /**
     * Initializes the action receipt with the given statistics changes and totem details.
     *
     * @param statsChanges the list of player statistics changes
     * @param totemDetails the details of the returned totem
     */
    public ActionReceipt(List<PlayerStatsChangeReceipt> statsChanges, TotemReturnReceipt totemDetails) {
        this.statsChanges = statsChanges;
        this.totemDetails = totemDetails;
    }

    /**
     * Gets the list of player statistics changes.
     *
     * @return the list of statistics changes
     */
    public List<PlayerStatsChangeReceipt> getStatsChanges() {
        return this.statsChanges;
    }

    /**
     * Gets the details of the returned totem.
     *
     * @return the totem details
     */
    public TotemReturnReceipt getTotemDetails() {
        return this.totemDetails;
    }
}
