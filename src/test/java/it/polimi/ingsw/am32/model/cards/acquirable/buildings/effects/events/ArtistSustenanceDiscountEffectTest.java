package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.events;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.Card;
import it.polimi.ingsw.am32.model.Deck;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArtistSustenanceDiscountEffectTest {

    private ArtistSustenanceDiscountEffect effect;
    private Player player;
    private GameState dummyGameState;

    // dummy game state to bypass complex initialization in pure java
    private static class DummyGameState extends GameState {
        public DummyGameState(TurnOrderTile dummyTile, Deck<Card> dummyDeck) {
            super(1, 2, new ArrayList<>(), dummyTile, dummyDeck, new HashMap<>());
        }
    }

    /**
     * Helper method using Java Reflection to read the private 'sustenanceDiscount'
     * field from the Player class without breaking encapsulation or requiring a getter.
     */
    private int getHiddenSustenanceDiscount(Player p) throws Exception {
        Field discountField = Player.class.getDeclaredField("sustenanceDiscount");
        discountField.setAccessible(true);
        return (int) discountField.get(p);
    }

    @BeforeEach
    void setUp() {
        // initialize the effect with a discount of 2 per artist
        effect = new ArtistSustenanceDiscountEffect(2);
        player = new Player("Henry");

        // instantiate real dependencies with basic valid data for gamestate
        List<Integer> foodMods = List.of(0, 0, 0, 0, 0);
        List<Integer> prestigeMods = List.of(0, 0, 0, 0, 0);
        TurnOrderTile dummyTile = new TurnOrderTile("TOT_01", 5, foodMods, prestigeMods);
        Deck<Card> dummyDeck = new Deck<>();

        dummyGameState = new DummyGameState(dummyTile, dummyDeck);
    }

    @Test
    void constructorShouldThrowExceptionIfDiscountIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new ArtistSustenanceDiscountEffect(-1));
    }

    @Test
    void constructorShouldAllowZeroDiscount() {
        assertDoesNotThrow(() -> new ArtistSustenanceDiscountEffect(0));
    }

    @Test
    void onAcquireShouldThrowExceptionIfPlayerIsNull() {
        assertThrows(NullPointerException.class, () -> effect.onAcquire(null, dummyGameState));
    }

    @Test
    void onAcquireShouldThrowExceptionIfGameStateIsNull() {
        assertThrows(NullPointerException.class, () -> effect.onAcquire(player, null));
    }

    @Test
    void onAcquireShouldApplyRetroactiveDiscountForExistingArtists() throws Exception {
        // arrange: simulate the player already having 3 artists before acquiring the building
        player.addArtist();
        player.addArtist();
        player.addArtist();

        // act: apply the acquire effect
        effect.onAcquire(player, dummyGameState);

        // assert: 3 artists * 2 discount each = 6 total discount
        int actualDiscount = getHiddenSustenanceDiscount(player);
        assertEquals(6, actualDiscount, "Retroactive discount was not calculated correctly");
    }

    @Test
    void onArtistAddedShouldThrowExceptionIfPlayerIsNull() {
        assertThrows(NullPointerException.class, () -> effect.onArtistAdded(null));
    }

    @Test
    void onArtistAddedShouldIncreaseSustenanceDiscount() throws Exception {
        // act: trigger the effect as if a new artist was just added
        effect.onArtistAdded(player);

        // assert: the player should have received a discount increase of 2
        int actualDiscount = getHiddenSustenanceDiscount(player);
        assertEquals(2, actualDiscount, "Discount was not applied when artist was added");
    }

    @Test
    void emptyMethodsShouldNotThrowExceptions() {
        assertDoesNotThrow(() -> effect.onRoundEnd(player, dummyGameState));
        assertDoesNotThrow(() -> effect.onGameEnd(player, dummyGameState));
    }
}