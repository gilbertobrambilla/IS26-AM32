package it.polimi.ingsw.am32.model.tiles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    private static class DummyTile extends Tile {
        public DummyTile(String id, int requiredPlayers) {
            super(id, requiredPlayers);
        }
    }

    private DummyTile tile;

    @BeforeEach
    void setUp() {
        tile = new DummyTile("tile_01", 3);
    }

    @Test
    void constructorShouldThrowExceptionIfIdIsNull() {
        assertThrows(NullPointerException.class, () -> new DummyTile(null, 3));
    }

    @Test
    void constructorShouldThrowExceptionIfRequiredPlayersIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> new DummyTile("tile_err", -1));
    }

    @Test
    void constructorShouldNotThrowExceptionIfRequiredPlayersIsZero() {
        assertDoesNotThrow(() -> new DummyTile("tile_zero", 0));
    }
}