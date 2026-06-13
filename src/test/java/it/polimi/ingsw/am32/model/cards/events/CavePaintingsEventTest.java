package it.polimi.ingsw.am32.model.cards.events;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.Card;
import it.polimi.ingsw.am32.model.enums.EventType;
import it.polimi.ingsw.am32.model.receipts.PlayerStatsChangeReceipt;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;
import it.polimi.ingsw.am32.model.Deck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CavePaintingsEventTest {

    private CavePaintingsEvent event;

    // dummy game state to bypass complex initialization in pure java
    private static class DummyGameState extends GameState {
        private final Player testPlayer;

        public DummyGameState(Player testPlayer, TurnOrderTile dummyTile, Deck<Card> dummyDeck) {
            // pass valid dummy data to satisfy game state's strict constructor requirements
            super(1, 2, new ArrayList<>(), dummyTile, dummyDeck, new HashMap<>());
            this.testPlayer = testPlayer;
        }

        @Override
        public List<Player> getPlayers() {
            // override to return a list with only our test player for this specific test
            return Collections.singletonList(testPlayer);
        }
    }

    @BeforeEach
    void setUp() {
        // initialize valid event before each test
        event = new CavePaintingsEvent("CP_01", 1, false, 2, 2, 3);
    }

    @Test
    void constructorShouldSetValuesCorrectly() {
        // verify that superclass getters return correct values
        assertEquals("CP_01", event.getId());
        assertEquals(1, event.getEra());
        assertFalse(event.isFinal());
    }

    @Test
    void constructorShouldThrowExceptionIfPrestigeBonusIsNegative() {
        // verify exception for negative prestige bonus
        assertThrows(IllegalArgumentException.class, () -> new CavePaintingsEvent("CP_02", 1, false, -1, 2, 3));
    }

    @Test
    void constructorShouldThrowExceptionIfBonusThresholdIsNegative() {
        // verify exception for negative bonus threshold
        assertThrows(IllegalArgumentException.class, () -> new CavePaintingsEvent("CP_03", 1, false, 2, -1, 3));
    }

    @Test
    void constructorShouldThrowExceptionIfPrestigePenaltyIsNegative() {
        // verify exception for negative prestige penalty
        assertThrows(IllegalArgumentException.class, () -> new CavePaintingsEvent("CP_04", 1, false, 2, 2, -1));
    }

    @Test
    void getEventTypeShouldReturnCavePaintings() {
        // verify correct event type enum
        assertEquals(EventType.CAVE_PAINTINGS, event.getEventType());
    }

    @Test
    void resolveEventShouldThrowExceptionIfGameStateIsNull() {
        // verify exception for null game state
        assertThrows(NullPointerException.class, () -> event.resolveEvent(null));
    }

    @Test
    void resolveEventShouldReturnCorrectReceipt() {
        // create a player for the test and give initial prestige to prevent negative numbers
        Player player = new Player("Alice");
        player.addPrestige(5);

        // initialize valid modifier lists for the turn order tile (five zeroes for 5 possible players)
        List<Integer> foodMods = List.of(0, 0, 0, 0, 0);
        List<Integer> prestigeMods = List.of(0, 0, 0, 0, 0);

        // instantiate real dependencies with basic valid data
        TurnOrderTile dummyTile = new TurnOrderTile("TOT_01", 5, foodMods, prestigeMods);
        Deck<Card> dummyDeck = new Deck<>();

        // create the dummy game state using pure java
        DummyGameState dummyGameState = new DummyGameState(player, dummyTile, dummyDeck);

        // resolve the event
        List<PlayerStatsChangeReceipt> receipts = event.resolveEvent(dummyGameState);

        // verify the receipt list is not null and contains exactly one receipt
        assertNotNull(receipts);
        assertEquals(1, receipts.size());

        // verify the receipt contents using the correct getter and java 21 getFirst()
        PlayerStatsChangeReceipt receipt = receipts.getFirst();
        assertEquals("Alice", receipt.getPlayerNickname());
    }
}
