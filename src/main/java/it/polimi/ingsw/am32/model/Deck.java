package it.polimi.ingsw.am32.model;

import it.polimi.ingsw.am32.model.cards.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Deck<T extends Card> {
    private final List<T> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

    public T draw() {
        if (this.cards.isEmpty()) {
            throw new IllegalStateException("cannot draw from an empty deck");
        }
        // drawing from the end of the list is optimized for arraylist
        return this.cards.removeLast();
    }

    public boolean isEmpty() {
        return this.cards.isEmpty();
    }

    public void addCard(T card) {
        Objects.requireNonNull(card, "card cannot be null");

        this.cards.add(card);
    }

    public int getSize() {
        return this.cards.size();
    }

    public int getFirstCardEra() {
        if (this.cards.isEmpty()) {
            throw new IllegalStateException("cannot get era from an empty deck");
        }

        return this.cards.getLast().getEra();
    }
}
