package it.polimi.ingsw.am32.cards.acquirable;

import it.polimi.ingsw.am32.cards.Card;

public abstract class AcquirableCard extends Card {
    private int basePrestige;

    public int getBasePrestige() {
        return this.basePrestige;
    }

    public AcquirableCard(String id, int era, int basePrestige) {
        super(id, era);
        this.basePrestige = basePrestige;
    }
}
