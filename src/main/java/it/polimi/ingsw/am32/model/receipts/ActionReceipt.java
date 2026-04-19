package it.polimi.ingsw.am32.model.receipts;

import java.util.List;

public class ActionReceipt {
    private final List<PlayerStatsChangeReceipt> statsChanges;
    private final TotemReturnReceipt totemDetails;

    public ActionReceipt(List<PlayerStatsChangeReceipt> statsChanges, TotemReturnReceipt totemDetails) {
        this.statsChanges = statsChanges;
        this.totemDetails = totemDetails;
    }

    public List<PlayerStatsChangeReceipt> getStatsChanges() {
        return statsChanges;
    }

    public TotemReturnReceipt getTotemDetails() {
        return totemDetails;
    }
}
