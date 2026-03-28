package it.polimi.ingsw.am32.cards.acquirable.characters;

import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.enums.CharacterType;
import it.polimi.ingsw.am32.enums.InventionType;
import it.polimi.ingsw.am32.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventorCardTest {

    private Player player;
    private InventorCard inventorCard;

    @BeforeEach
    void setUp() {
        player = new Player("TestNickname", TotemColor.RED);
        inventorCard = new InventorCard("InventorId", 1, 0, 2, InventionType.ARROWHEAD);
    }

    @Test
    void getCharacterTypeShouldReturnInventor() {
        assertEquals(CharacterType.INVENTOR, inventorCard.getCharacterType());
    }

    @Test
    void onAcquireShouldIncrementPlayerInventorAndInvention() {
        int initialInventors = player.getInventorCount();
        int initialInventions = player.getInventionCount();

        inventorCard.onAcquire(player);

        assertEquals(initialInventors + 1, player.getInventorCount());
        assertEquals(initialInventions + 1, player.getInventionCount());
    }

    @Test
    void constructorShouldThrowExceptionIfInventionTypeIsNull() {
        assertThrows(NullPointerException.class, () -> new InventorCard("InventorId", 1, 0, 2, null));
    }

    @Test
    void onAcquireShouldThrowExceptionIfPlayerIsNull() {
        assertThrows(NullPointerException.class, () -> inventorCard.onAcquire(null));
    }

}