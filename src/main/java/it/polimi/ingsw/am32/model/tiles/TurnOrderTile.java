package it.polimi.ingsw.am32.model.tiles;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.GameErrorReason;
import it.polimi.ingsw.am32.model.exceptions.InvalidGameActionException;
import it.polimi.ingsw.am32.model.receipts.TurnOrderPlacementReceipt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the turn order tile in the game.
 */
public class TurnOrderTile extends Tile {
    private final List<Player> turnOrder;
    private final List<Integer> foodModifiers;
    private final List<Integer> prestigeModifiers;

    /**
     * Initializes the turn order tile with modifiers.
     *
     * @param id the identifier of the tile
     * @param requiredPlayers the minimum number of players required
     * @param foodModifiers the list of food modifiers per position
     * @param prestigeModifiers the list of prestige modifiers per position
     */
    public TurnOrderTile(String id, int requiredPlayers, List<Integer> foodModifiers, List<Integer> prestigeModifiers) {
        super(id, requiredPlayers);

        Objects.requireNonNull(foodModifiers, "foodModifiers cannot be null");
        Objects.requireNonNull(prestigeModifiers, "prestigeModifiers cannot be null");

        this.turnOrder = new ArrayList<>();
        this.foodModifiers = foodModifiers;
        this.prestigeModifiers = prestigeModifiers;
    }

    /**
     * Initializes the players' order during the setup phase.
     *
     * @param playerList the initial list of players
     */
    public void initPlayersOrder(List<Player> playerList) {
        Objects.requireNonNull(playerList, "initialPlayers cannot be null");

        // capacity is checked to prevent exceeding limits during setup
        if (this.turnOrder.size() + playerList.size() > this.getRequiredPlayers()) {
            throw new IllegalStateException("turn order tile capacity exceeded during setup");
        }

        // players are added to the turn order without applying modifiers
        this.turnOrder.addAll(playerList);
    }

    /**
     * Places a player on the turn order tile and applies modifiers.
     *
     * @param player the player to place
     * @return the placement receipt
     */
    public TurnOrderPlacementReceipt placePlayer(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        if (isFull()) {
            // exception is thrown if the turn order tile is full
            throw new InvalidGameActionException(
                    GameErrorReason.TURN_ORDER_TILE_FULL,
                    "turn order tile is already occupied"
            );
        }

        // position index is determined
        int index = this.turnOrder.size();

        // player is added to the turn order
        this.turnOrder.add(player);

        // modifiers are evaluated based on the position index
        int foodMod = this.foodModifiers.get(index);
        int prestigeMod = this.prestigeModifiers.get(index);
        player.processTurnOrderModifiers(foodMod, prestigeMod);
        boolean gainedFood = (foodMod > 0);

        // player is notified of the placement to trigger relevant building effects
        player.onPlacedOnTurnOrderTile(gainedFood);

        return new TurnOrderPlacementReceipt(index, foodMod, prestigeMod);
    }

    /**
     * Removes a player from the turn order.
     *
     * @param player the player to remove
     */
    public void removePlayer(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        this.turnOrder.remove(player);
    }

    /**
     * Gets the current turn order of players.
     *
     * @return the list of players in order
     */
    public List<Player> getTurnOrder() {
        return new ArrayList<>(this.turnOrder);
    }

    /**
     * Checks if the turn order tile is filled.
     *
     * @return true if the tile is full, false otherwise
     */
    public boolean isFull() {
        return this.turnOrder.size() >= this.getRequiredPlayers();
    }
}
