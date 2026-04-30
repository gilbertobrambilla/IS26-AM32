package it.polimi.ingsw.am32.model.cards.visitors;

import it.polimi.ingsw.am32.model.cards.CardVisitor;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.model.cards.events.EventCard;
import it.polimi.ingsw.am32.model.enums.GameErrorReason;
import it.polimi.ingsw.am32.model.exceptions.InvalidActionException;

import java.util.List;
import java.util.Objects;

public class AcquisitionValidatorVisitor implements CardVisitor {
    private final List<CharacterCard> targetList;

    public AcquisitionValidatorVisitor(List<CharacterCard> targetList) {
        Objects.requireNonNull(targetList, "targetList cannot be null");

        this.targetList = targetList;
    }

    @Override
    public void visit(EventCard card) {
        Objects.requireNonNull(card, "card cannot be null");

        // exception is thrown immediately to prevent event acquisition
        throw new InvalidActionException(
                GameErrorReason.EVENT_CARD_NOT_ALLOWED,
                "event cards cannot be selected"
        );
    }

    @Override
    public void visit(CharacterCard card) {
        Objects.requireNonNull(card, "card cannot be null");

        // the valid character card is directly added to the target collection, avoiding explicit type casting
        this.targetList.add(card);
    }

    @Override
    public void visit(BuildingCard card) {
        Objects.requireNonNull(card, "card cannot be null");

        // exception is thrown as building cards are not handled by this specific flow
        throw new IllegalStateException("building cards cannot be processed as tribe cards");
    }
}
