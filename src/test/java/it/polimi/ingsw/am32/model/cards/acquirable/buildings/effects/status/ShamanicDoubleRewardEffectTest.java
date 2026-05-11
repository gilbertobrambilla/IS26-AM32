package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.status;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.Card;
import it.polimi.ingsw.am32.model.Deck;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShamanicDoubleRewardEffectTest {

    private ShamanicDoubleRewardEffect effect;
    private Player player;
    private GameState dummyGameState;

    // dummy game state to bypass complex initialization in pure java
    private static class DummyGameState extends GameState {
        public DummyGameState(TurnOrderTile dummyTile, Deck<Card> dummyDeck) {
            // pass valid dummy data to satisfy gamestate's strict constructor requirements
            super(1, 2, new ArrayList<>(), dummyTile, dummyDeck, new HashMap<>());
        }
    }

    @BeforeEach
    void setUp() {
        // initialize the effect and testing objects
        effect = new ShamanicDoubleRewardEffect();
        player = new Player("Dave");

        // initialize valid modifier lists for the turn order tile
        List<Integer> foodMods = List.of(0, 0, 0, 0, 0);
        List<Integer> prestigeMods = List.of(0, 0, 0, 0, 0);

        // instantiate real dependencies with basic valid data
        TurnOrderTile dummyTile = new TurnOrderTile("TOT_01", 5, foodMods, prestigeMods);
        Deck<Card> dummyDeck = new Deck<>();

        // initialize the dummy gamestate
        dummyGameState = new DummyGameState(dummyTile, dummyDeck);
    }

    @Test
    void onAcquireShouldThrowExceptionIfPlayerIsNull() {
        // verify exception is thrown when player is null
        assertThrows(NullPointerException.class, () -> effect.onAcquire(null, dummyGameState));
    }

    @Test
    void onAcquireShouldThrowExceptionIfGameStateIsNull() {
        // verify exception is thrown when gamestate is null
        assertThrows(NullPointerException.class, () -> effect.onAcquire(player, null));
    }

    @Test
    void onAcquireShouldSetDoubleShamanicBonusWithoutExceptions() {
        // verify the method executes and delegates to player without throwing errors
        assertDoesNotThrow(() -> effect.onAcquire(player, dummyGameState));
    }

    @Test
    void emptyMethodsShouldNotThrowExceptions() {
        // verify that empty interface methods can be safely called without side effects
        assertDoesNotThrow(() -> effect.onRoundEnd(player, dummyGameState));
        assertDoesNotThrow(() -> effect.onGameEnd(player, dummyGameState));
    }
}