package it.polimi.ingsw.am32.model.tiles;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import it.polimi.ingsw.am32.model.exceptions.InvalidActionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TurnOrderTileTest {
    private TurnOrderTile turnOrderTile;
    private Player player;
    private Player otherPlayer;
    private Player playerdemo;

    @BeforeEach
    void setUp() {
        player = new Player("TestNickname1", TotemColor.RED);
        otherPlayer = new Player("TestNickname2", TotemColor.BLUE);
        playerdemo = new Player("TestNickname3", TotemColor.BLACK);
        turnOrderTile = new TurnOrderTile("TurnOrderId", 3, List.of(2, 0, -1), List.of(0, 0, -2));
    }

    @Test
    void constructorShouldThrowExceptionIfFoodModifiersIsNull(){
        assertThrows(NullPointerException.class, () -> new TurnOrderTile("TurnOrderId", 3, null, List.of(1,2,3)));
    }

    @Test
    void constructorShouldThrowExceptionIfPrestigeModifiersIsNull(){
        assertThrows(NullPointerException.class, () -> new TurnOrderTile("TurnOrderId", 3, List.of(1,2,3), null));
    }

    @Test
    void placePlayerShouldThrowExceptionIfPlayerIsNull(){
        assertThrows(NullPointerException.class, () -> turnOrderTile.placePlayer(null));
    }

    @Test
    void placePlayerShouldThrowExceptionIfTurnOrderTileIsFull() {
        turnOrderTile.placePlayer(player);
        turnOrderTile.placePlayer(otherPlayer);
        turnOrderTile.placePlayer(playerdemo);

        assertTrue(turnOrderTile.isFull());
        assertThrows(InvalidActionException.class, () -> turnOrderTile.placePlayer(player));
    }

    @Test
    void placePlayerShouldAddFoodIfModifierIsPositive() {
        int initialFood = player.getFood();

        turnOrderTile.placePlayer(player);

        assertEquals(initialFood + 2, player.getFood());
    }

    @Test
    void placePlayerShouldRemoveFoodAndHasEnoughFood(){
        player.addFood(1);

        turnOrderTile.placePlayer(playerdemo);
        turnOrderTile.placePlayer(otherPlayer);

        turnOrderTile.placePlayer(player);

        assertEquals(0, player.getFood());
    }

    @Test
    void placePlayerShouldRemovePrestige(){
        turnOrderTile.placePlayer(playerdemo);
        turnOrderTile.placePlayer(otherPlayer);

        turnOrderTile.placePlayer(player);

        assertEquals(-2, player.getPrestige());
    }

    @Test
    void removePlayerShouldThrowExceptionIfPlayerIsNull(){
        assertThrows(NullPointerException.class, () -> turnOrderTile.removePlayer(null));
    }

    @Test
    void removePlayerShouldRemovePlayer(){
        turnOrderTile.placePlayer(playerdemo);
        turnOrderTile.placePlayer(otherPlayer);
        turnOrderTile.placePlayer(player);

        turnOrderTile.removePlayer(player);

        assertEquals(List.of(playerdemo, otherPlayer), turnOrderTile.getTurnOrder());
    }

    @Test
    void initPlayersOrderShouldThrowExceptionIfPlayerListIsNull(){
        assertThrows(NullPointerException.class, () -> turnOrderTile.initPlayersOrder(null));
    }

    @Test
    void initPlayersOrderShouldThrowExceptionIfExceedsCapacityWithExistingPlayers(){
        turnOrderTile.placePlayer(player);
        turnOrderTile.placePlayer(otherPlayer);

        assertThrows(IllegalStateException.class, () -> turnOrderTile.initPlayersOrder(List.of(playerdemo, player)));
    }

    @Test
    void initPlayersOrderShouldThrowExceptionIfListExceedsCapacity(){
        Player extraPlayer = new Player("TestNickname4", TotemColor.WHITE);

        assertThrows(IllegalStateException.class, () -> turnOrderTile.initPlayersOrder(List.of(player, otherPlayer, playerdemo, extraPlayer))
        );
    }

    @Test
    void initPlayersOrderShouldAddPlayersToTurnOrder(){
        turnOrderTile.initPlayersOrder(List.of(player, otherPlayer));

        assertEquals(List.of(player, otherPlayer), turnOrderTile.getTurnOrder());
    }

}