package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;

public class CardDTO implements Serializable {
    private final String id;

    public CardDTO(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
