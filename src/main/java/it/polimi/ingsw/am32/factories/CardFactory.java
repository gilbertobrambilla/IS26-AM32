package it.polimi.ingsw.am32.factories;

import java.util.List;

public interface CardFactory<D, C> {
    List<C> createCards(List<D> dtoList);
}
