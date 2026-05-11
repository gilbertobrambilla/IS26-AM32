package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HunterCardTest {

    private HunterCard hunterCardWithMeat;
    private HunterCard hunterCardWithoutMeat;
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
        public boolean hunterAddedCalled = false;

        public DummyBuildingCard() {
            super("B_01", 1, 0, 0, new DummyEffect());
        }

        @Override
        public void onHunterAdded(Player player) {
            //record that the method was successfully called
            hunterAddedCalled = true;
        }
    }

    @BeforeEach
    void setUp() {
        //initialize two hunter cards: one with meat and one without
        hunterCardWithMeat = new HunterCard("HNT_01", 1, 1, 3, true);
        hunterCardWithoutMeat = new HunterCard("HNT_02", 1, 1, 3, false);
        //initialize a real player
        player = new Player("Dave");
    }

    @Test
    void constructorShouldSetValuesCorrectly() {
        //verify getters from superclasses and the class itself
        assertEquals("HNT_01", hunterCardWithMeat.getId());
        assertEquals(1, hunterCardWithMeat.getEra());
        assertEquals(1, hunterCardWithMeat.getBasePrestige());
        assertEquals(CharacterType.HUNTER, hunterCardWithMeat.getCharacterType());
    }

    @Test
    void onAcquireShouldThrowExceptionIfPlayerIsNull() {
        //verify exception for null player
        assertThrows(NullPointerException.class, () -> {
            hunterCardWithMeat.onAcquire(null);
        });
    }

    @Test
    void onAcquireShouldRegisterHunterStatsToPlayer() {
        //verify acquiring a hunter with meat delegates without exceptions
        assertDoesNotThrow(() -> {
            hunterCardWithMeat.onAcquire(player);
        });

        //verify acquiring a hunter without meat delegates without exceptions
        assertDoesNotThrow(() -> {
            hunterCardWithoutMeat.onAcquire(player);
        });
    }

    @Test
    void notifyBuildingShouldInvokeSpecificMethod() {
        //arrange: create a dummy building
        DummyBuildingCard building = new DummyBuildingCard();

        //act: notify the building using double dispatch
        hunterCardWithMeat.notifyBuilding(building, player);

        //assert: verify the specific method was called on the building
        assertTrue(building.hunterAddedCalled);
    }
}