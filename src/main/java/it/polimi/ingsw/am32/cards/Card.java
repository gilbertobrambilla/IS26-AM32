package it.polimi.ingsw.am32.cards;

public abstract class Card {
    private String id;
    private int era;

    public Card(String id, int era) {
        this.id = id;
        this.era = era;
    }

    public int getEra() {
        return this.era;
    }

    public String getId() {
        return this.id;
    }
}
