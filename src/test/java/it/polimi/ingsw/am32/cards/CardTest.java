package it.polimi.ingsw.am32.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    private static class DummyCard extends Card {
        public DummyCard(String id, int era) {
            super(id, era);
        }

        @Override
        public boolean isEvent() {
            return false;
        }
    }

    private DummyCard card;

    @BeforeEach
    void setUp() {
        card = new DummyCard("CardID", 1);
    }

    @Test
    void gettersShouldReturnConstructorValues() {
        assertEquals("CardID", card.getId());
        assertEquals(1, card.getEra());
    }

    @Test
    void constructorShouldThrowExceptionIfIdIsNull() {
        assertThrows(NullPointerException.class, () -> new DummyCard(null, 1));
    }

    @Test
    void constructorShouldThrowExceptionIfEraIsLessThanOne() {
        assertThrows(IllegalArgumentException.class, () -> new DummyCard("CardID", 0));
        assertThrows(IllegalArgumentException.class, () -> new DummyCard("CardID", -1));
    }

    @Test
    void constructorShouldThrowExceptionIfEraIsGreaterThanThree() {
        assertThrows(IllegalArgumentException.class, () -> new DummyCard("CardID", 4));
    }

    @Test
    void constructorShouldNotThrowExceptionIfEraIsValid() {
        assertDoesNotThrow(() -> new DummyCard("CardID", 1));
        assertDoesNotThrow(() -> new DummyCard("CardID", 2));
        assertDoesNotThrow(() -> new DummyCard("CardID", 3));
    }

}