package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArtistCardTest {

    private Player player;
    private ArtistCard artistCard;

    @BeforeEach
    void setUp() {
        player = new Player("TestNickname", TotemColor.RED);
        artistCard = new ArtistCard("ArtistId", 1, 0, 2, 10);
    }

    @Test
    void getCharacterTypeShouldReturnArtist() {
        assertEquals(CharacterType.ARTIST, artistCard.getCharacterType());
    }

    @Test
    void onAcquireShouldIncrementPlayerArtistCount() {
        int initialArtists = player.getArtistCount();

        artistCard.onAcquire(player);

        assertEquals(initialArtists + 1, player.getArtistCount());
    }

    @Test
    void constructorShouldThrowExceptionIfPrestigePerPairIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new ArtistCard("ArtistId", 1, 0, 2, -1));
    }

    @Test
    void onAcquireShouldThrowExceptionIfPlayerIsNull() {
        assertThrows(NullPointerException.class, () -> artistCard.onAcquire(null));
    }
}