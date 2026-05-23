package it.polimi.ingsw.am32.model.factories;

import it.polimi.ingsw.am32.dto.setup.OfferTileDTO;
import it.polimi.ingsw.am32.model.tiles.Action;
import it.polimi.ingsw.am32.model.tiles.OfferTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Factory responsible for creating offer tiles from dto.
 */
public class OfferTileFactory implements TileFactory<OfferTileDTO, OfferTile> {

    /**
     * Initializes the offer tile factory.
     */
    public OfferTileFactory() {
        // default initialization is performed
    }

    /**
     * Creates a list of tiles from a list of dto.
     *
     * @param dtoList the list of dto
     * @return the list of created tiles
     */
    @Override
    public List<OfferTile> createTiles(List<OfferTileDTO> dtoList) {
        // dto list is checked for nullability
        Objects.requireNonNull(dtoList, "dtoList cannot be null");

        // list of tiles is initialized
        List<OfferTile> tiles = new ArrayList<>();

        // dto are iterated to create tiles
        for (OfferTileDTO dto : dtoList) {

            // action is created
            Action action = new Action(
                    dto.getDrawsFromUpperRow(),
                    dto.getDrawsFromLowerRow(),
                    dto.getFoodBonus()
            );

            // tile is created
            OfferTile newTile = new OfferTile(
                    dto.getId(),
                    dto.getMinPlayers(),
                    action
            );

            // tile is added to the list
            tiles.add(newTile);
        }

        // list is returned
        return tiles;
    }
}
