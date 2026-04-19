package it.polimi.ingsw.am32.model.cards.events;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.enums.EventType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EventCardTest {

    //creating a test concrete class
    private static class DummyEventCard extends EventCard {

        public DummyEventCard(String id, int era, boolean isFinal) {
            super(id, era, isFinal);
        }

        @Override
        public EventType getEventType() {
            return null; //skipping the getter test
        }

        @Override
        public void resolveEvent(GameState gameState) {
            //skipping test
        }
    }

    @Test
    void testIsEvent() {
        //creating a fake eventCard
        EventCard eventCard = new DummyEventCard("DUMMY_EVENT", 1, false);

        //verifying that isEvent() returns always true
        assertTrue(eventCard.isEvent());
    }
}