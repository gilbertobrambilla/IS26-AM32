package it.polimi.ingsw.am32.model.cards.events;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.cards.CardVisitor;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.model.enums.EventType;
import it.polimi.ingsw.am32.model.receipts.PlayerStatsChangeReceipt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventCardTest {

    // standard dummy event card with default priority (1)
    private static class DummyEventCard extends EventCard {
        public DummyEventCard(String id, int era, boolean isFinal) {
            super(id, era, isFinal);
        }

        @Override
        public EventType getEventType() {
            return EventType.HUNT;
        }

        @Override
        public List<PlayerStatsChangeReceipt> resolveEvent(GameState gameState) {
            return new ArrayList<>();
        }
    }

    // specific dummy event card to test overridden priority logic (e.g. sustenance event)
    private static class HighPriorityEventCard extends EventCard {
        public HighPriorityEventCard(String id, int era, boolean isFinal) {
            super(id, era, isFinal);
        }

        @Override
        protected int getResolutionPriority() {
            return 2;
        }

        @Override
        public EventType getEventType() {
            return EventType.SUSTENANCE;
        }

        @Override
        public List<PlayerStatsChangeReceipt> resolveEvent(GameState gameState) {
            return new ArrayList<>();
        }
    }

    private DummyEventCard eventCard;

    @BeforeEach
    void setUp() {
        // initialize a standard event card before each test
        eventCard = new DummyEventCard("EV_01", 1, false);
    }

    @Test
    void constructorShouldSetValuesCorrectly() {
        // verify fields initialized by the constructor and the superclass
        assertEquals("EV_01", eventCard.getId());
        assertEquals(1, eventCard.getEra());
        assertFalse(eventCard.isFinal());
    }

    @Test
    void acceptShouldThrowExceptionIfVisitorIsNull() {
        // verify exception for null visitor
        assertThrows(NullPointerException.class, () -> eventCard.accept(null));
    }

    @Test
    void acceptShouldCallVisitor() {
        // using an array to bypass the effectively final restriction
        final boolean[] wasVisited = {false};

        CardVisitor dummyVisitor = new CardVisitor() {
            @Override
            public void visit(EventCard card) {
                wasVisited[0] = true;
            }

            @Override
            public void visit(CharacterCard card) {}

            @Override
            public void visit(BuildingCard card) {}
        };

        // act
        eventCard.accept(dummyVisitor);

        // assert that the correct visit method was triggered
        assertTrue(wasVisited[0]);
    }

    @Test
    void getResolutionPriorityShouldReturnOneByDefault() {
        // verify the default priority implementation
        assertEquals(1, eventCard.getResolutionPriority());
    }

    @Test
    void compareToShouldThrowExceptionIfOtherIsNull() {
        // verify exception for null comparison
        assertThrows(NullPointerException.class, () -> eventCard.compareTo(null));
    }

    @Test
    void compareToShouldSortByPriorityFirst() {
        HighPriorityEventCard highPriorityEvent = new HighPriorityEventCard("EV_02", 1, false);

        // standard event (1) compared to high priority event (2) should return a negative number
        assertTrue(eventCard.compareTo(highPriorityEvent) < 0);

        // high priority event (2) compared to standard event (1) should return a positive number
        assertTrue(highPriorityEvent.compareTo(eventCard) > 0);
    }

    @Test
    void compareToShouldSortByEraIfPrioritiesAreTheSame() {
        DummyEventCard eraTwoEvent = new DummyEventCard("EV_03", 2, false);

        // era 1 compared to era 2 should return a negative number
        assertTrue(eventCard.compareTo(eraTwoEvent) < 0);

        // era 2 compared to era 1 should return a positive number
        assertTrue(eraTwoEvent.compareTo(eventCard) > 0);
    }

    @Test
    void compareToShouldReturnZeroIfPriorityAndEraAreTheSame() {
        DummyEventCard identicalEvent = new DummyEventCard("EV_04", 1, true);

        // comparison of identical priority and era should result in 0
        assertEquals(0, eventCard.compareTo(identicalEvent));
    }
}