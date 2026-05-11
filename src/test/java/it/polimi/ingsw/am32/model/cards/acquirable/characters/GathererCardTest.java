package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GathererCardTest {

    private GathererCard gathererCard;
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
        public boolean gathererAddedCalled = false;

        public DummyBuildingCard() {
            super("B_01", 1, 0, 0, new DummyEffect());
        }

        @Override
        public void onGathererAdded(Player player) {
            //record that the method was successfully called
            gathererAddedCalled = true;
        }
    }

    @BeforeEach
    void setUp() {
        //initialize a gatherer card before each test (base prestige 1, sustenance discount 2)
        gathererCard = new GathererCard("GAT_01", 1, 1, 3, 2);
        //initialize a real player
        player = new Player("Charlie");
    }

    @Test
    void constructorShouldSetValuesCorrectly() {
        //verify getters from superclasses and the class itself
        assertEquals("GAT_01", gathererCard.getId());
        assertEquals(1, gathererCard.getEra());
        assertEquals(1, gathererCard.getBasePrestige());
        assertEquals(CharacterType.GATHERER, gathererCard.getCharacterType());
    }

    @Test
    void constructorShouldThrowExceptionIfSustenanceDiscountIsNegative() {
        //verify exception for negative sustenance discount
        assertThrows(IllegalArgumentException.class, () -> {
            new GathererCard("GAT_02", 1, 1, 3, -1);
        });
    }

    @Test
    void onAcquireShouldThrowExceptionIfPlayerIsNull() {
        //verify exception for null player
        assertThrows(NullPointerException.class, () -> {
            gathererCard.onAcquire(null);
        });
    }

    @Test
    void onAcquireShouldRegisterGathererStatsToPlayer() {
        //verify that acquiring delegates correctly to the player without exceptions
        assertDoesNotThrow(() -> {
            gathererCard.onAcquire(player);
        });
    }

    @Test
    void notifyBuildingShouldInvokeSpecificMethod() {
        //arrange: create a dummy building
        DummyBuildingCard building = new DummyBuildingCard();

        //act: notify the building using double dispatch
        gathererCard.notifyBuilding(building, player);

        //assert: verify the specific method was called on the building
        assertTrue(building.gathererAddedCalled);
    }
}