package it.polimi.ingsw.am32.model.factories;

import java.util.List;

/**
 * Interface for factories that create tiles from a list of dto.
 *
 * @param <D> the dto type
 * @param <T> the tile type
 */
public interface TileFactory<D, T> {

    /**
     * Creates a list of tiles from a list of dto.
     *
     * @param dtoList the list of dto
     * @return the list of created tiles
     */
    List<T> createTiles(List<D> dtoList);
}
