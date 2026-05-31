package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;

/**
 * A card data transfer object is represented.
 */
public class CardDTO implements Serializable {
    private final String id;

    /**
     * A card data transfer object is initialized with its card id.
     *
     * @param id the card id
     */
    public CardDTO(String id) {
        this.id = id;
    }

    /**
     * The card id is returned.
     *
     * @return the card id
     */
    public String getId() {
        return id;
    }
}
