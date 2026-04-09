package it.polimi.ingsw.am32.cards.visitors;

import it.polimi.ingsw.am32.cards.CardVisitor;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.cards.events.EventCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RowDistributionVisitor implements CardVisitor {
    private final List<EventCard> events;
    private final List<CharacterCard> characters;

    public RowDistributionVisitor() {
        this.events = new ArrayList<>();
        this.characters = new ArrayList<>();
    }

    @Override
    public void visit(EventCard card) {
        Objects.requireNonNull(card, "card cannot be null");

        this.events.add(card);
    }

    @Override
    public void visit(CharacterCard card) {
        Objects.requireNonNull(card, "card cannot be null");

        this.characters.add(card);
    }

    @Override
    public void visit(BuildingCard card) {
        // building cards are intentionally ignored during tribe deck distribution
    }

    public List<EventCard> getEvents() {
        // a new list is returned to prevent internal state exposure
        return new ArrayList<>(this.events);
    }

    public List<CharacterCard> getCharacters() {
        // a new list is returned to prevent internal state exposure
        return new ArrayList<>(this.characters);
    }

    public int getCharacterCount() {
        // the size is returned directly to allow looping without creating new lists
        return this.characters.size();
    }
}
