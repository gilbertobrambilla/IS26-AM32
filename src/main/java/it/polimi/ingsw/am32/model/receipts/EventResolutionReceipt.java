package it.polimi.ingsw.am32.model.receipts;

import java.util.List;

/**
 * Represents a receipt for the resolution of an event.
 */
public class EventResolutionReceipt {
    private final String eventCardId;
    private final List<PlayerStatsChangeReceipt> statChanges;

    /**
     * Initializes the event resolution receipt with the event card id and statistics changes.
     *
     * @param eventCardId the id of the resolved event card
     * @param statChanges the list of player statistics changes
     */
    public EventResolutionReceipt(String eventCardId, List<PlayerStatsChangeReceipt> statChanges) {
        this.eventCardId = eventCardId;
        this.statChanges = statChanges;
    }

    /**
     * Gets the id of the resolved event card.
     *
     * @return the event card id
     */
    public String getEventCardId() {
        return this.eventCardId;
    }

    /**
     * Gets the list of player statistics changes.
     *
     * @return the list of statistics changes
     */
    public List<PlayerStatsChangeReceipt> getStatChanges() {
        return this.statChanges;
    }
}
