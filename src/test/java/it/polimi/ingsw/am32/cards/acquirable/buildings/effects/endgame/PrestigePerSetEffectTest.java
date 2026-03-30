package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.endgame;

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

class PrestigePerSetEffectTest {

    private Player player;
    private GameState gameState;
    private PrestigePerSetEffect effect;
    private final int POINTS_PER_SET = 6;

    @BeforeEach
    void setUp() {
        // real player instance is created to avoid mocking
        player = new Player("Alice", TotemColor.RED);

        // real game state is instantiated to avoid mocking
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 2, new ArrayList<>(), new ArrayList<>());
        gameState = new GameState(2, 2, new ArrayList<>(), dummyTurnOrderTile, new Deck<>(), new HashMap<>());

        // prestige per set effect is instantiated with valid parameters
        effect = new PrestigePerSetEffect(POINTS_PER_SET);
    }

    @Test
    void constructorShouldThrowExceptionIfPointsPerSetIsNegative() {
        // illegal argument exception is expected to be thrown when points per set is negative
        assertThrows(IllegalArgumentException.class, () -> new PrestigePerSetEffect(-1));
    }

    @Test
    void onGameEndShouldThrowExceptionIfPlayerIsNull() {
        // null pointer exception is expected to be thrown when player is null
        assertThrows(NullPointerException.class, () -> effect.onGameEnd(null, gameState));
    }

    @Test
    void onGameEndShouldThrowExceptionIfGameStateIsNull() {
        // null pointer exception is expected to be thrown when game state is null
        assertThrows(NullPointerException.class, () -> effect.onGameEnd(player, null));
    }

    @Test
    void onGameEndShouldAddZeroPrestigeIfSetIsIncomplete() {
        // a partial set of 5 out of 6 characters is added to the player
        player.addArtist();
        player.addBuilder();
        player.addGatherer();
        player.addHunter();
        player.addInventor();

        // effect is triggered at the end of the game
        effect.onGameEnd(player, gameState);

        // zero prestige is expected to be added since at least one character type is missing
        assertEquals(0, player.getPrestige());
    }

    @Test
    void onGameEndShouldAddCorrectPrestigeForSingleCompleteSet() {
        // exactly one character of each type is added
        player.addArtist();
        player.addBuilder();
        player.addGatherer();
        player.addHunter();
        player.addInventor();
        player.addShaman();

        // effect is triggered at the end of the game
        effect.onGameEnd(player, gameState);

        // correct amount of prestige is expected to be added for a single complete set
        assertEquals(POINTS_PER_SET, player.getPrestige());
    }

    @Test
    void onGameEndShouldAddCorrectPrestigeForMultipleCompleteSets() {
        // unbalanced quantities of characters are added to test the limiting character algorithm
        for (int i = 0; i < 4; i++) { player.addArtist(); }
        for (int i = 0; i < 5; i++) { player.addBuilder(); }
        for (int i = 0; i < 3; i++) { player.addGatherer(); }
        for (int i = 0; i < 2; i++) { player.addHunter(); }
        for (int i = 0; i < 3; i++) { player.addInventor(); }
        for (int i = 0; i < 2; i++) { player.addShaman(); }

        // effect is triggered at the end of the game
        effect.onGameEnd(player, gameState);

        // correct amount of prestige is expected to be added based on the bottleneck of 2 sets
        assertEquals(POINTS_PER_SET * 2, player.getPrestige());
    }

    @Test
    void onGameEndShouldAddZeroPrestigeIfPointsPerSetIsZero() {
        // effect is instantiated with zero points per set
        PrestigePerSetEffect zeroEffect = new PrestigePerSetEffect(0);

        // exactly one character of each type is added
        player.addArtist();
        player.addBuilder();
        player.addGatherer();
        player.addHunter();
        player.addInventor();
        player.addShaman();

        // effect is triggered at the end of the game
        zeroEffect.onGameEnd(player, gameState);

        // zero prestige is expected to be added when the points per set is zero
        assertEquals(0, player.getPrestige());
    }

    @Test
    void emptyMethodsShouldNotAlterPlayerState() {
        // initial state of the player is recorded to ensure no side effects occur
        int initialPrestige = player.getPrestige();

        // all empty interface methods are invoked to achieve full line and method coverage
        effect.onAcquire(player, gameState);
        effect.onEvent(null, player, gameState);
        effect.onRoundEnd(player, gameState);
        effect.onTotemPlaced(null, player, gameState);
        effect.onCharacterAdded(player, null);

        // player prestige is expected to remain unchanged after invoking empty methods
        assertEquals(initialPrestige, player.getPrestige());
    }
}
