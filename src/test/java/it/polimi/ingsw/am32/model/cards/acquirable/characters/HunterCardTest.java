package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HunterCardTest {
    private Player player;
    private HunterCard hunterCardFood;
    private HunterCard hunterCardNoFood;

    @BeforeEach
    void setUp() {
        player = new Player("TestNickname", TotemColor.RED);
        hunterCardFood = new HunterCard("HunterId", 1, 0, 2, true);
        hunterCardNoFood = new HunterCard("HunterId", 1, 0, 2, false);
    }

    @Test
    void getCharacterTypeShouldReturnHunter() {
        assertEquals(CharacterType.HUNTER, hunterCardFood.getCharacterType());
    }

    @Test
    void onAcquireShouldIncrementPlayerHunterCountAndFood() {
        int initialHunters = player.getHunterCount();
        int initialFood = player.getFood();

        for(int i = 0; i < 5; i++){
            hunterCardNoFood.onAcquire(player);
        }

        hunterCardFood.onAcquire(player);

        assertEquals(initialHunters + 6, player.getHunterCount());
        assertEquals(initialFood + 6, player.getFood());
    }

    @Test
    void onAcquireShouldIncrementPlayerHunterCountAndNoFood() {
        int initialHunters = player.getHunterCount();
        int initialFood = player.getFood();

        hunterCardNoFood.onAcquire(player);

        assertEquals(initialHunters + 1, player.getHunterCount());
        assertEquals(initialFood, player.getFood());
    }

    @Test
    void onAcquireShouldThrowExceptionIfPlayerIsNull() {
        assertThrows(NullPointerException.class, () -> hunterCardFood.onAcquire(null));
    }

}