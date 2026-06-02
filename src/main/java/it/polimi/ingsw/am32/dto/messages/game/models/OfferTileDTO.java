package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;

/**
 * An offer tile data transfer object is represented.
 */
public class OfferTileDTO implements Serializable {
    private final String id;
    private final String player;

    /**
     * An offer tile data transfer object is initialized with tile and player data.
     *
     * @param tileId the offer tile id
     * @param player the nickname of the player placed on the tile
     */
    public OfferTileDTO(String tileId, String player) {
        this.id = tileId;
        this.player = player;
    }

    /**
     * The offer tile id is returned.
     *
     * @return the offer tile id
     */
    public String getId() {
        return id;
    }

    /**
     * The player nickname is returned.
     *
     * @return the player nickname
     */
    public String getPlayer() {
        return player;
    }
}
