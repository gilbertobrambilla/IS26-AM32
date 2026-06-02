package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;
import java.util.List;

/**
 * A turn order data transfer object is represented.
 */
public class TurnOrderDTO implements Serializable {
    private final String id;
    private final List<String> players;

    /**
     * A turn order data transfer object is initialized with tile and player order data.
     *
     * @param tileId the turn order tile id
     * @param players the ordered player nicknames
     */
    public TurnOrderDTO(String tileId, List<String> players) {
        this.id = tileId;
        this.players = List.copyOf(players);
    }

    /**
     * The turn order tile id is returned.
     *
     * @return the turn order tile id
     */
    public String getId() {
        return id;
    }

    /**
     * The ordered player nicknames are returned.
     *
     * @return the ordered player nicknames
     */
    public List<String> getPlayers() {
        return players;
    }
}
