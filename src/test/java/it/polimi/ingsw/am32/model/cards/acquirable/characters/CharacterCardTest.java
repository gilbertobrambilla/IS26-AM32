package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacterCardTest {

    private static class AlienCharacterCard extends CharacterCard {
        public AlienCharacterCard(String id, int era, int basePrestige, int requiredPlayers) {
            super(id, era, basePrestige, requiredPlayers);
        }

        @Override
        public CharacterType getCharacterType() {
            return null;
        }
    }

    private AlienCharacterCard characterCard;

    @BeforeEach
    void setUp() {
        characterCard = new AlienCharacterCard("AlienID", 1, 0, 4);
    }

    @Test
    void getRequiredPlayersShouldReturnConstructorValue() {
        assertEquals(4, characterCard.getRequiredPlayers());
    }

    @Test
    void defaultMethodsShouldNotThrowExceptions() {
        Player player = new Player("TestNickname", TotemColor.RED);
        assertDoesNotThrow(() -> characterCard.onAcquire(player));
        assertDoesNotThrow(() -> characterCard.onGameEnd(player));
    }

    @Test
    void constructorShouldThrowExceptionIfRequiredPlayersIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new AlienCharacterCard("AlienID", 1, 0, -1));
    }

    @Test
    void constructorShouldThrowExceptionIfEraIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new AlienCharacterCard("AlienID", 0, 0, 4));
        assertThrows(IllegalArgumentException.class, () -> new AlienCharacterCard("AlienID", 4, 0, 4));
    }

}