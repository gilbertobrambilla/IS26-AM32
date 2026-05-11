package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.resources;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.Card;
import it.polimi.ingsw.am32.model.Deck;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FoodPerInventorPairEffectTest {

    private FoodPerInventorPairEffect effect;
    private Player player;
    private GameState dummyGameState;

    // dummy game state to bypass complex initialization in pure java
    private static class DummyGameState extends GameState {
        public DummyGameState(TurnOrderTile dummyTile, Deck<Card> dummyDeck) {
            // pass valid dummy data to satisfy gamestate's strict constructor requirements
            super(1, 2, new ArrayList<>(), dummyTile, dummyDeck, new HashMap<>());
        }
    }

    @BeforeEach
    void setUp() {
        // initialize the effect with 3 extra food for testing
        effect = new FoodPerInventorPairEffect(3);
        player = new Player("Grace");

        // initialize valid modifier lists for the turn order tile
        List<Integer> foodMods = List.of(0, 0, 0, 0, 0);
        List<Integer> prestigeMods = List.of(0, 0, 0, 0, 0);

        // instantiate real dependencies with basic valid data
        TurnOrderTile dummyTile = new TurnOrderTile("TOT_01", 5, foodMods, prestigeMods);
        Deck<Card> dummyDeck = new Deck<>();

        // initialize the dummy gamestate to satisfy empty method signatures
        dummyGameState = new DummyGameState(dummyTile, dummyDeck);
    }

    @Test
    void constructorShouldThrowExceptionIfExtraFoodIsNegative() {
        // verify exception for negative food values
        assertThrows(IllegalArgumentException.class, () -> new FoodPerInventorPairEffect(-1));
    }

    @Test
    void constructorShouldAllowZeroExtraFood() {
        // verify that an effect granting 0 food is mathematically valid
        assertDoesNotThrow(() -> new FoodPerInventorPairEffect(0));
    }

    @Test
    void onInventorPairCompletedShouldThrowExceptionIfPlayerIsNull() {
        // verify exception is thrown when player is null
        assertThrows(NullPointerException.class, () -> effect.onInventorPairCompleted(null));
    }

    @Test
    void onInventorPairCompletedShouldGrantFoodToPlayer() {
        // record the initial amount of food the player has
        int initialFood = player.getFood();

        // act: trigger the inventor pair completion effect
        effect.onInventorPairCompleted(player);

        // assert: verify the new total is exactly initial + 3
        assertEquals(initialFood + 3, player.getFood());
    }

    @Test
    void emptyMethodsShouldNotThrowExceptions() {
        // verify that empty interface methods can be safely called without side effects
        assertDoesNotThrow(() -> effect.onAcquire(player, dummyGameState));
        assertDoesNotThrow(() -> effect.onRoundEnd(player, dummyGameState));
        assertDoesNotThrow(() -> effect.onGameEnd(player, dummyGameState));
    }
}