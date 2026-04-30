package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;
import java.util.List;

public class TurnOrderDTO implements Serializable {
    private final String id;
    private final List<String> players;

    public TurnOrderDTO(String tileId, List<String> players) {
        this.id = tileId;
        this.players = players;
    }

    public String getId() {
        return id;
    }

    public List<String> getPlayers() {
        return players;
    }
}
