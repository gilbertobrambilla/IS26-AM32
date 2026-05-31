package it.polimi.ingsw.am32.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simplified version of the turn order tile is represented for the client side.
 */
public class LightTurnOrderTile {
    private final String tileId;
    private final List<String> players;

    /**
     * A new light turn order tile is constructed with the specified identifier and initial players.
     *
     * @param tileId the unique identifier of the tile
     * @param players the initially ordered list of player nicknames
     */
    public LightTurnOrderTile(String tileId, List<String> players) {
        this.tileId = tileId;
        this.players = new ArrayList<>(players);
    }

    /**
     * The unique identifier of the tile is returned.
     *
     * @return the tile id
     */
    public String getTileId() {
        return tileId;
    }

    /**
     * An unmodifiable list of player nicknames in turn order is returned.
     *
     * @return the ordered list of players
     */
    public List<String> getPlayers() {
        return Collections.unmodifiableList(players);
    }
}
