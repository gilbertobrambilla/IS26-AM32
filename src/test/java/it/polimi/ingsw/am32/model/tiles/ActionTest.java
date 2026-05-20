package it.polimi.ingsw.am32.model.tiles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ActionTest {

    private Action action;

    @BeforeEach
    void setUp() {
        // Create a standard action before each test (max 2 upper, max 1 lower, 3 food bonus)
        action = new Action(2, 1, 3);
    }

    @Test
    void constructorShouldSetValuesCorrectly() {
        // Act and Assert: verify that getters return the setup values
        assertEquals(2, action.getDrawsFromUpperRow());
        assertEquals(1, action.getDrawsFromLowerRow());
        assertEquals(3, action.getFoodBonus());
    }

    @Test
    void constructorShouldThrowExceptionIfDrawsFromUpperRowIsNegative() {
        // Arrange, Act and Assert: exception expected if upper draw is negative
        assertThrows(IllegalArgumentException.class, () -> {
            new Action(-1, 1, 3);
        });
    }

    @Test
    void constructorShouldThrowExceptionIfDrawsFromLowerRowIsNegative() {
        // Arrange, Act and Assert: exception expected if lower draw is negative
        assertThrows(IllegalArgumentException.class, () -> {
            new Action(2, -1, 3);
        });
    }

    @Test
    void constructorShouldThrowExceptionIfFoodBonusIsNegative() {
        // Arrange, Act and Assert: exception expected if food bonus is negative
        assertThrows(IllegalArgumentException.class, () -> {
            new Action(2, 1, -3);
        });
    }

    @Test
    void isValidSelectionShouldReturnTrueForValidRequests() {
        // Act and Assert: valid if requested exactly the maximum allowed, covered by character cards
        assertTrue(action.isValidSelection(2, 1, 5, 5, 0, 0));

        // Act and Assert: valid if requested less than maximum (e.g. 1) because there are no mandatory
        // character cards to pick, but buildings are available
        assertTrue(action.isValidSelection(1, 0, 0, 0, 2, 2));

        // Act and Assert: valid if requested 0 and the table is actually empty
        assertTrue(action.isValidSelection(0, 0, 0, 0, 0, 0));
    }

    @Test
    void isValidSelectionShouldReturnFalseIfRequestedExceedsAllowed() {
        // Act and Assert: false if requesting 3 upper cards but only 2 are allowed by the action
        assertFalse(action.isValidSelection(3, 1, 5, 5, 0, 0));

        // Act and Assert: false if requesting 2 lower cards but only 1 is allowed by the action
        assertFalse(action.isValidSelection(2, 2, 5, 5, 0, 0));
    }

    @Test
    void isValidSelectionShouldReturnFalseIfRequestedExceedsAvailable() {
        // Act and Assert: false if requesting 2 upper cards but only 1 is globally available on the table
        assertFalse(action.isValidSelection(2, 1, 1, 5, 0, 0));

        // Act and Assert: false if requesting 1 lower card but the lower table row is empty
        assertFalse(action.isValidSelection(2, 1, 5, 0, 0, 0));
    }

    @Test
    void isValidSelectionShouldReturnFalseIfMandatoryCharactersNotTaken() {
        // Act and Assert: false if requesting only 1 upper card, but there are 2 or more character cards
        // available (which are mandatory up to the action limit of 2)
        assertFalse(action.isValidSelection(1, 1, 2, 1, 0, 0));

        // Act and Assert: false if requesting 0 lower cards, but there is at least 1 mandatory character card
        assertFalse(action.isValidSelection(2, 0, 2, 1, 0, 0));
    }

    @Test
    void isValidSelectionShouldThrowExceptionForNegativeParameters() {
        // Arrange, Act and Assert: exception expected for negative parameters
        assertThrows(IllegalArgumentException.class, () -> action.isValidSelection(-1, 0, 0, 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> action.isValidSelection(0, -1, 0, 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> action.isValidSelection(0, 0, -1, 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> action.isValidSelection(0, 0, 0, -1, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> action.isValidSelection(0, 0, 0, 0, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> action.isValidSelection(0, 0, 0, 0, 0, -1));
    }
}
