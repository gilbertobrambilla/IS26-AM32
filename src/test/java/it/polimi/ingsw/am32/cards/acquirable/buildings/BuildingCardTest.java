package it.polimi.ingsw.am32.cards.acquirable.buildings;

import it.polimi.ingsw.am32.cards.acquirable.buildings.effects.endgame.PrestigePerSetEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuildingCardTest {
    private BuildingCard buildingCard;
    private BuildingCardEffect effect;
    private final int COST = 5;

    @BeforeEach
    void setUp() {
        // real effect instance created to avoid mocking
        effect = new PrestigePerSetEffect(2);

        // building card initialized with valid parameters
        buildingCard = new BuildingCard("build_01", 1, 3, COST, effect);
    }

    @Test
    void getCostShouldReturnCorrectValue() {
        // cost retrieved from the card is asserted to match the initialized value
        assertEquals(COST, buildingCard.getCost());
    }

    @Test
    void getEffectShouldReturnCorrectInstance() {
        // effect retrieved from the card is asserted to match the initialized instance
        assertEquals(effect, buildingCard.getEffect());
    }

    @Test
    void constructorShouldThrowExceptionIfCostIsNegative() {
        // illegal argument exception is expected to be thrown when cost is negative
        assertThrows(IllegalArgumentException.class, () -> new BuildingCard("build_02", 1, 3, -1, effect));
    }

    @Test
    void constructorShouldThrowExceptionIfEffectIsNull() {
        // null pointer exception is expected to be thrown when effect is null
        assertThrows(NullPointerException.class, () -> new BuildingCard("build_03", 1, 3, COST, null));
    }
}
