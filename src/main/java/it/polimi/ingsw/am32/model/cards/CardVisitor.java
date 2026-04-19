package it.polimi.ingsw.am32.model.cards;

import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.model.cards.events.EventCard;

public interface CardVisitor {
    void visit(EventCard card);
    void visit(CharacterCard card);
    void visit(BuildingCard card);
}
