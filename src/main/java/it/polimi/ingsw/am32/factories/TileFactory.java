package it.polimi.ingsw.am32.factories;

import java.util.List;

public interface TileFactory<D, T> {
    List<T> createTiles(List<D> dtoList);
}
