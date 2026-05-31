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
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShamanicRitualEventTest {

    private ShamanicRitualEvent event;

    // dummy game state to bypass complex initialization in pure java
    private static class DummyGameState extends GameState {
        private final List<Player> testPlayers;

        public DummyGameState(List<Player> testPlayers, TurnOrderTile dummyTile, Deck<Card> dummyDeck) {
            // pass valid dummy data to satisfy game state's strict constructor requirements
            super(1, 2, new ArrayList<>(), dummyTile, dummyDeck, new HashMap<>());
            this.testPlayers = testPlayers;
        }

        @Override
        public List<Player> getPlayers() {
            // override to return our custom list of players for this specific test
            return new ArrayList<>(testPlayers);
        }
    }

    @BeforeEach
    void setUp() {
        // initialize valid shamanic ritual event before each test (prestige bonus: 5, penalty: 3)
        event = new ShamanicRitualEvent("SHM_01", 1, false, 5, 3);
    }

    @Test
    void constructorShouldSetValuesCorrectly() {
        // verify that superclass getters return correct values
        assertEquals("SHM_01", event.getId());
        assertEquals(1, event.getEra());
        assertFalse(event.isFinal());
    }

    @Test
    void constructorShouldThrowExceptionIfPrestigeBonusIsNegative() {
        // verify exception for negative prestige bonus
        assertThrows(IllegalArgumentException.class, () -> new ShamanicRitualEvent("SHM_02", 1, false, -1, 3));
    }

    @Test
    void constructorShouldThrowExceptionIfPrestigePenaltyIsNegative() {
        // verify exception for negative prestige penalty
        assertThrows(IllegalArgumentException.class, () -> new ShamanicRitualEvent("SHM_03", 1, false, 5, -1));
    }

    @Test
    void getEventTypeShouldReturnShamanicRitual() {
        // verify correct event type enum
        assertEquals(EventType.SHAMANIC_RITUAL, event.getEventType());
    }

    @Test
    void resolveEventShouldThrowExceptionIfGameStateIsNull() {
        // verify exception for null game state
        assertThrows(NullPointerException.class, () -> event.resolveEvent(null));
    }

    @Test
    void resolveEventShouldReturnCorrectReceiptsForMultiplePlayers() {
        // create player 1 (winner candidate)
        Player player1 = new Player("Alice");
        player1.addShamanStars(2); // assuming this method exists from our previous shaman character setup

        // create player 2 (loser candidate)
        Player player2 = new Player("Bob");
        player2.addPrestige(10); // give bob prestige so a penalty doesn't push him below zero

        List<Player> players = List.of(player1, player2);

        // initialize valid modifier lists for the turn order tile
        List<Integer> foodMods = List.of(0, 0, 0, 0, 0);
        List<Integer> prestigeMods = List.of(0, 0, 0, 0, 0);

        // instantiate real dependencies with basic valid data
        TurnOrderTile dummyTile = new TurnOrderTile("TOT_01", 5, foodMods, prestigeMods);
        Deck<Card> dummyDeck = new Deck<>();

        // create the dummy game state using pure java and our multiple players
        DummyGameState dummyGameState = new DummyGameState(players, dummyTile, dummyDeck);

        // resolve the event
        List<PlayerStatsChangeReceipt> receipts = event.resolveEvent(dummyGameState);

        // verify the receipt list is not null and contains exactly two receipts (one per player)
        assertNotNull(receipts);
        assertEquals(2, receipts.size());

        // extract receipts and verify they correspond to our players
        PlayerStatsChangeReceipt receiptAlice = receipts.get(0);
        PlayerStatsChangeReceipt receiptBob = receipts.get(1);

        assertEquals("Alice", receiptAlice.getPlayerNickname());

        assertEquals("Bob", receiptBob.getPlayerNickname());
    }
}
