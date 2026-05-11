package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.events;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.Card;
import it.polimi.ingsw.am32.model.Deck;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExtraResourcesOnCavePaintingsEffectTest {

    private ExtraResourcesOnCavePaintingsEffect effect;
    private Player player;
    private GameState dummyGameState;

    // dummy game state to bypass complex initialization in pure java
    private static class DummyGameState extends GameState {
        public DummyGameState(TurnOrderTile dummyTile, Deck<Card> dummyDeck) {
            super(1, 2, new ArrayList<>(), dummyTile, dummyDeck, new HashMap<>());
        }
    }

    @BeforeEach
    void setUp() {
        // initialize the effect targeting builders: grants 2 extra food and 3 extra prestige per builder
        effect = new ExtraResourcesOnCavePaintingsEffect(CharacterType.BUILDER, 2, 3);
        player = new Player("Irene");

        // instantiate real dependencies with basic valid data for gamestate
        List<Integer> foodMods = List.of(0, 0, 0, 0, 0);
        List<Integer> prestigeMods = List.of(0, 0, 0, 0, 0);
        TurnOrderTile dummyTile = new TurnOrderTile("TOT_01", 5, foodMods, prestigeMods);
        Deck<Card> dummyDeck = new Deck<>();

        dummyGameState = new DummyGameState(dummyTile, dummyDeck);
    }

    @Test
    void constructorShouldThrowExceptionIfTargetCharacterIsNull() {
        assertThrows(NullPointerException.class, () -> new ExtraResourcesOnCavePaintingsEffect(null, 2, 3));
    }

    @Test
    void constructorShouldThrowExceptionIfExtraFoodIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new ExtraResourcesOnCavePaintingsEffect(CharacterType.BUILDER, -1, 3));
    }

    @Test
    void constructorShouldThrowExceptionIfExtraPrestigeIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new ExtraResourcesOnCavePaintingsEffect(CharacterType.BUILDER, 2, -1));
    }

    @Test
    void constructorShouldAllowZeroResources() {
        assertDoesNotThrow(() -> new ExtraResourcesOnCavePaintingsEffect(CharacterType.BUILDER, 0, 0));
    }

    @Test
    void onCavePaintingsEventShouldThrowExceptionIfPlayerIsNull() {
        assertThrows(NullPointerException.class, () -> effect.onCavePaintingsEvent(null, dummyGameState));
    }

    @Test
    void onCavePaintingsEventShouldThrowExceptionIfGameStateIsNull() {
        assertThrows(NullPointerException.class, () -> effect.onCavePaintingsEvent(player, null));
    }

    @Test
    void onCavePaintingsEventShouldAddCorrectResourcesBasedOnCharacterCount() {
        // arrange: give the player 2 builders
        player.addBuilder();
        player.addBuilder();

        int initialFood = player.getFood();
        int initialPrestige = player.getPrestige();

        // act: trigger the cave paintings event
        effect.onCavePaintingsEvent(player, dummyGameState);

        // assert: 2 builders * 2 extra food = 4 total food added
        assertEquals(initialFood + 4, player.getFood(), "Food was not multiplied correctly by character count");

        // assert: 2 builders * 3 extra prestige = 6 total prestige added
        assertEquals(initialPrestige + 6, player.getPrestige(), "Prestige was not multiplied correctly by character count");
    }

    @Test
    void onCavePaintingsEventShouldNotAddResourcesIfPlayerHasZeroCharactersOfTargetType() {
        // arrange: player has 0 builders. Give them initial resources to verify they don't change
        player.addFood(5);
        player.addPrestige(5);

        int initialFood = player.getFood();
        int initialPrestige = player.getPrestige();

        // act: trigger the event
        effect.onCavePaintingsEvent(player, dummyGameState);

        // assert: resources should remain identical since the multiplier (character count) is 0
        assertEquals(initialFood, player.getFood());
        assertEquals(initialPrestige, player.getPrestige());
    }

    @Test
    void emptyMethodsShouldNotThrowExceptions() {
        assertDoesNotThrow(() -> effect.onAcquire(player, dummyGameState));
        assertDoesNotThrow(() -> effect.onRoundEnd(player, dummyGameState));
        assertDoesNotThrow(() -> effect.onGameEnd(player, dummyGameState));
    }
}