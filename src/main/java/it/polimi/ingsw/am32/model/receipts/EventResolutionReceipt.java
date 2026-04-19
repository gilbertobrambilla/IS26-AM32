package it.polimi.ingsw.am32.model.receipts;

import java.util.List;

public class EventResolutionReceipt {
    private final String eventCardId;
    private final List<PlayerStatsChangeReceipt> statChanges;

    public EventResolutionReceipt(String eventCardId, List<PlayerStatsChangeReceipt> statChanges) {
        this.eventCardId = eventCardId;
        this.statChanges = statChanges;
    }

    public String getEventCardId() {
        return eventCardId;
    }

    public List<PlayerStatsChangeReceipt> getStatChanges() {
        return statChanges;
    }
}
