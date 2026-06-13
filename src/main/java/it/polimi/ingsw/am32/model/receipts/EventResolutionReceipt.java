package it.polimi.ingsw.am32.model.receipts;

import java.util.List;

/**
 * A receipt for an event resolution is represented.
 */
public class EventResolutionReceipt {
    private final String eventCardId;
    private final List<PlayerStatsChangeReceipt> statsChanges;

    /**
     * An event resolution receipt is initialized with the event card id and statistics changes.
     *
     * @param eventCardId the id of the resolved event card
     * @param statsChanges the list of player statistics changes
     */
    public EventResolutionReceipt(String eventCardId, List<PlayerStatsChangeReceipt> statsChanges) {
        this.eventCardId = eventCardId;
        this.statsChanges = List.copyOf(statsChanges);
    }

    /**
     * The id of the resolved event card is returned.
     *
     * @return the event card id
     */
    public String getEventCardId() {
        return this.eventCardId;
    }

    /**
     * The list of player statistics changes is returned.
     *
     * @return the list of statistics changes
     */
    public List<PlayerStatsChangeReceipt> getStatsChanges() {
        return this.statsChanges;
    }
}
