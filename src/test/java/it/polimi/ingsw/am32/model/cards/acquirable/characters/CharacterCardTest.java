package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.CardVisitor;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.events.EventCard;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacterCardTest {

    //dummy character class to test the abstract charactercard class
    private static class DummyCharacterCard extends CharacterCard {
        public DummyCharacterCard(String id, int era, int basePrestige, int requiredPlayers) {
            super(id, era, basePrestige, requiredPlayers);
        }

        @Override
        public void notifyBuilding(BuildingCard building, Player player) {}

        @Override
        public CharacterType getCharacterType() {
            return CharacterType.ARTIST;
        }
    }

    private DummyCharacterCard characterCard;
    private Player player;

    @BeforeEach
    void setUp() {
        characterCard = new DummyCharacterCard("CHR_01", 1, 2, 3);
        player = new Player("Alice");
    }

    @Test
    void constructorShouldSetValuesCorrectly() {
        assertEquals("CHR_01", characterCard.getId());
        assertEquals(1, characterCard.getEra());
        assertEquals(2, characterCard.getBasePrestige());
        assertEquals(3, characterCard.getRequiredPlayers());
    }

    @Test
    void constructorShouldThrowExceptionIfRequiredPlayersIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new DummyCharacterCard("CHR_02", 1, 2, -1));
    }

    @Test
    void acceptShouldThrowExceptionIfVisitorIsNull() {
        assertThrows(NullPointerException.class, () -> characterCard.accept(null));
    }

    @Test
    void acceptShouldCallVisitor() {
        //using an array to bypass the effectively final restriction in anonymous classes
        final boolean[] wasVisited = {false};

        CardVisitor dummyVisitor = new CardVisitor() {
            @Override
            public void visit(EventCard card) {}

            @Override
            public void visit(CharacterCard card) {
                //mark that the visit method was actually called
                wasVisited[0] = true;
            }

            @Override
            public void visit(BuildingCard card) {}
        };

        //act
        characterCard.accept(dummyVisitor);

        //assert
        assertTrue(wasVisited[0], "the visitor method for character card should have been called");
    }

    @Test
    void emptyMethodsShouldNotThrowExceptions() {
        assertDoesNotThrow(() -> characterCard.onAcquire(player));
        assertDoesNotThrow(() -> characterCard.onGameEnd(player));
    }
}