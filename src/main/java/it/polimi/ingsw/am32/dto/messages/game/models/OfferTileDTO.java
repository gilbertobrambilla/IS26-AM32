package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;

public class OfferTileDTO implements Serializable {
    private final String id;
    private final String player;

    public OfferTileDTO(String tileId, String player) {
        this.id = tileId;
        this.player = player;
    }

    public String getId() {
        return id;
    }

    public String getPlayer() {
        return player;
    }
}
