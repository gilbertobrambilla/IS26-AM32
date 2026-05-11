package it.polimi.ingsw.am32.model.tiles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ActionTest {

    private Action action;

    @BeforeEach
    void setUp() {
        //creo una action standard prima di ogni test (può pescare max 2 sopra, max 1 sotto, dà 3 cibo)
        action = new Action(2, 1, 3);
    }

    @Test
    void constructorShouldSetValuesCorrectly() {
        //act and assert: verifico che i getter restituiscano i valori inseriti nel setup
        assertEquals(2, action.getDrawsFromUpperRow());
        assertEquals(1, action.getDrawsFromLowerRow());
        assertEquals(3, action.getFoodBonus());
    }

    @Test
    void constructorShouldThrowExceptionIfDrawsFromUpperRowIsNegative() {
        //arrange, act and assert: eccezione se la pescata superiore è negativa
        assertThrows(IllegalArgumentException.class, () -> {
            new Action(-1, 1, 3);
        });
    }

    @Test
    void constructorShouldThrowExceptionIfDrawsFromLowerRowIsNegative() {
        //arrange, act and assert: eccezione se la pescata inferiore è negativa
        assertThrows(IllegalArgumentException.class, () -> {
            new Action(2, -1, 3);
        });
    }

    @Test
    void constructorShouldThrowExceptionIfFoodBonusIsNegative() {
        //arrange, act and assert: eccezione se il bonus cibo è negativo
        assertThrows(IllegalArgumentException.class, () -> {
            new Action(2, 1, -3);
        });
    }

    @Test
    void isValidSelectionShouldReturnTrueForValidRequests() {
        //act and assert: valido se chiedo esattamente il massimo consentito e disponibile
        assertTrue(action.isValidSelection(2, 1, 5, 5));

        //act and assert: valido se chiedo meno del massimo consentito
        assertTrue(action.isValidSelection(1, 0, 2, 2));

        //act and assert: valido se chiedo 0 (se permesso dal gioco)
        assertTrue(action.isValidSelection(0, 0, 0, 0));
    }

    @Test
    void isValidSelectionShouldReturnFalseIfRequestedExceedsAllowed() {
        //act and assert: falso se chiedo 3 carte sopra ma ne sono permesse solo 2
        assertFalse(action.isValidSelection(3, 1, 5, 5));

        //act and assert: falso se chiedo 2 carte sotto ma ne è permessa solo 1
        assertFalse(action.isValidSelection(2, 2, 5, 5));
    }

    @Test
    void isValidSelectionShouldReturnFalseIfRequestedExceedsAvailable() {
        //act and assert: falso se chiedo 2 carte sopra ma ce n'è solo 1 disponibile sul tavolo
        assertFalse(action.isValidSelection(2, 1, 1, 5));

        //act and assert: falso se chiedo 1 carta sotto ma il tavolo è vuoto sotto
        assertFalse(action.isValidSelection(2, 1, 5, 0));
    }

    @Test
    void isValidSelectionShouldThrowExceptionForNegativeParameters() {
        //arrange, act and assert: eccezione per requestedUpper negativo
        assertThrows(IllegalArgumentException.class, () -> {
            action.isValidSelection(-1, 0, 0, 0);
        });

        //arrange, act and assert: eccezione per requestedLower negativo
        assertThrows(IllegalArgumentException.class, () -> {
            action.isValidSelection(0, -1, 0, 0);
        });

        //arrange, act and assert: eccezione per availableUpper negativo
        assertThrows(IllegalArgumentException.class, () -> {
            action.isValidSelection(0, 0, -1, 0);
        });

        //arrange, act and assert: eccezione per availableLower negativo
        assertThrows(IllegalArgumentException.class, () -> {
            action.isValidSelection(0, 0, 0, -1);
        });
    }
}