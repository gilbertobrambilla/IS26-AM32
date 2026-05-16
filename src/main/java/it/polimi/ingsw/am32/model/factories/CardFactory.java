package it.polimi.ingsw.am32.model.factories;

import java.util.List;

/**
 * Interface for factories that create cards from a list of dto.
 *
 * @param <D> the dto type
 * @param <C> the card type
 */
public interface CardFactory<D, C> {

    /**
     * Creates a list of cards from a list of dto.
     *
     * @param dtoList the list of dto
     * @return the list of created cards
     */
    List<C> createCards(List<D> dtoList);
}
