package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GathererCardTest {

    private Player player;
    private GathererCard gathererCard;

    @BeforeEach
    void setUp() {
        player = new Player("TestNickname", TotemColor.RED);
        gathererCard = new GathererCard("GathererId", 1, 0, 2, 3);
    }

    @Test
    void getCharacterTypeShouldReturnGatherer() {
        assertEquals(CharacterType.GATHERER, gathererCard.getCharacterType());
    }

    @Test
    void onAcquireShouldIncrementPlayerGathererCountAndDiscount() {
        int initialGatherers = player.getGathererCount();
        int initialDiscount = player.getSustenanceDiscount();

        gathererCard.onAcquire(player);

        assertEquals(initialGatherers + 1, player.getGathererCount());
        assertEquals(initialDiscount + 3, player.getSustenanceDiscount());
    }

    @Test
    void constructorShouldThrowExceptionIfSustenanceDiscountIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new GathererCard("GathererId", 1, 0, 2, -1));
    }

    @Test
    void onAcquireShouldThrowExceptionIfPlayerIsNull() {
        assertThrows(NullPointerException.class, () -> gathererCard.onAcquire(null));
    }
}