package it.polimi.ingsw.am32.model.tiles;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.exceptions.InvalidGameActionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OfferTileTest {

    private Action dummyAction;
    private OfferTile offerTile;

    @BeforeEach
    void setUp() {
        // Create a dummy action for testing purposes
        dummyAction = new Action(1, 0, 0) {
            @Override
            public boolean isValidSelection(int requestedUpper, int requestedLower,
                                            int characterCardsUpper, int characterCardsLower,
                                            int affordableBuildingsUpper, int affordableBuildingsLower) {
                // Return true only if requestedUpper is equal to 1 to test the delegation
                return requestedUpper == 1;
            }
        };

        // Initialize the offer tile before each test
        offerTile = new OfferTile("OT_01", 3, dummyAction);
    }

    @Test
    void constructorShouldSetValuesAndInitiallyBeFree() {
        // Act and Assert: verify initial values via getters
        assertEquals("OT_01", offerTile.getId());
        assertEquals(3, offerTile.getRequiredPlayers());
        assertEquals(dummyAction, offerTile.getAction());
        assertNull(offerTile.getOccupyingPlayer());
        assertTrue(offerTile.isFree());
    }

    @Test
    void constructorShouldThrowExceptionIfActionIsNull() {
        // Arrange, Act and Assert: verify exception for null action
        assertThrows(NullPointerException.class, () -> {
            new OfferTile("OT_02", 3, null);
        });
    }

    @Test
    void placePlayerShouldSetOccupyingPlayerAndMakeTileNotFree() {
        // Arrange: create a player
        Player player = new Player("Alice");

        // Act: place the player on the tile
        offerTile.placePlayer(player);

        // Assert: verify that the tile is occupied by Alice
        assertFalse(offerTile.isFree());
        assertEquals(player, offerTile.getOccupyingPlayer());
    }

    @Test
    void placePlayerShouldThrowExceptionIfPlayerIsNull() {
        // Arrange, Act and Assert: verify exception for null player
        assertThrows(NullPointerException.class, () -> {
            offerTile.placePlayer(null);
        });
    }

    @Test
    void placePlayerShouldThrowExceptionIfTileIsAlreadyOccupied() {
        // Arrange: place a first player
        offerTile.placePlayer(new Player("Alice"));

        // Act and Assert: placing another player must throw an exception
        assertThrows(InvalidGameActionException.class, () -> {
            offerTile.placePlayer(new Player("Bob"));
        });
    }

    @Test
    void removePlayerShouldFreeTheTile() {
        // Arrange: place a player
        offerTile.placePlayer(new Player("Charlie"));
        assertFalse(offerTile.isFree());

        // Act: remove the player
        offerTile.removePlayer();

        // Assert: the tile must become free again
        assertTrue(offerTile.isFree());
        assertNull(offerTile.getOccupyingPlayer());
    }

    @Test
    void isValidSelectionShouldDelegateToAction() {
        // Act: invoke the method with requestedUpper = 1 (evaluated as true by the dummy action)
        boolean resultTrue = offerTile.isValidSelection(1, 0, 0, 0, 0, 0);

        // Act: invoke the method with requestedUpper different from 1 (evaluated as false)
        boolean resultFalse = offerTile.isValidSelection(2, 0, 0, 0, 0, 0);

        // Assert: the response must match the logic of the dummy action
        assertTrue(resultTrue);
        assertFalse(resultFalse);
    }

    @Test
    void isOccupiedByShouldReturnTrueOnlyForOccupyingPlayer() {
        // Arrange: create two players
        Player player1 = new Player("Alice");
        Player player2 = new Player("Bob");

        // Assert: the empty tile is not occupied by player1
        assertFalse(offerTile.isOccupiedBy(player1));

        // Act: place player 1
        offerTile.placePlayer(player1);

        // Assert: it must be occupied only by player1
        assertTrue(offerTile.isOccupiedBy(player1));
        assertFalse(offerTile.isOccupiedBy(player2));
    }
}
