package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArtistCardTest {

    private ArtistCard artistCard;
    private Player player;

    //dummy effect to satisfy building card constructor
    private static class DummyEffect implements BuildingCardEffect {
        @Override public void onAcquire(Player p, GameState g) {}
        @Override public void onArtistAdded(Player p) {}
        @Override public void onBuilderAdded(Player p) {}
        @Override public void onGathererAdded(Player p) {}
        @Override public void onHunterAdded(Player p) {}
        @Override public void onInventorAdded(Player p) {}
        @Override public void onShamanAdded(Player p) {}
        @Override public void onRoundEnd(Player p, GameState g) {}
        @Override public void onTurnOrderPlaced(Player p, boolean gainedFood) {}
        @Override public void onSetCompleted(Player p) {}
        @Override public void onInventorPairCompleted(Player p) {}
        @Override public void onHuntEvent(Player p, GameState g) {}
        @Override public void onSustenanceEvent(Player p, GameState g) {}
        @Override public void onCavePaintingsEvent(Player p, GameState g) {}
        @Override public void onShamanicRitualEvent(Player p, GameState g) {}
        @Override public void onGameEnd(Player p, GameState g) {}
    }

    //dummy building card to test the notification pattern
    private static class DummyBuildingCard extends BuildingCard {
        public boolean artistAddedCalled = false;

        public DummyBuildingCard() {
            super("B_01", 1, 0, 0, new DummyEffect());
        }

        @Override
        public void onArtistAdded(Player player) {
            //record that the method was successfully called
            artistAddedCalled = true;
        }
    }

    @BeforeEach
    void setUp() {
        //initialize an artist card before each test (base prestige 2, prestige per pair 3)
        artistCard = new ArtistCard("ART_01", 1, 2, 3, 3);
        //initialize a real player
        player = new Player("Alice");
    }

    @Test
    void constructorShouldSetValuesCorrectly() {
        //verify getters from superclasses and the class itself
        assertEquals("ART_01", artistCard.getId());
        assertEquals(1, artistCard.getEra());
        assertEquals(2, artistCard.getBasePrestige());
        assertEquals(CharacterType.ARTIST, artistCard.getCharacterType());
    }

    @Test
    void constructorShouldThrowExceptionIfPrestigePerPairIsNegative() {
        //verify exception for negative prestige per pair
        assertThrows(IllegalArgumentException.class, () -> {
            new ArtistCard("ART_02", 1, 2, 3, -1);
        });
    }

    @Test
    void onAcquireShouldThrowExceptionIfPlayerIsNull() {
        //verify exception for null player
        assertThrows(NullPointerException.class, () -> {
            artistCard.onAcquire(null);
        });
    }

    @Test
    void onAcquireShouldAddArtistToPlayer() {
        //verify that acquiring does not throw exceptions and delegates correctly
        assertDoesNotThrow(() -> {
            artistCard.onAcquire(player);
        });
    }

    @Test
    void onGameEndShouldThrowExceptionIfPlayerIsNull() {
        //verify exception for null player
        assertThrows(NullPointerException.class, () -> {
            artistCard.onGameEnd(null);
        });
    }

    @Test
    void onGameEndShouldScoreArtistPairs() {
        //arrange: player starts with 0 prestige
        assertEquals(0, player.getPrestige());

        //act: score the first artist (should not give points alone)
        artistCard.onGameEnd(player);

        //assert: prestige is still 0
        assertEquals(0, player.getPrestige());

        //act: score a second artist to complete the pair
        artistCard.onGameEnd(player);

        //assert: prestige should increase by prestigePerPair (which is 3)
        assertEquals(3, player.getPrestige());
    }

    @Test
    void notifyBuildingShouldInvokeSpecificMethod() {
        //arrange: create a dummy building
        DummyBuildingCard building = new DummyBuildingCard();

        //act: notify the building using double dispatch
        artistCard.notifyBuilding(building, player);

        //assert: verify the specific method was called on the building
        assertTrue(building.artistAddedCalled);
    }
}