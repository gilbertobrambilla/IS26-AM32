package it.polimi.ingsw.am32.cards.acquirable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AcquirableCardTest {

    private static class DummyAcquirableCard extends AcquirableCard {
        public DummyAcquirableCard(String id, int era, int basePrestige) {
            super(id, era, basePrestige);
        }
    }

    private DummyAcquirableCard acquirableCard;

    @BeforeEach
    void setUp() {
        acquirableCard = new DummyAcquirableCard("AcquirableCardID", 1, 5);
    }

    @Test
    void getBasePrestigeShouldReturnConstructorValue() {
        assertEquals(5, acquirableCard.getBasePrestige());
    }

    @Test
    void inheritedGettersShouldReturnCorrectValues() {
        assertEquals("AcquirableCardID", acquirableCard.getId());
        assertEquals(1, acquirableCard.getEra());
    }

    @Test
    void isEventShouldReturnFalse() {
        assertFalse(acquirableCard.isEvent());
    }

    @Test
    void constructorShouldThrowExceptionIfBasePrestigeIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new DummyAcquirableCard("AcquirableCardID", 2, -1));
    }

    @Test
    void constructorShouldThrowExceptionIfIdIsNull() {
        assertThrows(NullPointerException.class, () -> new DummyAcquirableCard(null, 1, 3));
    }

    @Test
    void constructorShouldThrowExceptionIfEraIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new DummyAcquirableCard("AcquirableCardID", 0, 3));
        assertThrows(IllegalArgumentException.class, () -> new DummyAcquirableCard("AcquirableCardID", 4, 3));
    }

}