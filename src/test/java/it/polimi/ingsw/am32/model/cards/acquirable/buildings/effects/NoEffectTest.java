package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects;

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

class NoEffectTest {

    private Player player;
    private GameState gameState;
    private NoEffect effect;

    @BeforeEach
    void setUp() {
        // real player instance is created and initialized with some resources to avoid mocking
        player = new Player("Alice", TotemColor.RED);
        player.addFood(5);
        player.addPrestige(2);
        player.addShamanStars(1);
        player.setShamanicImmunity(true);

        // real game state is instantiated to avoid mocking
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 2, new ArrayList<>(), new ArrayList<>());
        gameState = new GameState(2, 2, new ArrayList<>(), dummyTurnOrderTile, new Deck(), new HashMap<>());

        // no effect is instantiated
        effect = new NoEffect();
    }

    @Test
    void methodsShouldNotThrowExceptionsWithNullParameters() {
        // no exceptions are expected to be thrown when methods are called with null parameters
        assertDoesNotThrow(() -> {
            effect.onAcquire(null, null);
            effect.onEvent(null, null, null);
            effect.onRoundEnd(null, null);
            effect.onTotemPlaced(null, null, null);
            effect.onGameEnd(null, null);
            effect.onCharacterAdded(null, null);
        });
    }

    @Test
    void methodsShouldNotAlterPlayerState() {
        // initial state of the player is saved
        int initialFood = player.getFood();
        int initialPrestige = player.getPrestige();
        int initialShamanStars = player.getShamanStars();
        int initialSustenanceDiscount = player.getSustenanceDiscount();
        boolean initialImmunity = player.hasShamanicImmunity();
        boolean initialDoubleBonus = player.hasDoubleShamanicBonus();

        // all methods of the interface are invoked
        effect.onAcquire(player, gameState);
        effect.onEvent(null, player, gameState);
        effect.onRoundEnd(player, gameState);
        effect.onTotemPlaced(null, player, gameState);
        effect.onGameEnd(player, gameState);
        effect.onCharacterAdded(player, null);

        // player state is expected to remain completely unchanged after invoking all effect methods
        assertEquals(initialFood, player.getFood());
        assertEquals(initialPrestige, player.getPrestige());
        assertEquals(initialShamanStars, player.getShamanStars());
        assertEquals(initialSustenanceDiscount, player.getSustenanceDiscount());
        assertEquals(initialImmunity, player.hasShamanicImmunity());
        assertEquals(initialDoubleBonus, player.hasDoubleShamanicBonus());
    }
}
