package it.polimi.ingsw.am32.model.cards;

import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.model.cards.events.EventCard;

/**
 * Defines the visitor interface for processing different types of cards.
 */
public interface CardVisitor {

    /**
     * Visits an event card.
     *
     * @param card the event card to visit
     */
    void visit(EventCard card);

    /**
     * Visits a character card.
     *
     * @param card the character card to visit
     */
    void visit(CharacterCard card);

    /**
     * Visits a building card.
     *
     * @param card the building card to visit
     */
    void visit(BuildingCard card);
}
