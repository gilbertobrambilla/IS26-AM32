package it.polimi.ingsw.am32.model.cards.visitors;

import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.NoEffect;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.ArtistCard;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.model.cards.events.EventCard;
import it.polimi.ingsw.am32.model.cards.events.HuntEvent;
import it.polimi.ingsw.am32.model.enums.GameErrorReason;
import it.polimi.ingsw.am32.model.exceptions.InvalidGameActionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AcquisitionValidatorVisitorTest {

    private List<CharacterCard> targetList;
    private AcquisitionValidatorVisitor visitor;

    @BeforeEach
    void setUp() {
        // initialize an empty list to collect valid character cards
        targetList = new ArrayList<>();

        // instantiate the visitor with the target list
        visitor = new AcquisitionValidatorVisitor(targetList);
    }

    @Test
    void constructorThrowsExceptionWhenListIsNull() {
        // expect a null pointer exception when the provided list is null
        assertThrows(NullPointerException.class, () -> new AcquisitionValidatorVisitor(null));
    }

    @Test
    void visitEventCardThrowsInvalidGameActionException() {
        // instantiate a concrete event card
        EventCard eventCard = new HuntEvent("event_1", 1, false, 1, 1);

        // verify that visiting an event card throws the expected game exception
        InvalidGameActionException exception = assertThrows(InvalidGameActionException.class, () -> visitor.visit(eventCard));

        // verify the specific error reason
        assertEquals(GameErrorReason.EVENT_CARD_NOT_ALLOWED, exception.getReason());
    }

    @Test
    void visitEventCardThrowsExceptionWhenNull() {
        // expect a null pointer exception when null is casted to event card
        assertThrows(NullPointerException.class, () -> visitor.visit((EventCard) null));
    }

    @Test
    void visitCharacterCardAddsCardToTargetList() {
        // instantiate a concrete character card
        CharacterCard characterCard = new ArtistCard("char_1", 1, 2, 2, 1);

        // visit the character card
        visitor.visit(characterCard);

        // verify that the card was added to the target list
        assertEquals(1, targetList.size());
        assertTrue(targetList.contains(characterCard));
    }

    @Test
    void visitCharacterCardThrowsExceptionWhenNull() {
        // expect a null pointer exception when null is casted to character card
        assertThrows(NullPointerException.class, () -> visitor.visit((CharacterCard) null));
    }

    @Test
    void visitBuildingCardThrowsIllegalStateException() {
        // instantiate a concrete building card using no effect
        BuildingCard buildingCard = new BuildingCard("building_1", 1, 2, 3, new NoEffect());

        // verify that visiting a building card throws an illegal state exception
        assertThrows(IllegalStateException.class, () -> visitor.visit(buildingCard));
    }

    @Test
    void visitBuildingCardThrowsExceptionWhenNull() {
        // expect a null pointer exception when null is casted to building card
        assertThrows(NullPointerException.class, () -> visitor.visit((BuildingCard) null));
    }
}