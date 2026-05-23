package it.polimi.ingsw.am32.model.tiles;

import java.util.Objects;

/**
 * Represents a generic tile in the game.
 */
public abstract class Tile {
    private final String id;
    private final int requiredPlayers;

    /**
     * Initializes the tile with its identifier and player requirement.
     *
     * @param id the identifier of the tile
     * @param requiredPlayers the minimum number of players required
     */
    public Tile(String id, int requiredPlayers) {
        Objects.requireNonNull(id, "id cannot be null");
        if (requiredPlayers < 0) {
            throw new IllegalArgumentException("requiredPlayers cannot be negative");
        }

        this.id = id;
        this.requiredPlayers = requiredPlayers;
    }

    /**
     * Gets the identifier of the tile.
     *
     * @return the tile identifier
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the minimum number of players required for the tile.
     *
     * @return the required players
     */
    public int getRequiredPlayers() {
        return this.requiredPlayers;
    }
}
