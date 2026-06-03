package it.polimi.ingsw.am32.model.cards.visitors;

import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.NoEffect;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.ArtistCard;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.model.cards.events.EventCard;
import it.polimi.ingsw.am32.model.cards.events.HuntEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventCollectorVisitorTest {

    private EventCollectorVisitor visitor;

    @BeforeEach
    void setUp() {
        // initialize the visitor before each test
        visitor = new EventCollectorVisitor();
    }

    @Test
    void constructorInitializesEmptyList() {
        // verify that the collected events list is initially empty
        assertTrue(visitor.getCollectedEvents().isEmpty());
    }

    @Test
    void visitEventCardAddsCardToList() {
        // instantiate a concrete event card
        EventCard eventCard = new HuntEvent("event_1", 1, false, 1, 1);

        // visit the event card
        visitor.visit(eventCard);

        // retrieve the collected events
        List<EventCard> collectedEvents = visitor.getCollectedEvents();

        // verify that the event card was added to the list
        assertEquals(1, collectedEvents.size());
        assertTrue(collectedEvents.contains(eventCard));
    }

    @Test
    void visitEventCardThrowsExceptionWhenNull() {
        // expect a null pointer exception when a null event card is visited
        assertThrows(NullPointerException.class, () -> visitor.visit((EventCard) null));
    }

    @Test
    void visitCharacterCardDoesNothing() {
        // instantiate a concrete character card
        CharacterCard characterCard = new ArtistCard("char_1", 1, 2, 2, 1);

        // visit the character card
        visitor.visit(characterCard);

        // verify that the collected events list remains empty
        assertTrue(visitor.getCollectedEvents().isEmpty());
    }

    @Test
    void visitBuildingCardDoesNothing() {
        // instantiate a concrete building card using no effect
        BuildingCard buildingCard = new BuildingCard("building_1", 1, 2, 3, new NoEffect());

        // visit the building card
        visitor.visit(buildingCard);

        // verify that the collected events list remains empty
        assertTrue(visitor.getCollectedEvents().isEmpty());
    }

    @Test
    void getCollectedEventsReturnsCopyOfList() {
        // instantiate a concrete event card and visit it
        EventCard eventCard = new HuntEvent("event_1", 1, false, 1, 1);
        visitor.visit(eventCard);

        // retrieve the list and attempt to modify it
        List<EventCard> returnedList = visitor.getCollectedEvents();
        returnedList.clear();

        // verify that the internal list of the visitor was not affected
        assertEquals(1, visitor.getCollectedEvents().size());
        assertTrue(visitor.getCollectedEvents().contains(eventCard));
    }
}