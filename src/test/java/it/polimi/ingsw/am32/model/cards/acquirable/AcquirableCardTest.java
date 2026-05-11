package it.polimi.ingsw.am32.model.cards.acquirable;

import it.polimi.ingsw.am32.model.cards.CardVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AcquirableCardTest {

    //dummy class to instantiate the abstract acquirablecard class
    private static class DummyAcquirableCard extends AcquirableCard {

        public DummyAcquirableCard(String id, int era, int basePrestige) {
            super(id, era, basePrestige);
        }

        @Override
        public void accept(CardVisitor visitor) {
            //empty implementation for testing purposes
        }
    }

    private DummyAcquirableCard acquirableCard;

    @BeforeEach
    void setUp() {
        //initialize a dummy acquirable card before each test with valid parameters
        acquirableCard = new DummyAcquirableCard("ACQ_01", 1, 5);
    }

    @Test
    void constructorShouldSetValuesCorrectly() {
        //verify getters from the superclass (card) and the class itself
        assertEquals("ACQ_01", acquirableCard.getId());
        assertEquals(1, acquirableCard.getEra());
        assertEquals(5, acquirableCard.getBasePrestige());
    }

    @Test
    void constructorShouldThrowExceptionIfBasePrestigeIsNegative() {
        //verify exception is thrown for negative base prestige
        assertThrows(IllegalArgumentException.class, () -> new DummyAcquirableCard("ACQ_02", 1, -1));
    }

    @Test
    void constructorShouldAllowZeroBasePrestige() {
        //verify that zero is considered a valid base prestige value
        assertDoesNotThrow(() -> new DummyAcquirableCard("ACQ_03", 1, 0));
    }
}