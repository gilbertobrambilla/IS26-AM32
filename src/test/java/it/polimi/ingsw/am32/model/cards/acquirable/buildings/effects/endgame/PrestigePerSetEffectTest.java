package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.endgame;

import it.polimi.ingsw.am32.model.Deck;
import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class PrestigePerSetEffectTest {

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
        // initialize the effect with valid points per set
        PrestigePerSetEffect effect = new PrestigePerSetEffect(3);

        // verify that the object is successfully created
        assertNotNull(effect);
    }

    @Test
    void constructorThrowsExceptionWhenPointsAreNegative() {
        // expect an illegal argument exception when points per set are negative
        assertThrows(IllegalArgumentException.class, () -> new PrestigePerSetEffect(-1));
    }

    @Test
    void onGameEndAppliesCorrectPrestigeCalculation() {
        // add one of each character to the player to simulate exactly one completed set
        player.addArtist();
        player.addBuilder();
        player.addGatherer();
        player.addHunter(false);
        player.addInventor();
        player.addShaman();

        // create the effect awarding 5 points per set
        PrestigePerSetEffect effect = new PrestigePerSetEffect(5);

        // trigger the endgame effect
        effect.onGameEnd(player, gameState);

        // verify the prestige math: 1 completed set * 5 points = 5 extra prestige points
        assertEquals(5, player.getPrestige());
    }

    @Test
    void onGameEndThrowsExceptionIfPlayerIsNull() {
        // create a valid effect
        PrestigePerSetEffect effect = new PrestigePerSetEffect(3);

        // verify that passing a null player throws a null pointer exception
        assertThrows(NullPointerException.class, () -> effect.onGameEnd(null, gameState));
    }

    @Test
    void onGameEndThrowsExceptionIfGameStateIsNull() {
        // create a valid effect
        PrestigePerSetEffect effect = new PrestigePerSetEffect(3);

        // verify that passing a null game state throws a null pointer exception
        assertThrows(NullPointerException.class, () -> effect.onGameEnd(player, null));
    }

    @Test
    void onAcquireDoesNothingAndThrowsNoExceptions() {
        // create a valid effect
        PrestigePerSetEffect effect = new PrestigePerSetEffect(3);

        // verify that calling the empty method does not throw any exception
        assertDoesNotThrow(() -> effect.onAcquire(player, gameState));
    }

    @Test
    void onRoundEndDoesNothingAndThrowsNoExceptions() {
        // create a valid effect
        PrestigePerSetEffect effect = new PrestigePerSetEffect(3);

        // verify that calling the empty method does not throw any exception
        assertDoesNotThrow(() -> effect.onRoundEnd(player, gameState));
    }
}