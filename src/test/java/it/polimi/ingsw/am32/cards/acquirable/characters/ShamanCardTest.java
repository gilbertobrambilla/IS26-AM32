package it.polimi.ingsw.am32.cards.acquirable.characters;

import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.enums.CharacterType;
import it.polimi.ingsw.am32.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShamanCardTest {

    private Player player;
    private ShamanCard shamanCard;
    private final int SHAMAN_STARS_VALUE = 1;

    @BeforeEach
    void setUp() {
        player = new Player("TestNickname", TotemColor.RED);
        shamanCard = new ShamanCard("ShamanId", 1, 0, 2, SHAMAN_STARS_VALUE);
    }

    @Test
    void getCharacterTypeShouldReturnShaman() {
        assertEquals(CharacterType.SHAMAN, shamanCard.getCharacterType());
    }

    @Test
    void onAcquireShouldIncrementPlayerShamanCountAndShamanStars() {
        int initialShamans = player.getShamanCount();
        int initialStars = player.getShamanStars();

        shamanCard.onAcquire(player);

        assertEquals(initialShamans + 1, player.getShamanCount());
        assertEquals(initialStars + SHAMAN_STARS_VALUE, player.getShamanStars());
    }

    @Test
    void constructorShouldThrowExceptionIfShamanStarsIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new ShamanCard("ShamanId", 1, 0, 2, 0));
        assertThrows(IllegalArgumentException.class, () -> new ShamanCard("ShamanId", 1, 0, 2, 4));
    }

    @Test
    void onAcquireShouldThrowExceptionIfPlayerIsNull() {
        assertThrows(NullPointerException.class, () -> shamanCard.onAcquire(null));
    }
}