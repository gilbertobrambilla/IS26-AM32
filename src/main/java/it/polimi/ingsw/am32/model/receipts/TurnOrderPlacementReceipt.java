package it.polimi.ingsw.am32.model.receipts;

public class TurnOrderPlacementReceipt {
    private final int slotIndex;
    private final int foodModifier;
    private final int prestigeModifier;

    public TurnOrderPlacementReceipt(int slotIndex, int foodModifier, int prestigeModifier) {
        this.slotIndex = slotIndex;
        this.foodModifier = foodModifier;
        this.prestigeModifier = prestigeModifier;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public int getFoodModifier() {
        return foodModifier;
    }

    public int getPrestigeModifier() {
        return prestigeModifier;
    }
}
