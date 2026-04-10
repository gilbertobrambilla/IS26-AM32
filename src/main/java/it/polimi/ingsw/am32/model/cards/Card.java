package it.polimi.ingsw.am32.model.cards;

import java.util.Objects;

public abstract class Card {
    private final String id;
    private final int era;

    public Card(String id, int era) {
        Objects.requireNonNull(id, "id cannot be null");

        if (era < 1 || era > 3) {
            throw new IllegalArgumentException("era must be between 1 and 3");
        }

        this.id = id;
        this.era = era;
    }

    public int getEra() {
        return this.era;
    }

    public String getId() {
        return this.id;
    }

    public abstract void accept(CardVisitor visitor);
}
