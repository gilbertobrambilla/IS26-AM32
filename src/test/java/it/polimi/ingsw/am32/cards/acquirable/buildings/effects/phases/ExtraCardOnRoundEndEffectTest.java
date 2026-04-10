package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.phases;

import it.polimi.ingsw.am32.Deck;
import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.enums.GamePhase;
import it.polimi.ingsw.am32.enums.TotemColor;
import it.polimi.ingsw.am32.tiles.TurnOrderTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ExtraCardOnRoundEndEffectTest {

    private Player player;
    private GameState gameState;
    private ExtraCardOnRoundEndEffect effect;

    @BeforeEach
    void setUp() {
        // real player instance is created
        player = new Player("Alice", TotemColor.RED);

        // real game state is instantiated
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 2, new ArrayList<>(), new ArrayList<>());
        gameState = new GameState(2, 2, new ArrayList<>(), dummyTurnOrderTile, new Deck<>(), new HashMap<>());

        // extra card on round end effect is instantiated
        effect = new ExtraCardOnRoundEndEffect();
    }

    @Test
    void onRoundEndShouldThrowExceptionIfPlayerIsNull() {
        // null pointer exception is expected when player is null
        assertThrows(NullPointerException.class, () -> effect.onRoundEnd(null, gameState));
    }

    @Test
    void onRoundEndShouldThrowExceptionIfGameStateIsNull() {
        // null pointer exception is expected when game state is null
        assertThrows(NullPointerException.class, () -> effect.onRoundEnd(player, null));
    }

    @Test
    void onRoundEndShouldUpdateGamePhaseAndCurrentPlayer() {
        // effect is triggered at the end of the round
        effect.onRoundEnd(player, gameState);

        // game phase is set to extra acquisition and current player is updated
        assertEquals(GamePhase.EXTRA_ACQUISITION, gameState.getCurrentPhase());
        assertEquals(player, gameState.getCurrentPlayer());
    }

    @Test
    void emptyMethodsShouldNotAlterState() {
        // initial state is recorded
        GamePhase initialPhase = gameState.getCurrentPhase();
        Player initialPlayer = gameState.getCurrentPlayer();
        int initialFood = player.getFood();
        int initialPrestige = player.getPrestige();

        // empty interface methods are invoked
        effect.onAcquire(player, gameState);
        effect.onEvent(null, player, gameState);
        effect.onTotemPlaced(null, player, gameState);
        effect.onGameEnd(player, gameState);
        effect.onCharacterAdded(player, null);

        // player and game state are verified to remain unchanged
        assertEquals(initialPhase, gameState.getCurrentPhase());
        assertEquals(initialPlayer, gameState.getCurrentPlayer());
        assertEquals(initialFood, player.getFood());
        assertEquals(initialPrestige, player.getPrestige());
    }
}
