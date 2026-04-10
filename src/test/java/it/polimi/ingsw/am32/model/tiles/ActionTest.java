package it.polimi.ingsw.am32.model.tiles;

import it.polimi.ingsw.am32.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ActionTest {
    private Action action;
    private OfferTile offerTile;
    private Player player;
    private int requestedUpper;
    private int requestedLower;
    private int availableUpper;
    private int availableLower;
    private boolean isValidSelection;
    private int drawsFormUpperRow;
    private int drawsFormLowerRow;
    private int foodBonus;

    @BeforeEach
    void setUp() {
        action = new Action(1,1,1);
        isValidSelection = true;
        requestedUpper = 1;
        requestedLower = 1;
        availableUpper = 1;
        availableLower = 1;
        drawsFormUpperRow = 1;
        drawsFormLowerRow = 1;
        foodBonus = 1;
        offerTile = new OfferTile("OfferTIleId",2,action);
    }
    @Test
    void constructorShouldThrowExceptionWhenDrawsFromUpperRowAreNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Action(-1, -1, -1));
    }
    @Test
    void constructorShouldThrowExceptionWhenDrawsFromLowerRowAreNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Action(1, -1, -1));
    }
    @Test
    void constructorShouldThrowExceptionWhenFoodBonusIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Action(1, 1, -1));
    }
    @Test
    void isValisdSelectionShouldThrowExceptionWhenRequestedUpperRowIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {action.isValidSelection(-1,1,1,1);});
    }
    @Test
    void isValisdSelectionShouldThrowExceptionWhenRequestedLowerRowIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {action.isValidSelection(1,-1,1,1);});
    }
    @Test
    void isValisdSelectionShouldThrowExceptionWhenAvailableUpperRowIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {action.isValidSelection(1,1,-1,1);});
    }
    @Test
    void isValisdSelectionShouldThrowExceptionWhenAvailableLowerRowIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {action.isValidSelection(1,1,1,-1);});
    }
    @Test
    void isValidSelectionShouldReturnTrueWhenIsValidSelection() {
        assertTrue(action.isValidSelection(requestedUpper,requestedLower,availableUpper,availableLower));
    }
    @Test
    void isValidSelectionShouldReturnFalseIfIsUpperValid() {
        availableUpper = 0;
        assertFalse(action.isValidSelection(requestedUpper, requestedLower, availableUpper, availableLower));
    }
    @Test
    void isValidSelectionShouldReturnFalseIfIsLowerValid() {
        availableLower = 0;
        assertFalse(action.isValidSelection(requestedUpper, requestedLower, availableUpper, availableLower));
    }
    //manca da testare penso la questione in cui quei booleani ritornino true?
    @Test
    void isValidSelectionShouldReturnIsUpperValidAndIsLowerValid() {
        action.isValidSelection(requestedUpper,requestedLower,availableUpper,availableLower);
        assertTrue(action.isValidSelection(requestedUpper,requestedLower,availableUpper,availableLower));
    }
    @Test
    void getDrawsFormUpperRowShouldReturnDrawsFormUpperRow() {
        assertEquals(1, action.getDrawsFromUpperRow());
    }
    @Test
    void getDrawsFormLowerRowShouldReturnDrawsFormLowerRow() {
        assertEquals(1, action.getDrawsFromLowerRow());
    }
    @Test
    void getFoodBonusShouldReturnFoodBonus() {
        assertEquals(1, action.getFoodBonus());
    }


}