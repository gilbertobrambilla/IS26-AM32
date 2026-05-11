package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import it.polimi.ingsw.am32.model.enums.InventionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventorCardTest {

    private InventorCard inventorCard;
    private Player player;
    private InventionType testInvention;

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
        public boolean inventorAddedCalled = false;

        public DummyBuildingCard() {
            super("B_01", 1, 0, 0, new DummyEffect());
        }

        @Override
        public void onInventorAdded(Player player) {
            //record that the method was successfully called
            inventorAddedCalled = true;
        }
    }

    @BeforeEach
    void setUp() {
        //pick the first available invention type from the enum to ensure validity
        testInvention = InventionType.values()[0];

        //initialize an inventor card before each test
        inventorCard = new InventorCard("INV_01", 1, 1, 3, testInvention);
        //initialize a real player
        player = new Player("Eve");
    }

    @Test
    void constructorShouldSetValuesCorrectly() {
        //verify getters from superclasses and the class itself
        assertEquals("INV_01", inventorCard.getId());
        assertEquals(1, inventorCard.getEra());
        assertEquals(1, inventorCard.getBasePrestige());
        assertEquals(CharacterType.INVENTOR, inventorCard.getCharacterType());
    }

    @Test
    void constructorShouldThrowExceptionIfInventionIsNull() {
        //verify exception for null invention
        assertThrows(NullPointerException.class, () -> {
            new InventorCard("INV_02", 1, 1, 3, null);
        });
    }

    @Test
    void onAcquireShouldThrowExceptionIfPlayerIsNull() {
        //verify exception for null player
        assertThrows(NullPointerException.class, () -> {
            inventorCard.onAcquire(null);
        });
    }

    @Test
    void onAcquireShouldRegisterInventorStatsToPlayer() {
        //verify that acquiring delegates correctly to the player without exceptions
        assertDoesNotThrow(() -> {
            inventorCard.onAcquire(player);
        });
    }

    @Test
    void onGameEndShouldThrowExceptionIfPlayerIsNull() {
        //verify exception for null player
        assertThrows(NullPointerException.class, () -> {
            inventorCard.onGameEnd(null);
        });
    }

    @Test
    void onGameEndShouldScoreInventorUniqueInventions() {
        //arrange: player acquires the inventor, gaining 1 unique invention type
        inventorCard.onAcquire(player);
        assertEquals(0, player.getPrestige());

        //act: trigger game end scoring for the card
        inventorCard.onGameEnd(player);

        //assert: prestige should increase by 1 for possessing one unique invention
        assertEquals(1, player.getPrestige());
    }

    @Test
    void notifyBuildingShouldInvokeSpecificMethod() {
        //arrange: create a dummy building
        DummyBuildingCard building = new DummyBuildingCard();

        //act: notify the building using double dispatch
        inventorCard.notifyBuilding(building, player);

        //assert: verify the specific method was called on the building
        assertTrue(building.inventorAddedCalled);
    }
}