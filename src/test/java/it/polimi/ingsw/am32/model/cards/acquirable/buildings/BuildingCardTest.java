package it.polimi.ingsw.am32.model.cards.acquirable.buildings;

import it.polimi.ingsw.am32.model.Deck;
import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.CardVisitor;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.NoEffect;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.model.cards.events.EventCard;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BuildingCardTest {

    private Player player;
    private GameState gameState;
    private NoEffect noEffect;

    @BeforeEach
    void setUp() {
        // instantiate a real player for testing state changes
        player = new Player("TestPlayer");

        // create a dummy turn order tile to satisfy game state constructor requirements
        TurnOrderTile dummyTile = new TurnOrderTile("dummy_tile", 2, Arrays.asList(0, 0), Arrays.asList(0, 0));

        // instantiate a minimal dummy game state to avoid null pointer exceptions
        gameState = new GameState(1, 2, new ArrayList<>(), dummyTile, new Deck<>(), new HashMap<>());

        // instantiate a dummy effect for the building card
        noEffect = new NoEffect();
    }

    @Test
    void constructorCreatesObjectWithValidParameters() {
        // initialize the card with valid parameters
        BuildingCard card = new BuildingCard("building_1", 1, 2, 3, noEffect);

        // verify that the object is successfully created and getters return expected values
        assertNotNull(card);
        assertEquals("building_1", card.getId());
        assertEquals(1, card.getEra());
        assertEquals(2, card.getBasePrestige());
        assertEquals(3, card.getCost());
        assertEquals(noEffect, card.getEffect());
    }

    @Test
    void constructorThrowsExceptionWhenCostIsNegative() {
        // expect an illegal argument exception when cost is negative
        assertThrows(IllegalArgumentException.class, () -> new BuildingCard("building_1", 1, 2, -1, noEffect));
    }

    @Test
    void constructorThrowsExceptionWhenEffectIsNull() {
        // expect a null pointer exception when effect is null
        assertThrows(NullPointerException.class, () -> new BuildingCard("building_1", 1, 2, 3, null));
    }

    @Test
    void getDiscountedCostAppliesDiscountCorrectly() {
        // initialize the card with cost 5
        BuildingCard card = new BuildingCard("building_1", 1, 2, 5, noEffect);

        // verify that applying a discount of 2 results in a cost of 3
        assertEquals(3, card.getDiscountedCost(2));
    }

    @Test
    void getDiscountedCostReturnsZeroWhenDiscountExceedsCost() {
        // initialize the card with cost 3
        BuildingCard card = new BuildingCard("building_1", 1, 2, 3, noEffect);

        // verify that applying a discount of 5 results in a cost of 0, not negative
        assertEquals(0, card.getDiscountedCost(5));
    }

    @Test
    void onGameEndAddsBasePrestigeToPlayer() {
        // initialize the card with base prestige 4
        BuildingCard card = new BuildingCard("building_1", 1, 4, 3, noEffect);

        // trigger the endgame method
        card.onGameEnd(player, gameState);

        // verify that the player received the base prestige points
        assertEquals(4, player.getPrestige());
    }

    @Test
    void onGameEndThrowsExceptionIfPlayerIsNull() {
        // initialize a valid card
        BuildingCard card = new BuildingCard("building_1", 1, 2, 3, noEffect);

        // expect a null pointer exception when player is null
        assertThrows(NullPointerException.class, () -> card.onGameEnd(null, gameState));
    }

    @Test
    void onGameEndThrowsExceptionIfGameStateIsNull() {
        // initialize a valid card
        BuildingCard card = new BuildingCard("building_1", 1, 2, 3, noEffect);

        // expect a null pointer exception when game state is null
        assertThrows(NullPointerException.class, () -> card.onGameEnd(player, null));
    }

    @Test
    void acceptThrowsExceptionIfVisitorIsNull() {
        // initialize a valid card
        BuildingCard card = new BuildingCard("building_1", 1, 2, 3, noEffect);

        // expect a null pointer exception when visitor is null
        assertThrows(NullPointerException.class, () -> card.accept(null));
    }

    @Test
    void acceptCallsVisitOnVisitor() {
        // initialize a valid card
        BuildingCard card = new BuildingCard("building_1", 1, 2, 3, noEffect);

        // create a simple custom visitor to track the method call
        class TestVisitor implements CardVisitor {
            boolean visited = false;

            @Override
            public void visit(EventCard card) {}

            @Override
            public void visit(CharacterCard card) {}

            @Override
            public void visit(BuildingCard card) {
                visited = true;
            }
        }

        TestVisitor visitor = new TestVisitor();
        card.accept(visitor);

        // verify that the visitor was correctly called
        assertTrue(visitor.visited);
    }

    @Test
    void delegationMethodsExecuteWithoutExceptions() {
        // initialize a valid card
        BuildingCard card = new BuildingCard("building_1", 1, 2, 3, noEffect);

        // verify that calling delegation methods does not throw any exception
        assertDoesNotThrow(() -> {
            card.onArtistAdded(player);
            card.onBuilderAdded(player);
            card.onGathererAdded(player);
            card.onHunterAdded(player);
            card.onInventorAdded(player);
            card.onShamanAdded(player);
            card.applyAcquireEffect(player, gameState);
            card.applyRoundEnd(player, gameState);
            card.applyTurnOrderPlaced(player, true);
            card.applySetCompleted(player);
            card.applyInventorPairCompleted(player);
            card.applyHuntEvent(player, gameState);
            card.applySustenanceEvent(player, gameState);
            card.applyCavePaintingsEvent(player, gameState);
            card.applyShamanicRitualEvent(player, gameState);
        });
    }
}