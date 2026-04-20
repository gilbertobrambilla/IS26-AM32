package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;

public class OfferTileDTO implements Serializable {
    private final String tileId;
    private final String occupyingPlayer;

    public OfferTileDTO(String tileId, String occupyingPlayer) {
        this.tileId = tileId;
        this.occupyingPlayer = occupyingPlayer;
    }

    public String getTileId() {
        return this.tileId;
    }

    public String getOccupyingPlayer() {
        return this.occupyingPlayer;
    }
}
