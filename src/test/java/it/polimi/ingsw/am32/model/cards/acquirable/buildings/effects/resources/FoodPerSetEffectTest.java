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

class FoodPerSetEffectTest {

    private Player player;
    private GameState gameState;
    private FoodPerSetEffect effect;
    private final int EXTRA_FOOD = 3;

    @BeforeEach
    void setUp() {
        // real player instance is created
        player = new Player("Alice", TotemColor.RED);

        // real game state is instantiated
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 2, new ArrayList<>(), new ArrayList<>());
        gameState = new GameState(2, 2, new ArrayList<>(), dummyTurnOrderTile, new Deck<>(), new HashMap<>());

        // food per set effect is instantiated with valid extra food bonus
        effect = new FoodPerSetEffect(EXTRA_FOOD);
    }

    @Test
    void constructorShouldThrowExceptionIfExtraFoodIsNegative() {
        // illegal argument exception is expected when extra food is negative
        assertThrows(IllegalArgumentException.class, () -> new FoodPerSetEffect(-1));
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
    void onAcquireShouldAddZeroFoodIfSetIsIncomplete() {
        // a partial set of characters is added to the player
        player.addArtist();
        player.addBuilder();
        player.addGatherer();
        player.addHunter();
        player.addInventor();

        // initial food count is recorded
        int initialFood = player.getFood();

        // effect is triggered on acquire
        effect.onAcquire(player, gameState);

        // zero food is added when the player has an incomplete set
        assertEquals(initialFood, player.getFood());
    }

    @Test
    void onAcquireShouldAddCorrectFoodForSingleCompleteSet() {
        // exactly one character of each type is added
        player.addArtist();
        player.addBuilder();
        player.addGatherer();
        player.addHunter();
        player.addInventor();
        player.addShaman();

        // initial food count is recorded
        int initialFood = player.getFood();

        // effect is triggered on acquire
        effect.onAcquire(player, gameState);

        // correct amount of food is added for a single complete set
        assertEquals(initialFood + EXTRA_FOOD, player.getFood());
    }

    @Test
    void onAcquireShouldAddCorrectFoodForMultipleCompleteSets() {
        // unbalanced quantities of characters are added
        for (int i = 0; i < 4; i++) { player.addArtist(); }
        for (int i = 0; i < 2; i++) { player.addBuilder(); }
        for (int i = 0; i < 3; i++) { player.addGatherer(); }
        for (int i = 0; i < 5; i++) { player.addHunter(); }
        for (int i = 0; i < 3; i++) { player.addInventor(); }
        for (int i = 0; i < 2; i++) { player.addShaman(); }

        // initial food count is recorded
        int initialFood = player.getFood();

        // effect is triggered on acquire
        effect.onAcquire(player, gameState);

        // correct amount of food is added for multiple complete sets
        assertEquals(initialFood + (EXTRA_FOOD * 2), player.getFood());
    }

    @Test
    void onAcquireShouldAddZeroFoodIfExtraFoodIsZero() {
        // effect is instantiated with a zero food bonus
        FoodPerSetEffect zeroEffect = new FoodPerSetEffect(0);

        // exactly one character of each type is added
        player.addArtist();
        player.addBuilder();
        player.addGatherer();
        player.addHunter();
        player.addInventor();
        player.addShaman();

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
