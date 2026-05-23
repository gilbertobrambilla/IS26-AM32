package it.polimi.ingsw.am32.model.cards.acquirable;

import it.polimi.ingsw.am32.model.cards.Card;

/**
 * Represents an abstract acquirable card in the game.
 */
public abstract class AcquirableCard extends Card {
    private final int basePrestige;

    /**
     * Initializes the acquirable card with an identifier, an era, and base prestige.
     *
     * @param id the unique identifier of the card
     * @param era the era the card belongs to
     * @param basePrestige the base prestige value of the card
     */
    public AcquirableCard(String id, int era, int basePrestige) {
        super(id, era);

        if (basePrestige < 0) {
            throw new IllegalArgumentException("basePrestige cannot be negative");
        }

        this.basePrestige = basePrestige;
    }

    /**
     * Gets the base prestige of the card.
     *
     * @return the base prestige value
     */
    public int getBasePrestige() {
        return this.basePrestige;
    }
}
