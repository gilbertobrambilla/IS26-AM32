package it.polimi.ingsw.am32.cards.acquirable;

import it.polimi.ingsw.am32.cards.Card;

public abstract class AcquirableCard extends Card {
    private final int basePrestige;

    public AcquirableCard(String id, int era, int basePrestige) {
        super(id, era);

        if (basePrestige < 0) {
            throw new IllegalArgumentException("basePrestige cannot be negative");
        }

        this.basePrestige = basePrestige;
    }

    @Override
    public boolean isEvent() {
        return false;
    }

    public int getBasePrestige() {
        return this.basePrestige;
    }
}
