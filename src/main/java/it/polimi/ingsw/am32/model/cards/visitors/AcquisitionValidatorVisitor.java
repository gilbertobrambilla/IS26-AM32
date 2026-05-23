package it.polimi.ingsw.am32.model.cards.visitors;

import it.polimi.ingsw.am32.model.cards.CardVisitor;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.model.cards.events.EventCard;
import it.polimi.ingsw.am32.model.enums.GameErrorReason;
import it.polimi.ingsw.am32.model.exceptions.InvalidGameActionException;

import java.util.List;
import java.util.Objects;

/**
 * Validates the acquisition of different card types and collects valid ones.
 */
public class AcquisitionValidatorVisitor implements CardVisitor {
    private final List<CharacterCard> targetList;

    /**
     * Initializes the visitor with a target list for character cards.
     *
     * @param targetList list where valid character cards are collected
     */
    public AcquisitionValidatorVisitor(List<CharacterCard> targetList) {
        Objects.requireNonNull(targetList, "targetList cannot be null");

        this.targetList = targetList;
    }

    /**
     * Rejects event cards since they cannot be acquired.
     *
     * @param card the event card to visit
     * @throws InvalidGameActionException if an event card is visited
     */
    @Override
    public void visit(EventCard card) {
        Objects.requireNonNull(card, "card cannot be null");

        // exception is thrown immediately to prevent event acquisition
        throw new InvalidGameActionException(
                GameErrorReason.EVENT_CARD_NOT_ALLOWED,
                "event cards cannot be selected"
        );
    }

    /**
     * Validates and collects a character card.
     *
     * @param card the character card to visit
     */
    @Override
    public void visit(CharacterCard card) {
        Objects.requireNonNull(card, "card cannot be null");

        // valid character card is added to the target collection
        this.targetList.add(card);
    }

    /**
     * Rejects building cards since they are not processed as tribe cards.
     *
     * @param card the building card to visit
     * @throws IllegalStateException if a building card is visited
     */
    @Override
    public void visit(BuildingCard card) {
        Objects.requireNonNull(card, "card cannot be null");

        // exception is thrown as building cards are not handled by this flow
        throw new IllegalStateException("building cards cannot be processed as tribe cards");
    }
}
