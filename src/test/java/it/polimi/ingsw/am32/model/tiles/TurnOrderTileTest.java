package it.polimi.ingsw.am32.model.tiles;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.exceptions.InvalidActionException;
import it.polimi.ingsw.am32.model.receipts.TurnOrderPlacementReceipt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TurnOrderTileTest {

    private TurnOrderTile tile;
    private List<Integer> foodModifiers;
    private List<Integer> prestigeModifiers;

    @BeforeEach
    void setUp() {
        //setup basic modifiers for a 3-player tile
        foodModifiers = Arrays.asList(0, 0, -1);
        prestigeModifiers = Arrays.asList(0, 0, -2);
        tile = new TurnOrderTile("TOT_01", 3, foodModifiers, prestigeModifiers);
    }

    @Test
    void constructorShouldThrowExceptionIfModifiersAreNull() {
        //arrange, act and assert: check null food modifiers
        assertThrows(NullPointerException.class, () -> {
            new TurnOrderTile("TOT_02", 3, null, prestigeModifiers);
        });

        //arrange, act and assert: check null prestige modifiers
        assertThrows(NullPointerException.class, () -> {
            new TurnOrderTile("TOT_03", 3, foodModifiers, null);
        });
    }

    @Test
    void initPlayersOrderShouldAddPlayersSuccessfully() {
        //arrange: create a list of players
        List<Player> players = Arrays.asList(new Player("Alice"), new Player("Bob"));

        //act: init the turn order
        tile.initPlayersOrder(players);

        //assert: turn order should contain the players
        assertEquals(2, tile.getTurnOrder().size());
        assertEquals("Alice", tile.getTurnOrder().get(0).getNickname());
        assertEquals("Bob", tile.getTurnOrder().get(1).getNickname());
    }

    @Test
    void initPlayersOrderShouldThrowExceptionIfCapacityExceeded() {
        //arrange: create a list with too many players (4 players for a 3-player capacity)
        List<Player> tooManyPlayers = Arrays.asList(
                new Player("P1"), new Player("P2"), new Player("P3"), new Player("P4")
        );

        //act and assert: check that illegal state exception is thrown
        assertThrows(IllegalStateException.class, () -> {
            tile.initPlayersOrder(tooManyPlayers);
        });
    }

    @Test
    void placePlayerShouldAddPlayerAndReturnReceipt() {
        //arrange: create a player
        Player player = new Player("Charlie");

        //act: place player on the tile
        TurnOrderPlacementReceipt receipt = tile.placePlayer(player);

        //assert: receipt is generated and player is in the list
        assertNotNull(receipt);
        assertEquals(1, tile.getTurnOrder().size());
        assertEquals(player, tile.getTurnOrder().get(0));
    }

    @Test
    void placePlayerShouldThrowExceptionIfTileIsFull() {
        //arrange: fill the tile completely
        tile.placePlayer(new Player("P1"));
        tile.placePlayer(new Player("P2"));
        tile.placePlayer(new Player("P3"));

        //act and assert: placing a 4th player should throw invalid action exception
        assertThrows(InvalidActionException.class, () -> {
            tile.placePlayer(new Player("P4"));
        });
    }

    @Test
    void isFullShouldReturnCorrectStatus() {
        //assert: initially not full
        assertFalse(tile.isFull());

        //act: add players up to capacity
        tile.placePlayer(new Player("P1"));
        tile.placePlayer(new Player("P2"));
        assertFalse(tile.isFull());

        tile.placePlayer(new Player("P3"));

        //assert: now it should be full
        assertTrue(tile.isFull());
    }

    @Test
    void removePlayerShouldRemoveTheCorrectPlayer() {
        //arrange: place two players
        Player p1 = new Player("PlayerOne");
        Player p2 = new Player("PlayerTwo");
        tile.placePlayer(p1);
        tile.placePlayer(p2);

        //act: remove the first player
        tile.removePlayer(p1);

        //assert: only the second player should remain
        assertEquals(1, tile.getTurnOrder().size());
        assertEquals(p2, tile.getTurnOrder().get(0));
    }
}