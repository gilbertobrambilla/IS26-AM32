package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;

public class OfferTileDTO implements Serializable {
    private final String tileId;
    private final String player;

    public OfferTileDTO(String tileId, String player) {
        this.tileId = tileId;
        this.player = player;
    }

    public String getTileId() {
        return tileId;
    }

    public String getPlayer() {
        return player;
    }
}
