package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuilderCardTest {

    private BuilderCard builderCard;
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
        public boolean builderAddedCalled = false;

        public DummyBuildingCard() {
            super("B_01", 1, 0, 0, new DummyEffect());
        }

        @Override
        public void onBuilderAdded(Player player) {
            //record that the method was successfully called
            builderAddedCalled = true;
        }
    }

    @BeforeEach
    void setUp() {
        //initialize a builder card before each test (base prestige 2, building discount 1)
        builderCard = new BuilderCard("BLD_01", 1, 2, 3, 1);
        //initialize a real player
        player = new Player("Bob");
    }

    @Test
    void constructorShouldSetValuesCorrectly() {
        //verify getters from superclasses and the class itself
        assertEquals("BLD_01", builderCard.getId());
        assertEquals(1, builderCard.getEra());
        assertEquals(2, builderCard.getBasePrestige());
        assertEquals(CharacterType.BUILDER, builderCard.getCharacterType());
    }

    @Test
    void constructorShouldThrowExceptionIfBuildingDiscountIsNegative() {
        //verify exception for negative building discount
        assertThrows(IllegalArgumentException.class, () -> {
            new BuilderCard("BLD_02", 1, 2, 3, -1);
        });
    }

    @Test
    void onAcquireShouldThrowExceptionIfPlayerIsNull() {
        //verify exception for null player
        assertThrows(NullPointerException.class, () -> {
            builderCard.onAcquire(null);
        });
    }

    @Test
    void onAcquireShouldRegisterBuilderStatsToPlayer() {
        //verify that acquiring delegates correctly to the player without exceptions
        assertDoesNotThrow(() -> {
            builderCard.onAcquire(player);
        });
    }

    @Test
    void onGameEndShouldThrowExceptionIfPlayerIsNull() {
        //verify exception for null player
        assertThrows(NullPointerException.class, () -> {
            builderCard.onGameEnd(null);
        });
    }

    @Test
    void onGameEndShouldAddBasePrestigeToPlayer() {
        //arrange: ensure player starts with 0 prestige
        assertEquals(0, player.getPrestige());

        //act: trigger game end scoring for the card
        builderCard.onGameEnd(player);

        //assert: prestige should increase by base prestige (which is 2)
        assertEquals(2, player.getPrestige());
    }

    @Test
    void notifyBuildingShouldInvokeSpecificMethod() {
        //arrange: create a dummy building
        DummyBuildingCard building = new DummyBuildingCard();

        //act: notify the building using double dispatch
        builderCard.notifyBuilding(building, player);

        //assert: verify the specific method was called on the building
        assertTrue(building.builderAddedCalled);
    }
}