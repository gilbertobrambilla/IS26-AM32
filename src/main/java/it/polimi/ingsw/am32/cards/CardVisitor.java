package it.polimi.ingsw.am32.cards;

import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.cards.events.EventCard;

public interface CardVisitor {
    void visit(EventCard card);
    void visit(CharacterCard card);
    void visit(BuildingCard card);
}
