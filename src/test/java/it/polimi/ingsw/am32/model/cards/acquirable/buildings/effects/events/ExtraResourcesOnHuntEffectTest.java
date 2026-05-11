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

class ExtraResourcesOnHuntEffectTest {

    private ExtraResourcesOnHuntEffect effect;
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
        // initialize the effect targeting gatherers: grants 1 extra food and 2 extra prestige per gatherer
        effect = new ExtraResourcesOnHuntEffect(CharacterType.GATHERER, 1, 2);
        player = new Player("Jack");

        // instantiate real dependencies with basic valid data for gamestate
        List<Integer> foodMods = List.of(0, 0, 0, 0, 0);
        List<Integer> prestigeMods = List.of(0, 0, 0, 0, 0);
        TurnOrderTile dummyTile = new TurnOrderTile("TOT_01", 5, foodMods, prestigeMods);
        Deck<Card> dummyDeck = new Deck<>();

        dummyGameState = new DummyGameState(dummyTile, dummyDeck);
    }

    @Test
    void constructorShouldThrowExceptionIfTargetCharacterIsNull() {
        assertThrows(NullPointerException.class, () -> new ExtraResourcesOnHuntEffect(null, 1, 2));
    }

    @Test
    void constructorShouldThrowExceptionIfExtraFoodIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new ExtraResourcesOnHuntEffect(CharacterType.GATHERER, -1, 2));
    }

    @Test
    void constructorShouldThrowExceptionIfExtraPrestigeIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new ExtraResourcesOnHuntEffect(CharacterType.GATHERER, 1, -1));
    }

    @Test
    void onHuntEventShouldThrowExceptionIfPlayerIsNull() {
        assertThrows(NullPointerException.class, () -> effect.onHuntEvent(null, dummyGameState));
    }

    @Test
    void onHuntEventShouldThrowExceptionIfGameStateIsNull() {
        assertThrows(NullPointerException.class, () -> effect.onHuntEvent(player, null));
    }

    @Test
    void onHuntEventShouldAddCorrectResourcesBasedOnCharacterCount() {
        // arrange: give the player 3 gatherers
        player.addGatherer();
        player.addGatherer();
        player.addGatherer();

        int initialFood = player.getFood();
        int initialPrestige = player.getPrestige();

        // act: trigger the hunt event effect
        effect.onHuntEvent(player, dummyGameState);

        // assert: 3 gatherers * 1 extra food = 3 total food added
        assertEquals(initialFood + 3, player.getFood(), "Food was not multiplied correctly by gatherer count");

        // assert: 3 gatherers * 2 extra prestige = 6 total prestige added
        assertEquals(initialPrestige + 6, player.getPrestige(), "Prestige was not multiplied correctly by gatherer count");
    }

    @Test
    void onHuntEventShouldNotAddResourcesIfPlayerHasZeroCharactersOfTargetType() {
        // arrange: player has 0 gatherers but has 5 initial food/prestige
        player.addFood(5);
        player.addPrestige(5);

        int initialFood = player.getFood();
        int initialPrestige = player.getPrestige();

        // act: trigger the event
        effect.onHuntEvent(player, dummyGameState);

        // assert: resources should remain unchanged
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