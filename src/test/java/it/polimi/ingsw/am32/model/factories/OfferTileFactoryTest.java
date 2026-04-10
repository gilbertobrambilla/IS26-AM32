package it.polimi.ingsw.am32.model.factories;

import it.polimi.ingsw.am32.model.dto.setup.OfferTileDTO;
import it.polimi.ingsw.am32.model.tiles.OfferTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OfferTileFactoryTest {

    private OfferTileFactory factory;

    @BeforeEach
    void setUp() {
        factory = new OfferTileFactory();
    }

    @Test
    void createTilesShouldReturnListOfValidOfferTiles() {
        List<OfferTileDTO> dtoList = new ArrayList<>();

        dtoList.add(new OfferTileDTO("off_01", 3, 1, 0, 2));
        dtoList.add(new OfferTileDTO("off_02", 4, 0, 2, 0));

        List<OfferTile> tiles = factory.createTiles(dtoList);

        assertEquals(2, tiles.size());

        assertEquals("off_01", tiles.get(0).getId());
        assertEquals(3, tiles.get(0).getRequiredPlayers());
        assertEquals(1, tiles.get(0).getAction().getDrawsFromUpperRow());
        assertEquals(0, tiles.get(0).getAction().getDrawsFromLowerRow());
        assertEquals(2, tiles.get(0).getAction().getFoodBonus());

        assertEquals("off_02", tiles.get(1).getId());
        assertEquals(4, tiles.get(1).getRequiredPlayers());
        assertEquals(0, tiles.get(1).getAction().getDrawsFromUpperRow());
        assertEquals(2, tiles.get(1).getAction().getDrawsFromLowerRow());
        assertEquals(0, tiles.get(1).getAction().getFoodBonus());
    }

    @Test
    void createTilesShouldThrowExceptionIfListIsNull() {
        assertThrows(NullPointerException.class, () -> factory.createTiles(null));
    }

    @Test
    void createTilesShouldLetIllegalArgumentExceptionPropagateWhenActionParamsAreNegative() {
        List<OfferTileDTO> dtoList = new ArrayList<>();
        dtoList.add(new OfferTileDTO("off_err", 3, -1, 0, 0));

        assertThrows(IllegalArgumentException.class, () -> factory.createTiles(dtoList));
    }
}