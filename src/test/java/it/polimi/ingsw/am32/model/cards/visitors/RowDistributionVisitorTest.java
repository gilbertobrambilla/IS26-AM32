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

class RowDistributionVisitorTest {

    private RowDistributionVisitor visitor;

    @BeforeEach
    void setUp() {
        // initialize the visitor before each test
        visitor = new RowDistributionVisitor();
    }

    @Test
    void constructorInitializesEmptyLists() {
        // verify that the lists are initially empty
        assertTrue(visitor.getEvents().isEmpty());
        assertTrue(visitor.getCharacters().isEmpty());
        assertEquals(0, visitor.getCharacterCount());
    }

    @Test
    void visitEventCardAddsCardToEventsList() {
        // instantiate a concrete event card
        EventCard eventCard = new HuntEvent("event_1", 1, false, 1, 1);

        // visit the event card
        visitor.visit(eventCard);

        // verify that the event card was added
        assertEquals(1, visitor.getEvents().size());
        assertTrue(visitor.getEvents().contains(eventCard));

        // verify the other list remains untouched
        assertTrue(visitor.getCharacters().isEmpty());
    }

    @Test
    void visitEventCardThrowsExceptionWhenNull() {
        // expect a null pointer exception when a null event card is visited
        assertThrows(NullPointerException.class, () -> visitor.visit((EventCard) null));
    }

    @Test
    void visitCharacterCardAddsCardToCharactersList() {
        // instantiate a concrete character card
        CharacterCard characterCard = new ArtistCard("char_1", 1, 2, 2, 1);

        // visit the character card
        visitor.visit(characterCard);

        // verify that the character card was added
        assertEquals(1, visitor.getCharacters().size());
        assertEquals(1, visitor.getCharacterCount());
        assertTrue(visitor.getCharacters().contains(characterCard));

        // verify the other list remains untouched
        assertTrue(visitor.getEvents().isEmpty());
    }

    @Test
    void visitCharacterCardThrowsExceptionWhenNull() {
        // expect a null pointer exception when a null character card is visited
        assertThrows(NullPointerException.class, () -> visitor.visit((CharacterCard) null));
    }

    @Test
    void visitBuildingCardDoesNothing() {
        // instantiate a concrete building card using no effect
        BuildingCard buildingCard = new BuildingCard("building_1", 1, 2, 3, new NoEffect());

        // visit the building card
        visitor.visit(buildingCard);

        // verify that both lists remain empty
        assertTrue(visitor.getEvents().isEmpty());
        assertTrue(visitor.getCharacters().isEmpty());
        assertEquals(0, visitor.getCharacterCount());
    }

    @Test
    void getEventsReturnsCopyOfList() {
        // instantiate a concrete event card and visit it
        EventCard eventCard = new HuntEvent("event_1", 1, false, 1, 1);
        visitor.visit(eventCard);

        // retrieve the list and attempt to modify it
        List<EventCard> returnedList = visitor.getEvents();
        returnedList.clear();

        // verify that the internal list of the visitor was not affected
        assertEquals(1, visitor.getEvents().size());
        assertTrue(visitor.getEvents().contains(eventCard));
    }

    @Test
    void getCharactersReturnsCopyOfList() {
        // instantiate a concrete character card and visit it
        CharacterCard characterCard = new ArtistCard("char_1", 1, 2, 2, 1);
        visitor.visit(characterCard);

        // retrieve the list and attempt to modify it
        List<CharacterCard> returnedList = visitor.getCharacters();
        returnedList.clear();

        // verify that the internal list of the visitor was not affected
        assertEquals(1, visitor.getCharacters().size());
        assertTrue(visitor.getCharacters().contains(characterCard));
    }
}