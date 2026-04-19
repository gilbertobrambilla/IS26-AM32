package it.polimi.ingsw.am32.model.factories;

import java.util.List;

public interface CardFactory<D, C> {
    List<C> createCards(List<D> dtoList);
}
