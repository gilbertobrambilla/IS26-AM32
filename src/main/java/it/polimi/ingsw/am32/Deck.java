package it.polimi.ingsw.am32;

import it.polimi.ingsw.am32.cards.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Deck {
    private final List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    public Card draw() {
        if (this.cards.isEmpty()) {
            return null;
        }
        // drawing from the end of the list is optimized for arraylist
        return this.cards.removeLast();
    }

    public boolean isEmpty() {
        return this.cards.isEmpty();
    }

    public void addCard(Card card) {
        Objects.requireNonNull(card, "card cannot be null");

        this.cards.add(card);
    }

    public int getFirstCardEra() {
        return this.cards.getLast().getEra();
    }
}
