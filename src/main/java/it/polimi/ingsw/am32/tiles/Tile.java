package it.polimi.ingsw.am32.tiles;

import java.util.Objects;

public abstract class Tile {
    private final String id;
    private final int requiredPlayers;

    public Tile(String id, int requiredPlayers) {
        Objects.requireNonNull(id, "id cannot be null");
        if (requiredPlayers < 0) {
            throw new IllegalArgumentException("requiredPlayers cannot be negative");
        }

        this.id = id;
        this.requiredPlayers = requiredPlayers;
    }

    public String getId() {
        return this.id;
    }

    public int getRequiredPlayers() {
        return this.requiredPlayers;
    }
}
