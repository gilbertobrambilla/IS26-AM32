package it.polimi.ingsw.am32.tiles;

import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.enums.InvalidActionReason;
import it.polimi.ingsw.am32.exceptions.InvalidActionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TurnOrderTile extends Tile{
    private final List<Player> turnOrder;
    private final List<Integer> foodModifiers;
    private final List<Integer> prestigeModifiers;

    public TurnOrderTile(String id, int requiredPlayers, List<Integer> foodModifiers, List<Integer> prestigeModifiers) {
        super(id, requiredPlayers);

        Objects.requireNonNull(foodModifiers, "foodModifiers cannot be null");
        Objects.requireNonNull(prestigeModifiers, "prestigeModifiers cannot be null");

        this.turnOrder = new ArrayList<>();
        this.foodModifiers = foodModifiers;
        this.prestigeModifiers = prestigeModifiers;
    }

    public void initPlayersOrder(List<Player> playerList) {
        Objects.requireNonNull(playerList, "initialPlayers cannot be null");

        // players are added to the turn order without applying modifiers
        // intended only for the initial setup phase
        if (this.turnOrder.size() + playerList.size() > this.getRequiredPlayers()) {
            throw new IllegalStateException("turn order tile capacity exceeded during setup");
        }

        this.turnOrder.addAll(playerList);
    }

    public void placePlayer(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        if (isFull()) {
            // exception is thrown if the turn order tile is full
            throw new InvalidActionException(
                    InvalidActionReason.TURN_ORDER_TILE_FULL,
                    "turn order tile is already occupied"
            );
        }

        // position index is determined
        int index = this.turnOrder.size();

        // player is added to the turn order
        this.turnOrder.add(player);

        // modifiers are evaluated based on the position index
        int foodMod = this.foodModifiers.get(index);

        if (foodMod > 0) {
            // positive food modifier is applied
            player.addFood(foodMod);
        } else if (foodMod < 0) {
            // negative food modifier is processed
            int cost = Math.abs(foodMod);
            if (player.hasEnoughFood(cost)) {
                // food is paid if available
                player.removeFood(cost);
            } else {
                // prestige penalty is applied if food is insufficient
                int prestigeMod = this.prestigeModifiers.get(index);
                if (prestigeMod < 0) {
                    player.removePrestige(Math.abs(prestigeMod));
                }
            }
        }
    }

    public void removePlayer(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        this.turnOrder.remove(player);
    }

    public List<Player> getTurnOrder() {
        return new ArrayList<>(this.turnOrder);
    }

    public boolean isFull() {
        return this.turnOrder.size() >= this.getRequiredPlayers();
    }
}
