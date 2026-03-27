package it.polimi.ingsw.am32.tiles;


import it.polimi.ingsw.am32.tiles.TurnOrderTile;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.enums.TotemColor;
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
        turnOrderTile = new TurnOrderTile("TurnOrderId", 3,List.of(1,2,3), List.of(1,2,3));//da riguardare
    }
    @Test
    //checking if the player Nickname remain the same after the place of the totem
    void placePlayerShouldNotChangeNickname(){
        String initialPlayer = player.getNickname();
        turnOrderTile.placePlayer(player);
        assertEquals(initialPlayer,player.getNickname());
    }

    @Test

    void placePlayerShouldPlacePlayer() {

    }


    @Test

    void placePlayerShouldPlaceInRightPosition(){

    }

    // @Test
    //checking if player goes in a different position


    /*@Test
    checking if player hits another totem in the TurnOrderTile
    /void placePlayerShouldNotOccupateTile() {
        turnOrderTile.placePlayer(player);
        assertThrows(IllegalArgumentException.class, () -> {

        }*/
    @Test
    //checking if removed player is the one i want
    void removePlayerShouldRemovePlayer() {
        String initialPlayer = player.getNickname();
        turnOrderTile.removePlayer(player);
        assertEquals(initialPlayer,player.getNickname());
    }

    @Test
        //testing if i have brain because i'm not sure about this test , per quello che intendo io è sbagliata
    void removePlayerShouldNotRemoveOtherPlayer() {
        int numofplayer = turnOrderTile.getRequiredPlayers();
        turnOrderTile.removePlayer(otherPlayer);
        assertEquals(numofplayer, turnOrderTile.getRequiredPlayers());
    }

    @Test
    void isFullShouldReturnTrue() {
        turnOrderTile.placePlayer(player);
        turnOrderTile.placePlayer(otherPlayer);
        turnOrderTile.placePlayer(playerdemo);

        turnOrderTile.isFull();
        assertTrue(turnOrderTile.isFull());
    }

    @Test
    void isFullShouldReturnFalse() {
        turnOrderTile.isFull();
        assertFalse(turnOrderTile.isFull());
    }
}
