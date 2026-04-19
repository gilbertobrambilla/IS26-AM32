package it.polimi.ingsw.am32.model.factories;

import java.util.List;

public interface TileFactory<D, T> {
    List<T> createTiles(List<D> dtoList);
}
