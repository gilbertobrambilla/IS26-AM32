package it.polimi.ingsw.am32.model.tiles;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.GameErrorReason;
import it.polimi.ingsw.am32.model.exceptions.InvalidGameActionException;

import java.util.Objects;

public class OfferTile extends Tile{
    private final Action action;
    private Player occupyingPlayer;

    public OfferTile(String id, int requiredPlayers, Action action) {
        super(id, requiredPlayers);

        Objects.requireNonNull(action, "action cannot be null");

        this.action = action;
        this.occupyingPlayer = null;
    }

    public Action getAction() {
        return this.action;
    }

    public Player getOccupyingPlayer() {
        return this.occupyingPlayer;
    }

    public void placePlayer(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        if (!isFree()) {
            // exception is thrown if the tile is already occupied
            throw new InvalidGameActionException(
                    GameErrorReason.TILE_ALREADY_OCCUPIED,
                    "cannot place player: offer tile is already occupied"
            );
        }
        this.occupyingPlayer = player;
    }

    public void removePlayer() {
        this.occupyingPlayer = null;
    }

    public boolean isFree() {
        return this.occupyingPlayer == null;
    }

    public boolean isValidSelection(int requestedUpper, int requestedLower, int availableUpper, int availableLower) {
        return this.action.isValidSelection(requestedUpper, requestedLower, availableUpper, availableLower);
    }

    /**
     * Checks if the tile is currently occupied by the specified player.
     * @param player the player to check
     * @return true if the player occupies this tile, false otherwise
     */
    public boolean isOccupiedBy(Player player) {
        return !isFree() && this.occupyingPlayer.equals(player);
    }
}
