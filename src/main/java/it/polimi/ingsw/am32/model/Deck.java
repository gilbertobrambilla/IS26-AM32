package it.polimi.ingsw.am32.model;

import it.polimi.ingsw.am32.model.cards.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a generic deck of cards.
 *
 * @param <T> the type of card in the deck
 */
public class Deck<T extends Card> {
    private final List<T> cards;

    /**
     * Initializes an empty deck.
     */
    public Deck() {
        this.cards = new ArrayList<>();
    }

    /**
     * Draws the top card from the deck.
     *
     * @return the drawn card
     */
    public T draw() {
        if (this.cards.isEmpty()) {
            throw new IllegalStateException("cannot draw from an empty deck");
        }
        // drawing from the end of the list is optimized for arraylist
        return this.cards.removeLast();
    }

    /**
     * Checks if the deck is empty.
     *
     * @return true if the deck is empty, false otherwise
     */
    public boolean isEmpty() {
        return this.cards.isEmpty();
    }

    /**
     * Adds a card to the deck.
     *
     * @param card the card to add
     */
    public void addCard(T card) {
        Objects.requireNonNull(card, "card cannot be null");

        this.cards.add(card);
    }

    /**
     * Gets the number of cards in the deck.
     *
     * @return the size of the deck
     */
    public int getSize() {
        return this.cards.size();
    }

    /**
     * Gets the era of the first card to be drawn.
     *
     * @return the era of the first card
     */
    public int getFirstCardEra() {
        if (this.cards.isEmpty()) {
            throw new IllegalStateException("cannot get era from an empty deck");
        }

        return this.cards.getLast().getEra();
    }
}
