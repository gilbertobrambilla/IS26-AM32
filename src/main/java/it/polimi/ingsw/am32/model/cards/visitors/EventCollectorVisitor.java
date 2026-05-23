package it.polimi.ingsw.am32.model.cards.visitors;

import it.polimi.ingsw.am32.model.cards.CardVisitor;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.model.cards.events.EventCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Collects event cards from a generic collection of cards.
 */
public class EventCollectorVisitor implements CardVisitor {
    private final List<EventCard> events;

    /**
     * Initializes the visitor with an empty list of events.
     */
    public EventCollectorVisitor() {
        this.events = new ArrayList<>();
    }

    /**
     * Collects the visited event card.
     *
     * @param card the event card to visit
     */
    @Override
    public void visit(EventCard card) {
        Objects.requireNonNull(card, "card cannot be null");

        this.events.add(card);
    }

    /**
     * Ignores character cards during an event collection.
     *
     * @param card the character card to visit
     */
    @Override
    public void visit(CharacterCard card) {
        // character cards are intentionally ignored
    }

    /**
     * Ignores building cards during an event collection.
     *
     * @param card the building card to visit
     */
    @Override
    public void visit(BuildingCard card) {
        // building cards are intentionally ignored
    }

    /**
     * Provides the collected event cards.
     *
     * @return a new list containing the collected event cards
     */
    public List<EventCard> getCollectedEvents() {
        return new ArrayList<>(this.events);
    }
}
