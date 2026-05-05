package it.polimi.ingsw.am32.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a simplified version of the turn order tile for the client side.
 */
public class LightTurnOrderTile {
    private final String tileId;
    private final List<String> players;

    /**
     * Constructs a new LightTurnOrderTile with the specified identifier and initial players.
     * @param tileId The unique identifier of the tile.
     * @param players The initial ordered list of player nicknames.
     */
    public LightTurnOrderTile(String tileId, List<String> players) {
        this.tileId = tileId;
        this.players = new ArrayList<>(players);
    }

    /**
     * Returns the unique identifier of the tile.
     * @return The tile id.
     */
    public String getTileId() {
        return tileId;
    }

    /**
     * Returns an unmodifiable list of player nicknames in turn order.
     * @return The ordered list of players.
     */
    public List<String> getPlayers() {
        return Collections.unmodifiableList(players);
    }
}
