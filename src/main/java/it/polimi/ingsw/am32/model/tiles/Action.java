package it.polimi.ingsw.am32.model.tiles;

/**
 * Represents the action associated with an offer tile.
 */
public class Action {
    private final int drawsFromUpperRow;
    private final int drawsFromLowerRow;
    private final int foodBonus;

    /**
     * Initializes the action with the specific draws and bonuses.
     *
     * @param drawsFromUpperRow the number of draws from the upper row
     * @param drawsFromLowerRow the number of draws from the lower row
     * @param foodBonus the amount of food bonus
     */
    public Action(int drawsFromUpperRow, int drawsFromLowerRow, int foodBonus) {
        if (drawsFromUpperRow < 0) {
            throw new IllegalArgumentException("drawsFromUpperRow cannot be negative");
        }
        if (drawsFromLowerRow < 0) {
            throw new IllegalArgumentException("drawsFromLowerRow cannot be negative");
        }
        if (foodBonus < 0) {
            throw new IllegalArgumentException("foodBonus cannot be negative");
        }

        this.drawsFromUpperRow = drawsFromUpperRow;
        this.drawsFromLowerRow = drawsFromLowerRow;
        this.foodBonus = foodBonus;
    }

    /**
     * Gets the number of draws allowed from the upper row.
     *
     * @return the draws from the upper row
     */
    public int getDrawsFromUpperRow() {
        return this.drawsFromUpperRow;
    }

    /**
     * Gets the number of draws allowed from the lower row.
     *
     * @return the draws from the lower row
     */
    public int getDrawsFromLowerRow() {
        return this.drawsFromLowerRow;
    }

    /**
     * Gets the food bonus associated with the action.
     *
     * @return the food bonus
     */
    public int getFoodBonus() {
        return this.foodBonus;
    }

    /**
     * Validates if the selected draws are allowed by the action constraints.
     *
     * @param requestedUpper the requested draws from the upper row
     * @param requestedLower the requested draws from the lower row
     * @param characterCardsUpper the available character cards in the upper row
     * @param characterCardsLower the available character cards in the lower row
     * @param affordableBuildingsUpper the affordable buildings in the upper row
     * @param affordableBuildingsLower the affordable buildings in the lower row
     * @return true if the selection is valid, false otherwise
     */
    public boolean isValidSelection(
            int requestedUpper, int requestedLower,
            int characterCardsUpper, int characterCardsLower,
            int affordableBuildingsUpper, int affordableBuildingsLower) {

        if (requestedUpper < 0) {
            throw new IllegalArgumentException("requestedUpper cannot be negative");
        }
        if (requestedLower < 0) {
            throw new IllegalArgumentException("requestedLower cannot be negative");
        }
        if (characterCardsUpper < 0) {
            throw new IllegalArgumentException("characterCardsUpper cannot be negative");
        }
        if (characterCardsLower < 0) {
            throw new IllegalArgumentException("characterCardsLower cannot be negative");
        }
        if (affordableBuildingsUpper < 0) {
            throw new IllegalArgumentException("affordableBuildingsUpper cannot be negative");
        }
        if (affordableBuildingsLower < 0) {
            throw new IllegalArgumentException("affordableBuildingsLower cannot be negative");
        }

        // mandatory characters to be taken are calculated based on tile limits
        int mandatoryUpper = Math.min(this.drawsFromUpperRow, characterCardsUpper);
        int mandatoryLower = Math.min(this.drawsFromLowerRow, characterCardsLower);

        // maximum limit for buildings and characters is evaluated
        int maxUpper = Math.min(this.drawsFromUpperRow, characterCardsUpper + affordableBuildingsUpper);
        int maxLower = Math.min(this.drawsFromLowerRow, characterCardsLower + affordableBuildingsLower);

        return requestedUpper >= mandatoryUpper && requestedUpper <= maxUpper
            && requestedLower >= mandatoryLower && requestedLower <= maxLower;
    }
}
