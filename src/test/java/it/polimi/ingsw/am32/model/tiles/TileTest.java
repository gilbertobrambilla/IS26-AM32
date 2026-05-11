package it.polimi.ingsw.am32.model.tiles;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    //create a dummy concrete class to test the abstract tile class
    private static class DummyTile extends Tile {

        //constructor that calls the superclass constructor
        public DummyTile(String id, int requiredPlayers) {
            super(id, requiredPlayers);
        }
    }

    @Test
    void constructorShouldSetValuesCorrectly() {
        //arrange: create a valid dummy tile
        Tile tile = new DummyTile("T_01", 3);

        //act and assert: verify that getters return the correct values
        assertEquals("T_01", tile.getId());
        assertEquals(3, tile.getRequiredPlayers());
    }

    @Test
    void constructorShouldThrowExceptionIfIdIsNull() {
        //arrange, act and assert: verify that a null pointer exception is thrown
        assertThrows(NullPointerException.class, () -> {
            new DummyTile(null, 4);
        });
    }

    @Test
    void constructorShouldThrowExceptionIfRequiredPlayersIsNegative() {
        //arrange, act and assert: verify exception for negative required players
        assertThrows(IllegalArgumentException.class, () -> {
            new DummyTile("T_02", -1);
        });
    }

    @Test
    void constructorShouldAllowZeroRequiredPlayers() {
        //arrange, act and assert: verify zero is accepted as it is not negative
        assertDoesNotThrow(() -> {
            new DummyTile("T_03", 0);
        });
    }
}