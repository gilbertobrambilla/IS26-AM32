package it.polimi.ingsw.am32.tiles;

import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.enums.InvalidActionReason;
import it.polimi.ingsw.am32.enums.TotemColor;
import it.polimi.ingsw.am32.exceptions.InvalidActionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        playerdemo = new Player("Nickname3", TotemColor.BLACK);
        action = new Action(1, 1, 2);
        offerTile = new OfferTile("id", 3, action);
    }

    @Test
    void constructorShouldThrowExceptionIfActionIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new OfferTile("id", 3, null);
        });
    }

    @Test
    void placePlayerShouldThrowExceptionIfPlayerIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new OfferTile("id", 3, null);
        });
    }

    @Test
    void placePlayerShouldThrowExceptionIfIsFreeIsNotNull() {
        assertThrows(NullPointerException.class, () -> {
            new OfferTile("id", 3, null);
        });
    }
    @Test
    void placePlayerShouldThrowExceptionIfisFreeIsNotNull() {
        assertThrows(NullPointerException.class, () -> {
            offerTile.placePlayer(null);
        });
    }
    @Test
    void placePlayerShouldPlacePlayer() {
        Player player = offerTile.getOccupyingPlayer();
        offerTile.placePlayer(playerdemo);
        assertEquals(playerdemo, offerTile.getOccupyingPlayer());
    }
    @Test
    void placePlayerShouldThrowExceptionIfIsFreeIsNotFree() {
        offerTile.placePlayer(playerdemo);

        assertFalse(offerTile.isFree(),"The tile is already occupied");
        assertThrows(InvalidActionException.class, () -> {offerTile.placePlayer(playerdemo);});

    }

    @Test
    void removePlayerShouldThrowExceptionIfPlayerIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new OfferTile("id", 3, null);
        });
    }

    @Test
    void getActionShouldReturnAction() {
        assertEquals(action, offerTile.getAction());
    }

    /*@Test
    void removePlayerShouldThrowExceptionIfPlayerIsFreeIsNotNull() {
        assertThrows(NullPointerException.class, () -> {})
    }
    */
    @Test
    void removePlayerShouldRemovePlayer() {
        offerTile.placePlayer(playerdemo);

        offerTile.removePlayer();
        assertEquals(null, offerTile.getOccupyingPlayer());
    }
}

   /* @Test
    void placePlayerCannotPlacePlayer() {
        offerTile.placePlayer(otherPlayer);
        assertThrows(IllegalArgumentException.class, () -> offerTile.placePlayer(player));
    }

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
    }*/
