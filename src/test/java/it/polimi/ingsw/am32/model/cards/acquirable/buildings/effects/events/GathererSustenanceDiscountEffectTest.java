package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.events;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.Card;
import it.polimi.ingsw.am32.model.Deck;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GathererSustenanceDiscountEffectTest {

    private GathererSustenanceDiscountEffect effect;
    private Player player;
    private GameState dummyGameState;

    // dummy game state to bypass complex initialization in pure java
    private static class DummyGameState extends GameState {
        public DummyGameState(TurnOrderTile dummyTile, Deck<Card> dummyDeck) {
            super(1, 2, new ArrayList<>(), dummyTile, dummyDeck, new HashMap<>());
        }
    }

    /**
     * Helper method using Java Reflection to read the private 'sustenanceDiscount'
     * field from the Player class.
     */
    private int getHiddenSustenanceDiscount(Player p) throws Exception {
        Field discountField = Player.class.getDeclaredField("sustenanceDiscount");
        discountField.setAccessible(true);
        return (int) discountField.get(p);
    }

    @BeforeEach
    void setUp() {
        // initialize the effect with a discount of 3 per gatherer
        effect = new GathererSustenanceDiscountEffect(3);
        player = new Player("Liam");

        // instantiate dependencies for gamestate
        List<Integer> foodMods = List.of(0, 0, 0, 0, 0);
        List<Integer> prestigeMods = List.of(0, 0, 0, 0, 0);
        TurnOrderTile dummyTile = new TurnOrderTile("TOT_01", 5, foodMods, prestigeMods);
        Deck<Card> dummyDeck = new Deck<>();

        dummyGameState = new DummyGameState(dummyTile, dummyDeck);
    }

    @Test
    void constructorShouldThrowExceptionIfDiscountIsNegative() {
        // verify exception for negative discount values
        assertThrows(IllegalArgumentException.class, () -> new GathererSustenanceDiscountEffect(-1));
    }

    @Test
    void constructorShouldAllowZeroDiscount() {
        // verify that an effect granting 0 discount is mathematically valid
        assertDoesNotThrow(() -> new GathererSustenanceDiscountEffect(0));
    }

    @Test
    void onAcquireShouldThrowExceptionIfPlayerIsNull() {
        assertThrows(NullPointerException.class, () -> effect.onAcquire(null, dummyGameState));
    }

    @Test
    void onAcquireShouldThrowExceptionIfGameStateIsNull() {
        assertThrows(NullPointerException.class, () -> effect.onAcquire(player, null));
    }

    @Test
    void onAcquireShouldApplyRetroactiveDiscountForExistingGatherers() throws Exception {
        // arrange: simulate the player already having 2 gatherers before acquiring the building
        player.addGatherer();
        player.addGatherer();

        // act: apply the acquire effect
        effect.onAcquire(player, dummyGameState);

        // assert: 2 gatherers * 3 discount each = 6 total discount
        int actualDiscount = getHiddenSustenanceDiscount(player);
        assertEquals(6, actualDiscount, "Retroactive discount for gatherers was not calculated correctly");
    }

    @Test
    void onGathererAddedShouldThrowExceptionIfPlayerIsNull() {
        assertThrows(NullPointerException.class, () -> effect.onGathererAdded(null));
    }

    @Test
    void onGathererAddedShouldIncreaseSustenanceDiscount() throws Exception {
        // act: trigger the effect as if a new gatherer was just added
        effect.onGathererAdded(player);

        // assert: the player should have received a discount increase of 3
        int actualDiscount = getHiddenSustenanceDiscount(player);
        assertEquals(3, actualDiscount, "Discount was not applied when gatherer was added");
    }

    @Test
    void emptyMethodsShouldNotThrowExceptions() {
        assertDoesNotThrow(() -> effect.onRoundEnd(player, dummyGameState));
        assertDoesNotThrow(() -> effect.onGameEnd(player, dummyGameState));
    }
}