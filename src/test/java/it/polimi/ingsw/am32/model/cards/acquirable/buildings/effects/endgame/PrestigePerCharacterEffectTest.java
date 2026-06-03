package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.endgame;

import it.polimi.ingsw.am32.model.Deck;
import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class PrestigePerCharacterEffectTest {

    private Player player;
    private GameState gameState;

    @BeforeEach
    void setUp() {
        // instantiate a real player for testing state changes
        player = new Player("TestPlayer");

        // create a dummy turn order tile to satisfy game state constructor requirements
        TurnOrderTile dummyTile = new TurnOrderTile("dummy_tile", 2, Arrays.asList(0, 0), Arrays.asList(0, 0));

        // instantiate a minimal dummy game state to avoid null pointer exceptions
        gameState = new GameState(1, 2, new ArrayList<>(), dummyTile, new Deck<>(), new HashMap<>());
    }

    @Test
    void constructorCreatesObjectWithValidParameters() {
        // initialize the effect with a valid character type and positive points
        PrestigePerCharacterEffect effect = new PrestigePerCharacterEffect(CharacterType.ARTIST, 2);

        // verify that the object is successfully created
        assertNotNull(effect);
    }

    @Test
    void constructorThrowsExceptionWhenTargetTypeIsNull() {
        // expect a null pointer exception when character type is null
        assertThrows(NullPointerException.class, () -> new PrestigePerCharacterEffect(null, 2));
    }

    @Test
    void constructorThrowsExceptionWhenPointsAreNegative() {
        // expect an illegal argument exception when points per character are negative
        assertThrows(IllegalArgumentException.class, () -> new PrestigePerCharacterEffect(CharacterType.ARTIST, -1));
    }

    @Test
    void onGameEndAppliesCorrectPrestigeCalculation() {
        // add three artist characters to the player
        player.addArtist();
        player.addArtist();
        player.addArtist();

        // create the effect targeting artists with 2 points each
        PrestigePerCharacterEffect effect = new PrestigePerCharacterEffect(CharacterType.ARTIST, 2);

        // trigger the endgame effect
        effect.onGameEnd(player, gameState);

        // verify the prestige math: 3 artists * 2 points = 6 extra prestige points
        assertEquals(6, player.getPrestige());
    }

    @Test
    void onGameEndThrowsExceptionIfPlayerIsNull() {
        // create a valid effect
        PrestigePerCharacterEffect effect = new PrestigePerCharacterEffect(CharacterType.ARTIST, 2);

        // verify that passing a null player throws a null pointer exception
        assertThrows(NullPointerException.class, () -> effect.onGameEnd(null, gameState));
    }

    @Test
    void onGameEndThrowsExceptionIfGameStateIsNull() {
        // create a valid effect
        PrestigePerCharacterEffect effect = new PrestigePerCharacterEffect(CharacterType.ARTIST, 2);

        // verify that passing a null game state throws a null pointer exception
        assertThrows(NullPointerException.class, () -> effect.onGameEnd(player, null));
    }

    @Test
    void onAcquireDoesNothingAndThrowsNoExceptions() {
        // create a valid effect
        PrestigePerCharacterEffect effect = new PrestigePerCharacterEffect(CharacterType.ARTIST, 2);

        // verify that calling the empty method does not throw any exception
        assertDoesNotThrow(() -> effect.onAcquire(player, gameState));
    }

    @Test
    void onRoundEndDoesNothingAndThrowsNoExceptions() {
        // create a valid effect
        PrestigePerCharacterEffect effect = new PrestigePerCharacterEffect(CharacterType.ARTIST, 2);

        // verify that calling the empty method does not throw any exception
        assertDoesNotThrow(() -> effect.onRoundEnd(player, gameState));
    }
}