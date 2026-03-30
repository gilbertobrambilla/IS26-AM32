package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.phases;

import it.polimi.ingsw.am32.Deck;
import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.enums.TotemColor;
import it.polimi.ingsw.am32.tiles.Action;
import it.polimi.ingsw.am32.tiles.OfferTile;
import it.polimi.ingsw.am32.tiles.TurnOrderTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ExtraFoodOnTotemEffectTest {

    private Player player;
    private GameState gameState;
    private OfferTile offerTile;
    private ExtraFoodOnTotemEffect effect;
    private final int EXTRA_FOOD = 2;

    @BeforeEach
    void setUp() {
        // real player instance is created
        player = new Player("Alice", TotemColor.RED);

        // real game state is instantiated
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 2, new ArrayList<>(), new ArrayList<>());
        gameState = new GameState(2, 2, new ArrayList<>(), dummyTurnOrderTile, new Deck<>(), new HashMap<>());

        // offer tile is instantiated using inferred parameters
        Action dummyAction = new Action(0, 0, 3);
        offerTile = new OfferTile("offertile_A", 5, dummyAction);

        // extra food on totem effect is instantiated with a valid positive bonus
        effect = new ExtraFoodOnTotemEffect(EXTRA_FOOD);
    }

    @Test
    void constructorShouldThrowExceptionIfExtraFoodIsNegative() {
        // illegal argument exception is expected when extra food is negative
        assertThrows(IllegalArgumentException.class, () -> new ExtraFoodOnTotemEffect(-1));
    }

    @Test
    void onTotemPlacedShouldThrowExceptionIfOfferTileIsNull() {
        // null pointer exception is expected when offer tile is null
        assertThrows(NullPointerException.class, () -> effect.onTotemPlaced(null, player, gameState));
    }

    @Test
    void onTotemPlacedShouldThrowExceptionIfPlayerIsNull() {
        // null pointer exception is expected when player is null
        assertThrows(NullPointerException.class, () -> effect.onTotemPlaced(offerTile, null, gameState));
    }

    @Test
    void onTotemPlacedShouldThrowExceptionIfGameStateIsNull() {
        // null pointer exception is expected when game state is null
        assertThrows(NullPointerException.class, () -> effect.onTotemPlaced(offerTile, player, null));
    }

    @Test
    void onTotemPlacedShouldAddCorrectFoodAmount() {
        // initial food count is recorded
        int initialFood = player.getFood();

        // effect is triggered by placing a totem
        effect.onTotemPlaced(offerTile, player, gameState);

        // correct amount of food is added to the player when a totem is placed
        assertEquals(initialFood + EXTRA_FOOD, player.getFood());
    }

    @Test
    void onTotemPlacedShouldAddCorrectFoodAmountForMultipleTriggers() {
        // initial food count is recorded
        int initialFood = player.getFood();

        // effect is triggered twice
        effect.onTotemPlaced(offerTile, player, gameState);
        effect.onTotemPlaced(offerTile, player, gameState);

        // correct amount of food is accumulated for multiple triggers
        assertEquals(initialFood + (EXTRA_FOOD * 2), player.getFood());
    }

    @Test
    void onTotemPlacedShouldAddZeroFoodIfExtraFoodIsZero() {
        // effect is instantiated with a zero food bonus
        ExtraFoodOnTotemEffect zeroEffect = new ExtraFoodOnTotemEffect(0);

        // initial food count is recorded
        int initialFood = player.getFood();

        // effect is triggered
        zeroEffect.onTotemPlaced(offerTile, player, gameState);

        // zero food is added when extra food is configured to zero
        assertEquals(initialFood, player.getFood());
    }

    @Test
    void emptyMethodsShouldNotAlterState() {
        // initial state is recorded
        int initialFood = player.getFood();
        int initialPrestige = player.getPrestige();

        // empty interface methods are invoked
        effect.onAcquire(player, gameState);
        effect.onEvent(null, player, gameState);
        effect.onRoundEnd(player, gameState);
        effect.onGameEnd(player, gameState);
        effect.onCharacterAdded(player, null);

        // player state is verified to remain unchanged
        assertEquals(initialFood, player.getFood());
        assertEquals(initialPrestige, player.getPrestige());
    }
}
