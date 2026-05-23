package it.polimi.ingsw.am32.model.cards.visitors;

import it.polimi.ingsw.am32.model.cards.CardVisitor;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.model.cards.events.EventCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Distributes cards into specific collections based on their type.
 */
public class RowDistributionVisitor implements CardVisitor {

    private final List<EventCard> events;
    private final List<CharacterCard> characters;

    /**
     * Initializes the visitor with empty lists for events and characters.
     */
    public RowDistributionVisitor() {
        this.events = new ArrayList<>();
        this.characters = new ArrayList<>();
    }

    /**
     * Adds the event card to the events' collection.
     *
     * @param card the event card to visit
     */
    @Override
    public void visit(EventCard card) {
        Objects.requireNonNull(card, "card cannot be null");

        this.events.add(card);
    }

    /**
     * Adds the character card to the characters' collection.
     *
     * @param card the character card to visit
     */
    @Override
    public void visit(CharacterCard card) {
        Objects.requireNonNull(card, "card cannot be null");

        this.characters.add(card);
    }

    /**
     * Ignores building cards during distribution.
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
    public List<EventCard> getEvents() {
        return new ArrayList<>(this.events);
    }

    /**
     * Provides the collected character cards.
     *
     * @return a new list containing the collected character cards
     */
    public List<CharacterCard> getCharacters() {
        return new ArrayList<>(this.characters);
    }

    /**
     * Provides the count of collected character cards.
     *
     * @return the number of collected character cards
     */
    public int getCharacterCount() {
        return this.characters.size();
    }
}
