package it.polimi.ingsw.am32.model.receipts;

import java.util.List;

public class ExtraAcquisitionReceipt {
    private final List<PlayerStatsChangeReceipt> statsChanges;

    public ExtraAcquisitionReceipt(List<PlayerStatsChangeReceipt> statsChanges) {
        this.statsChanges = statsChanges;
    }

    public List<PlayerStatsChangeReceipt> getStatsChanges() {
        return statsChanges;
    }
}
