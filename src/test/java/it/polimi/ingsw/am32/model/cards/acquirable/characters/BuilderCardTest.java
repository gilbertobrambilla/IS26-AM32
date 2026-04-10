package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuilderCardTest {

    private Player player;
    private BuilderCard builderCard;
    private final int DISCOUNT_VALUE = 2;

    @BeforeEach
    void setUp() {
        player = new Player("TestNickname", TotemColor.RED);
        builderCard = new BuilderCard("BuilderId", 1, 3, 2, DISCOUNT_VALUE);
    }

    @Test
    void getCharacterTypeShouldReturnBuilder() {
        assertEquals(CharacterType.BUILDER, builderCard.getCharacterType());
    }

    @Test
    void onAcquireShouldIncrementPlayerBuilderCountAndDiscount() {
        int initialBuilders = player.getBuilderCount();
        int initialDiscount = player.getBuildingDiscount();

        builderCard.onAcquire(player);

        assertEquals(initialBuilders + 1, player.getBuilderCount());
        assertEquals(initialDiscount + DISCOUNT_VALUE, player.getBuildingDiscount());
    }

    @Test
    void constructorShouldThrowExceptionIfBuildingDiscountIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new BuilderCard("BuilderId", 1, 3, 2, -1));
    }

    @Test
    void onAcquireShouldThrowExceptionIfPlayerIsNull() {
        assertThrows(NullPointerException.class, () -> builderCard.onAcquire(null));
    }

}