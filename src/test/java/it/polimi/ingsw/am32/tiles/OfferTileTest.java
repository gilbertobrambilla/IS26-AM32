package it.polimi.ingsw.am32.tiles;

import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.enums.TotemColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OfferTileTest {

    private OfferTile offerTile;
    private Player player;
    private Player otherPlayer;
    private Player playerdemo;
    private Action action;


    @BeforeEach
    void setUp() {
        player = new Player("Nickname1", TotemColor.RED);
        otherPlayer = new Player("Nickname2", TotemColor.BLUE);
        playerdemo = new Player("Nickname3",TotemColor.BLACK);
        offerTile = new OfferTile("id", 3, action);

    }

    @Test
    void getActionShouldReturnAction() {
        assertEquals(action, offerTile.getAction());
    }


    @Test
    void getOccupyingPlayerShouldReturnPlayer() {
        assertEquals(player, offerTile.getOccupyingPlayer());
    }

    @Test
    void getOccupyingPlayerShouldnotReturnPlayer() {
        assertNotEquals(otherPlayer, offerTile.getOccupyingPlayer());
    }
    @Test
    void placePlayerShouldPlacePlayer() {
        int numofplayers = offerTile.getRequiredPlayers();
        offerTile.placePlayer(player);
        assertEquals(player, offerTile.getOccupyingPlayer());
    }
    @Test
        //checking if the player Nickname remain the same after the place of the totem
    void placePlayerShouldNotChangeNickname(){
        String initialPlayer = player.getNickname();
        offerTile.placePlayer(player);
        assertEquals(initialPlayer,player.getNickname());
    }
    /*@Test
    Questo test dovrebbe fallire per fare un effettivo Test

    void placePlayerShouldnotPlacePlayer() {
        assertNotEquals(otherPlayer, offerTile.getOccupyingPlayer());
    }*/

   /* @Test
    void placePlayerCannotPlacePlayer() {
        offerTile.placePlayer(otherPlayer);
        assertThrows(IllegalArgumentException.class, () -> offerTile.placePlayer(player));
    }*/

    @Test
    void removePlayerShouldRemovePlayer() {
        int numofplayers = offerTile.getRequiredPlayers();
        offerTile.removePlayer();
        assertEquals(otherPlayer, offerTile.getOccupyingPlayer());
    }

    @Test
    void removePlayerShouldnotRemoveOtherPlayer() {
        offerTile.removePlayer();
        assertNotEquals(otherPlayer, offerTile.getOccupyingPlayer());
    }

    @Test
    void isFreeShouldReturnTrue() {
        assertTrue(offerTile.isFree());
    }
    /*@Test
    void isFreeShouldReturnFalse() {
        assertTrue(offerTile.isFree());
    }*/
}