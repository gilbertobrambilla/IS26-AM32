package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShamanCardTest {

    private ShamanCard shamanCard;
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
        public boolean shamanAddedCalled = false;

        public DummyBuildingCard() {
            super("B_01", 1, 0, 0, new DummyEffect());
        }

        @Override
        public void onShamanAdded(Player player) {
            //record that the method was successfully called
            shamanAddedCalled = true;
        }
    }

    @BeforeEach
    void setUp() {
        //initialize a shaman card before each test (base prestige 1, shaman stars 2)
        shamanCard = new ShamanCard("SHA_01", 1, 1, 3, 2);
        //initialize a real player
        player = new Player("Frank");
    }

    @Test
    void constructorShouldSetValuesCorrectly() {
        //verify getters from superclasses and the class itself
        assertEquals("SHA_01", shamanCard.getId());
        assertEquals(1, shamanCard.getEra());
        assertEquals(1, shamanCard.getBasePrestige());
        assertEquals(CharacterType.SHAMAN, shamanCard.getCharacterType());
    }

    @Test
    void constructorShouldThrowExceptionIfShamanStarsIsLessThanOne() {
        //verify exception for zero shaman stars
        assertThrows(IllegalArgumentException.class, () -> {
            new ShamanCard("SHA_02", 1, 1, 3, 0);
        });

        //verify exception for negative shaman stars
        assertThrows(IllegalArgumentException.class, () -> {
            new ShamanCard("SHA_03", 1, 1, 3, -1);
        });
    }

    @Test
    void constructorShouldThrowExceptionIfShamanStarsIsGreaterThanThree() {
        //verify exception for more than three shaman stars
        assertThrows(IllegalArgumentException.class, () -> {
            new ShamanCard("SHA_04", 1, 1, 3, 4);
        });
    }

    @Test
    void onAcquireShouldThrowExceptionIfPlayerIsNull() {
        //verify exception for null player
        assertThrows(NullPointerException.class, () -> {
            shamanCard.onAcquire(null);
        });
    }

    @Test
    void onAcquireShouldRegisterShamanStatsToPlayer() {
        //verify that acquiring delegates correctly to the player without exceptions
        assertDoesNotThrow(() -> {
            shamanCard.onAcquire(player);
        });
    }

    @Test
    void notifyBuildingShouldInvokeSpecificMethod() {
        //arrange: create a dummy building
        DummyBuildingCard building = new DummyBuildingCard();

        //act: notify the building using double dispatch
        shamanCard.notifyBuilding(building, player);

        //assert: verify the specific method was called on the building
        assertTrue(building.shamanAddedCalled);
    }
}