package it.polimi.ingsw.am32.tiles;

public class Action {
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

    public boolean isValidSelection(int requestedUpper, int requestedLower, int availableUpper, int availableLower) {
        if (requestedUpper < 0) {
            throw new IllegalArgumentException("requestedUpper cannot be negative");
        }
        if (requestedLower < 0) {
            throw new IllegalArgumentException("requestedLower cannot be negative");
        }
        if (availableUpper < 0) {
            throw new IllegalArgumentException("availableUpper cannot be negative");
        }
        if (availableLower < 0) {
            throw new IllegalArgumentException("availableLower cannot be negative");
        }

        boolean isUpperValid = (requestedUpper <= this.drawsFromUpperRow) && (requestedUpper <= availableUpper);
        boolean isLowerValid = (requestedLower <= this.drawsFromLowerRow) && (requestedLower <= availableLower);

        return isUpperValid && isLowerValid;
    }
}
