package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.status;

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

class ExtraShamanStarsEffectTest {

    private Player player;
    private GameState gameState;
    private ExtraShamanStarsEffect effect;
    private final int EXTRA_STARS = 3;

    @BeforeEach
    void setUp() {
        // real player instance is created
        player = new Player("Alice", TotemColor.RED);

        // real game state is instantiated
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 2, new ArrayList<>(), new ArrayList<>());
        gameState = new GameState(2, 2, new ArrayList<>(), dummyTurnOrderTile, new Deck<>(), new HashMap<>());

        // extra shaman stars effect is instantiated with a valid positive bonus
        effect = new ExtraShamanStarsEffect(EXTRA_STARS);
    }

    @Test
    void constructorShouldThrowExceptionIfExtraStarsIsNegative() {
        // illegal argument exception is expected when extra stars is negative
        assertThrows(IllegalArgumentException.class, () -> new ExtraShamanStarsEffect(-1));
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
    void onAcquireShouldAddCorrectShamanStars() {
        // initial shaman stars count is modified and recorded
        player.addShamanStars(2);
        int initialStars = player.getShamanStars();

        // effect is triggered on acquire
        effect.onAcquire(player, gameState);

        // correct amount of shaman stars is added to the player upon acquisition
        assertEquals(initialStars + EXTRA_STARS, player.getShamanStars());
    }

    @Test
    void onAcquireShouldAddZeroStarsIfExtraStarsIsZero() {
        // effect is instantiated with a zero stars bonus
        ExtraShamanStarsEffect zeroEffect = new ExtraShamanStarsEffect(0);

        // initial shaman stars count is modified and recorded
        player.addShamanStars(2);
        int initialStars = player.getShamanStars();

        // effect is triggered on acquire
        zeroEffect.onAcquire(player, gameState);

        // zero shaman stars are added when extra stars is configured to zero
        assertEquals(initialStars, player.getShamanStars());
    }

    @Test
    void emptyMethodsShouldNotAlterState() {
        // initial state is recorded
        int initialFood = player.getFood();
        int initialPrestige = player.getPrestige();
        int initialShamanStars = player.getShamanStars();

        // empty interface methods are invoked
        effect.onEvent(null, player, gameState);
        effect.onRoundEnd(player, gameState);
        effect.onTotemPlaced(null, player, gameState);
        effect.onGameEnd(player, gameState);
        effect.onCharacterAdded(player, null);

        // player state is verified to remain unchanged
        assertEquals(initialFood, player.getFood());
        assertEquals(initialPrestige, player.getPrestige());
        assertEquals(initialShamanStars, player.getShamanStars());
    }
}
