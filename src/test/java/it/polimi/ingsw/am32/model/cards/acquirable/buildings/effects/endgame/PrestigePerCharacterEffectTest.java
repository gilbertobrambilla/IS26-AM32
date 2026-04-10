package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.endgame;

import it.polimi.ingsw.am32.model.Deck;
import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class PrestigePerCharacterEffectTest {

    private Player player;
    private GameState gameState;
    private PrestigePerCharacterEffect effect;
    private final int POINTS_PER_CHARACTER = 2;
    private final CharacterType TARGET_TYPE = CharacterType.SHAMAN;

    @BeforeEach
    void setUp() {
        // real player instance is created to avoid mocking
        player = new Player("Alice", TotemColor.RED);

        // real game state is instantiated to avoid mocking
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 2, new ArrayList<>(), new ArrayList<>());
        gameState = new GameState(2, 2, new ArrayList<>(), dummyTurnOrderTile, new Deck<>(), new HashMap<>());

        // prestige per character effect is instantiated with valid parameters
        effect = new PrestigePerCharacterEffect(TARGET_TYPE, POINTS_PER_CHARACTER);
    }

    @Test
    void constructorShouldThrowExceptionIfTargetTypeIsNull() {
        // null pointer exception is expected to be thrown when target type is null
        assertThrows(NullPointerException.class, () -> new PrestigePerCharacterEffect(null, POINTS_PER_CHARACTER));
    }

    @Test
    void constructorShouldThrowExceptionIfPointsPerCharacterIsNegative() {
        // illegal argument exception is expected to be thrown when points per character is negative
        assertThrows(IllegalArgumentException.class, () -> new PrestigePerCharacterEffect(TARGET_TYPE, -1));
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
    void onGameEndShouldAddCorrectPrestigeBasedOnTargetCharacterCount() {
        // target characters are added to the player to provide base count
        player.addShaman();
        player.addShaman();
        player.addShaman();

        // effect is triggered at the end of the game
        effect.onGameEnd(player, gameState);

        // correct amount of prestige is expected to be added to the player based on the target character count
        // 3 shamans multiplied by 2 points each should result in 6 extra prestige
        assertEquals(6, player.getPrestige());
    }

    @Test
    void onGameEndShouldAddZeroPrestigeIfPlayerHasOnlyDifferentCharacterTypes() {
        // different character types are added to the player to ensure they are not counted
        player.addArtist();
        player.addBuilder();
        player.addHunter();

        // effect is triggered at the end of the game
        effect.onGameEnd(player, gameState);

        // zero prestige is expected to be added when the player has no characters of the target type
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

    @Test
    void onGameEndShouldCoverAllCharacterTypes() {
        // player is populated with characters to test each branch of the target character calculation
        player.addArtist();
        player.addBuilder();
        player.addGatherer();
        player.addHunter();
        player.addInventor();

        // initial prestige is recorded
        int initialPrestige = player.getPrestige();

        // effects for all remaining character types are instantiated and triggered
        new PrestigePerCharacterEffect(CharacterType.ARTIST, 1).onGameEnd(player, gameState);
        new PrestigePerCharacterEffect(CharacterType.BUILDER, 1).onGameEnd(player, gameState);
        new PrestigePerCharacterEffect(CharacterType.GATHERER, 1).onGameEnd(player, gameState);
        new PrestigePerCharacterEffect(CharacterType.HUNTER, 1).onGameEnd(player, gameState);
        new PrestigePerCharacterEffect(CharacterType.INVENTOR, 1).onGameEnd(player, gameState);

        // 5 total prestige is expected to be added (1 point for each character type tested)
        assertEquals(initialPrestige + 5, player.getPrestige());
    }
}
