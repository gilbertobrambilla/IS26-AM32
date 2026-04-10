package it.polimi.ingsw.am32.model.cards.visitors;

import it.polimi.ingsw.am32.model.cards.CardVisitor;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.model.cards.events.EventCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventCollectorVisitor implements CardVisitor {
    private final List<EventCard> events;

    public EventCollectorVisitor() {
        this.events = new ArrayList<>();
    }

    @Override
    public void visit(EventCard card) {
        Objects.requireNonNull(card, "card cannot be null");

        this.events.add(card);
    }

    @Override
    public void visit(CharacterCard card) {
        // character cards are intentionally ignored during event collection
    }

    @Override
    public void visit(BuildingCard card) {
        // building cards are intentionally ignored during event collection
    }

    public List<EventCard> getCollectedEvents() {
        // a new list is returned to prevent internal state exposure
        return new ArrayList<>(this.events);
    }
}
