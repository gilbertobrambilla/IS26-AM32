package it.polimi.ingsw.am32.model.cards;

import java.util.Objects;

/**
 * Represents an abstract card in the game.
 */
public abstract class Card {
    private final String id;
    private final int era;

    /**
     * Initializes the card with an identifier and an era.
     *
     * @param id the unique identifier of the card
     * @param era the era the card belongs to
     */
    public Card(String id, int era) {
        Objects.requireNonNull(id, "id cannot be null");

        if (era < 1 || era > 3) {
            throw new IllegalArgumentException("era must be between 1 and 3");
        }

        this.id = id;
        this.era = era;
    }

    /**
     * Provides the era of the card.
     *
     * @return the era of the card
     */
    public int getEra() {
        return this.era;
    }

    /**
     * Provides the identifier of the card.
     *
     * @return the unique identifier of the card
     */
    public String getId() {
        return this.id;
    }

    /**
     * Accepts a visitor to perform operations on the card.
     *
     * @param visitor the visitor to accept
     */
    public abstract void accept(CardVisitor visitor);
}
