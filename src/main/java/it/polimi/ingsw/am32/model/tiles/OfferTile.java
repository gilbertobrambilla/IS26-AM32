package it.polimi.ingsw.am32.model.tiles;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.GameErrorReason;
import it.polimi.ingsw.am32.model.exceptions.InvalidGameActionException;

import java.util.Objects;

/**
 * Represents an offer tile in the game.
 */
public class OfferTile extends Tile {
    private final Action action;
    private Player occupyingPlayer;

    /**
     * Initializes the offer tile with the required players and action.
     *
     * @param id the identifier of the tile
     * @param requiredPlayers the minimum number of players required
     * @param action the action associated with the tile
     */
    public OfferTile(String id, int requiredPlayers, Action action) {
        super(id, requiredPlayers);

        Objects.requireNonNull(action, "action cannot be null");

        this.action = action;
        this.occupyingPlayer = null;
    }

    /**
     * Gets the action associated with the tile.
     *
     * @return the tile action
     */
    public Action getAction() {
        return this.action;
    }

    /**
     * Gets the player currently occupying the tile.
     *
     * @return the occupying player, or null if free
     */
    public Player getOccupyingPlayer() {
        return this.occupyingPlayer;
    }

    /**
     * Places a player on the offer tile.
     *
     * @param player the player to place
     */
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

    /**
     * Removes the current player from the tile.
     */
    public void removePlayer() {
        this.occupyingPlayer = null;
    }

    /**
     * Checks if the tile is currently free.
     *
     * @return true if the tile is free, false otherwise
     */
    public boolean isFree() {
        return this.occupyingPlayer == null;
    }

    /**
     * Validates if the tile action allows the selected draws.
     *
     * @param requestedUpper the requested draws from the upper row
     * @param requestedLower the requested draws from the lower row
     * @param characterCardsUpper the available character cards in the upper row
     * @param characterCardsLower the available character cards in the lower row
     * @param affordableBuildingsUpper the affordable buildings in the upper row
     * @param affordableBuildingsLower the affordable buildings in the lower row
     * @return true if the selection is valid, false otherwise
     */
    public boolean isValidSelection(
            int requestedUpper, int requestedLower,
            int characterCardsUpper, int characterCardsLower,
            int affordableBuildingsUpper, int affordableBuildingsLower) {
        return this.action.isValidSelection(requestedUpper, requestedLower,
                characterCardsUpper, characterCardsLower,
                affordableBuildingsUpper, affordableBuildingsLower);
    }

    /**
     * Checks if the tile is currently occupied by the specified player.
     *
     * @param player the player to check
     * @return true if the player occupies this tile, false otherwise
     */
    public boolean isOccupiedBy(Player player) {
        return !isFree() && this.occupyingPlayer.equals(player);
    }
}
