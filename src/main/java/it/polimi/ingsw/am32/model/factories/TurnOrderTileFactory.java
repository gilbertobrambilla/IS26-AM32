package it.polimi.ingsw.am32.model.factories;

import it.polimi.ingsw.am32.dto.setup.TurnOrderTileDTO;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Factory responsible for creating turn order tiles from dto.
 */
public class TurnOrderTileFactory implements TileFactory<TurnOrderTileDTO, TurnOrderTile> {

    /**
     * Initializes the turn order tile factory.
     */
    public TurnOrderTileFactory() {
        // default initialization is performed
    }

    /**
     * Creates a list of tiles from a list of dto.
     *
     * @param dtoList the list of dto
     * @return the list of created tiles
     */
    @Override
    public List<TurnOrderTile> createTiles(List<TurnOrderTileDTO> dtoList) {
        // dto list is checked for nullability
        Objects.requireNonNull(dtoList, "dtoList cannot be null");

        // list of tiles is initialized
        List<TurnOrderTile> tiles = new ArrayList<>();

        // dto are iterated to create tiles
        for (TurnOrderTileDTO dto : dtoList) {

            // tile is created directly using lists from dto
            TurnOrderTile newTile = new TurnOrderTile(
                    dto.getId(),
                    dto.getRequiredPlayers(),
                    dto.getFoodModifiers(),
                    dto.getPrestigeModifiers()
            );

            // tile is added to the list
            tiles.add(newTile);
        }

        // list is returned
        return tiles;
    }

    /**
     * Creates a single turn order tile from a dto.
     *
     * @param dto the turn order tile dto
     * @return the created turn order tile
     */
    public TurnOrderTile createTile(TurnOrderTileDTO dto) {
        // dto is checked for nullability
        Objects.requireNonNull(dto, "dto cannot be null");

        // tile is created directly using lists from dto
        return new TurnOrderTile(
                dto.getId(),
                dto.getRequiredPlayers(),
                dto.getFoodModifiers(),
                dto.getPrestigeModifiers()
        );
    }
}
