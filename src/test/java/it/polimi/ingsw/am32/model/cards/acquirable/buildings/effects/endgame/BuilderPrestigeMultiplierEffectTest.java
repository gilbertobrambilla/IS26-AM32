package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.endgame;

import it.polimi.ingsw.am32.model.Deck;
import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.BuilderCard;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BuilderPrestigeMultiplierEffectTest {
    private Player player;
    private GameState gameState;
    private BuilderPrestigeMultiplierEffect effect;
    private final int MULTIPLIER = 3;

    @BeforeEach
    void setUp() {
        // real player instance is created to avoid mocking
        player = new Player("Alice", TotemColor.RED);

        // real game state is instantiated to avoid mocking
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 2, new ArrayList<>(), new ArrayList<>());
        gameState = new GameState(2, 2, new ArrayList<>(), dummyTurnOrderTile, new Deck<>(), new HashMap<>());

        // builder prestige multiplier effect is instantiated with a valid multiplier
        effect = new BuilderPrestigeMultiplierEffect(MULTIPLIER);
    }

    @Test
    void constructorShouldThrowExceptionIfMultiplierIsLessThanOne() {
        // illegal argument exception is expected to be thrown when multiplier is less than one
        assertThrows(IllegalArgumentException.class, () -> new BuilderPrestigeMultiplierEffect(0));
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
    void onGameEndShouldAddCorrectPrestigeBasedOnMultiplier() {
        // real builder cards are added to the player to provide base prestige
        BuilderCard builder1 = new BuilderCard("b1", 1, 2, 0, 1);
        BuilderCard builder2 = new BuilderCard("b2", 1, 3, 0, 1);
        player.addCharacter(builder1);
        player.addCharacter(builder2);

        // effect is triggered at the end of the game
        effect.onGameEnd(player, gameState);

        // correct amount of prestige is expected to be added to the player based on the multiplier
        // base prestige is 5, multiplier is 3, extra prestige should be 5 * (3 - 1) = 10
        assertEquals(10, player.getPrestige());
    }

    @Test
    void onGameEndShouldAddZeroPrestigeIfMultiplierIsOne() {
        // effect is instantiated with a multiplier of exactly one
        BuilderPrestigeMultiplierEffect effectWithOne = new BuilderPrestigeMultiplierEffect(1);

        // real builder cards are added to the player to provide base prestige
        BuilderCard builder1 = new BuilderCard("b1", 1, 2, 0, 1);
        player.addCharacter(builder1);

        // effect is triggered at the end of the game
        effectWithOne.onGameEnd(player, gameState);

        // zero prestige is expected to be added when the multiplier is exactly one
        assertEquals(0, player.getPrestige());
    }

    @Test
    void onGameEndShouldAddZeroPrestigeIfPlayerHasNoBuilders() {
        // effect is triggered on a player with no builder cards assigned
        effect.onGameEnd(player, gameState);

        // zero prestige is expected to be added when the player has no builder cards
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
