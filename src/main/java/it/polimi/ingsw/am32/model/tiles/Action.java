package it.polimi.ingsw.am32.model.tiles;

public class Action  {
    private final int drawsFromUpperRow;
    private final int drawsFromLowerRow;
    private final int foodBonus;

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

    public int getDrawsFromUpperRow() {
        return this.drawsFromUpperRow;
    }

    public int getDrawsFromLowerRow() {
        return drawsFromLowerRow;
    }

    public int getFoodBonus() {
        return foodBonus;
    }

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

        // the player must take at least all available character cards, up to the tile limit
        int mandatoryUpper = Math.min(this.drawsFromUpperRow, characterCardsUpper);
        int mandatoryLower = Math.min(this.drawsFromLowerRow, characterCardsLower);

        // after mandatory characters are taken, remaining slots can be optionally filled with buildings
        int maxUpper = Math.min(this.drawsFromUpperRow, characterCardsUpper + affordableBuildingsUpper);
        int maxLower = Math.min(this.drawsFromLowerRow, characterCardsLower + affordableBuildingsLower);

        return requestedUpper >= mandatoryUpper && requestedUpper <= maxUpper
            && requestedLower >= mandatoryLower && requestedLower <= maxLower;
    }
}
