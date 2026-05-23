package it.polimi.ingsw.am32.model.receipts;

/**
 * Represents a receipt for a turn order placement.
 */
public class TurnOrderPlacementReceipt {
    private final int slotIndex;
    private final int foodModifier;
    private final int prestigeModifier;

    /**
     * Initializes the turn order placement receipt with the specified modifiers and index.
     *
     * @param slotIndex the index of the slot
     * @param foodModifier the modifier for food
     * @param prestigeModifier the modifier for prestige
     */
    public TurnOrderPlacementReceipt(int slotIndex, int foodModifier, int prestigeModifier) {
        this.slotIndex = slotIndex;
        this.foodModifier = foodModifier;
        this.prestigeModifier = prestigeModifier;
    }

    /**
     * Gets the index of the slot.
     *
     * @return the slot index
     */
    public int getSlotIndex() {
        return this.slotIndex;
    }

    /**
     * Gets the modifier for food.
     *
     * @return the food modifier
     */
    public int getFoodModifier() {
        return this.foodModifier;
    }

    /**
     * Gets the modifier for prestige.
     *
     * @return the prestige modifier
     */
    public int getPrestigeModifier() {
        return this.prestigeModifier;
    }
}
