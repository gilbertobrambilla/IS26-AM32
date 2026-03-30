package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.status;

import it.polimi.ingsw.am32.Deck;
import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.enums.TotemColor;
import it.polimi.ingsw.am32.tiles.TurnOrderTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ShamanicDoubleRewardEffectTest {

    private Player player;
    private GameState gameState;
    private ShamanicDoubleRewardEffect effect;

    @BeforeEach
    void setUp() {
        // real player instance is created
        player = new Player("Alice", TotemColor.RED);

        // real game state is instantiated
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 2, new ArrayList<>(), new ArrayList<>());
        gameState = new GameState(2, 2, new ArrayList<>(), dummyTurnOrderTile, new Deck<>(), new HashMap<>());

        // shamanic double reward effect is instantiated
        effect = new ShamanicDoubleRewardEffect();
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
    void onAcquireShouldEnableDoubleShamanicBonus() {
        // double shamanic bonus flag is set to true upon acquisition
        effect.onAcquire(player, gameState);

        assertTrue(player.hasDoubleShamanicBonus());
    }

    @Test
    void onAcquireShouldMaintainBonusIfAlreadyEnabled() {
        // double shamanic bonus flag remains true if already enabled before acquisition
        effect.onAcquire(player, gameState);
        effect.onAcquire(player, gameState);

        assertTrue(player.hasDoubleShamanicBonus());
    }

    @Test
    void onAcquireShouldNotAlterOtherPlayerResources() {
        // player resources such as stars and prestige remain unchanged
        int initialStars = player.getShamanStars();
        int initialPrestige = player.getPrestige();

        effect.onAcquire(player, gameState);

        assertEquals(initialStars, player.getShamanStars());
        assertEquals(initialPrestige, player.getPrestige());
    }

    @Test
    void emptyMethodsShouldNotAlterState() {
        // initial state is recorded
        boolean initialDoubleBonus = player.hasDoubleShamanicBonus();
        int initialFood = player.getFood();
        int initialPrestige = player.getPrestige();

        // empty interface methods are invoked
        effect.onEvent(null, player, gameState);
        effect.onRoundEnd(player, gameState);
        effect.onTotemPlaced(null, player, gameState);
        effect.onGameEnd(player, gameState);
        effect.onCharacterAdded(player, null);

        // player state is verified to remain unchanged
        assertEquals(initialDoubleBonus, player.hasDoubleShamanicBonus());
        assertEquals(initialFood, player.getFood());
        assertEquals(initialPrestige, player.getPrestige());
    }
}
