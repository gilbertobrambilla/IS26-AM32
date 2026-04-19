package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.resources;

import it.polimi.ingsw.am32.model.Deck;
import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class FoodPerInventorPairEffectTest {

    private Player player;
    private GameState gameState;
    private FoodPerInventorPairEffect effect;
    private final int EXTRA_FOOD = 2;

    @BeforeEach
    void setUp() {
        // real player instance is created
        player = new Player("Alice", TotemColor.RED);

        // real game state is instantiated
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 2, new ArrayList<>(), new ArrayList<>());
        gameState = new GameState(2, 2, new ArrayList<>(), dummyTurnOrderTile, new Deck<>(), new HashMap<>());

        // food per inventor pair effect is instantiated with a valid extra food bonus
        effect = new FoodPerInventorPairEffect(EXTRA_FOOD);
    }

    @Test
    void constructorShouldThrowExceptionIfExtraFoodIsNegative() {
        // illegal argument exception is expected when extra food is negative
        assertThrows(IllegalArgumentException.class, () -> new FoodPerInventorPairEffect(-1));
    }

    @Test
    void onAcquireShouldThrowExceptionIfPlayerIsNull() {
        // null pointer exception is expected when player is null
        assertThrows(NullPointerException.class, () -> effect.onAcquire(null, gameState));
    }

    @Test
    void onAcquireShouldThrowExceptionIfGameStateIsNull() {
        // null pointer exception is expected when game state is null
        assertThrows(NullPointerException.class, () -> effect.onAcquire(player, null));
    }

    @Test
    void onAcquireShouldAddZeroFoodIfPlayerHasNoInventors() {
        // initial food count is recorded
        int initialFood = player.getFood();

        // effect is triggered on acquire
        effect.onAcquire(player, gameState);

        // zero food is added when the player has no inventors
        assertEquals(initialFood, player.getFood());
    }

    @Test
    void onAcquireShouldAddZeroFoodIfPlayerHasOnlyOneInventor() {
        // exactly one inventor is added to the player
        player.addInventor();

        // initial food count is recorded
        int initialFood = player.getFood();

        // effect is triggered on acquire
        effect.onAcquire(player, gameState);

        // zero food is added when the player has an odd number of inventors less than two
        assertEquals(initialFood, player.getFood());
    }

    @Test
    void onAcquireShouldAddCorrectFoodForSinglePair() {
        // three inventors are added to the player
        player.addInventor();
        player.addInventor();
        player.addInventor();

        // initial food count is recorded
        int initialFood = player.getFood();

        // effect is triggered on acquire
        effect.onAcquire(player, gameState);

        // correct amount of food is added for a single pair of inventors
        assertEquals(initialFood + EXTRA_FOOD, player.getFood());
    }

    @Test
    void onAcquireShouldAddCorrectFoodForMultiplePairs() {
        // four inventors are added to the player
        player.addInventor();
        player.addInventor();
        player.addInventor();
        player.addInventor();

        // initial food count is recorded
        int initialFood = player.getFood();

        // effect is triggered on acquire
        effect.onAcquire(player, gameState);

        // correct amount of food is added for multiple pairs of inventors
        assertEquals(initialFood + (EXTRA_FOOD * 2), player.getFood());
    }

    @Test
    void onAcquireShouldAddZeroFoodIfExtraFoodIsZero() {
        // effect is instantiated with a zero food bonus
        FoodPerInventorPairEffect zeroEffect = new FoodPerInventorPairEffect(0);

        // two inventors are added to the player
        player.addInventor();
        player.addInventor();

        // initial food count is recorded
        int initialFood = player.getFood();

        // effect is triggered on acquire
        zeroEffect.onAcquire(player, gameState);

        // zero food is added when extra food is configured to zero
        assertEquals(initialFood, player.getFood());
    }

    @Test
    void emptyMethodsShouldNotAlterState() {
        // initial state is recorded
        int initialFood = player.getFood();
        int initialPrestige = player.getPrestige();

        // empty interface methods are invoked
        effect.onEvent(null, player, gameState);
        effect.onRoundEnd(player, gameState);
        effect.onTotemPlaced(null, player, gameState);
        effect.onGameEnd(player, gameState);
        effect.onCharacterAdded(player, null);

        // player state is verified to remain unchanged
        assertEquals(initialFood, player.getFood());
        assertEquals(initialPrestige, player.getPrestige());
    }
}
