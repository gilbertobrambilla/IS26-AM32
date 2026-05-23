package it.polimi.ingsw.am32.model.cards.acquirable.buildings;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.CardVisitor;
import it.polimi.ingsw.am32.model.cards.acquirable.AcquirableCard;

import java.util.Objects;

/**
 * Represents a building card in the game.
 */
public class BuildingCard extends AcquirableCard {
    private final int cost;
    private final BuildingCardEffect effect;

    /**
     * Initializes the building card with its attributes.
     *
     * @param id the unique identifier of the card
     * @param era the era the card belongs to
     * @param basePrestige the base prestige value of the card
     * @param cost the resource cost to acquire the building
     * @param effect the specific effects associated with the building
     */
    public BuildingCard(String id, int era, int basePrestige, int cost, BuildingCardEffect effect) {
        super(id, era, basePrestige);

        if (cost < 0) {
            throw new IllegalArgumentException("cost cannot be negative");
        }

        Objects.requireNonNull(effect, "effect cannot be null");

        this.cost = cost;
        this.effect = effect;
    }

    /**
     * Accepts a visitor to perform operations on the card.
     *
     * @param visitor the visitor to accept
     */
    @Override
    public void accept(CardVisitor visitor) {
        Objects.requireNonNull(visitor, "visitor cannot be null");

        visitor.visit(this);
    }

    /**
     * Gets the cost of the building.
     *
     * @return the building cost
     */
    public int getCost() {
        return this.cost;
    }

    /**
     * Gets the discounted cost based on a player's discount.
     *
     * @param playerDiscount the discount available to the player
     * @return the discounted cost
     */
    public int getDiscountedCost(int playerDiscount) {
        return Math.max(0, this.cost - playerDiscount);
    }

    /**
     * Gets the effect associated with the building.
     *
     * @return the building effect
     */
    public BuildingCardEffect getEffect() {
        return this.effect;
    }

    /**
     * Triggers the endgame effects for the player.
     *
     * @param player the player owning the building
     * @param gameState the current state of the game
     */
    public void onGameEnd(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "game state cannot be null");

        // base prestige is added to the player
        player.addPrestige(this.getBasePrestige());

        // building endgame effect is triggered
        this.effect.onGameEnd(player, gameState);
    }

    /**
     * Triggers the effect when an artist is added.
     *
     * @param player the player adding the character
     */
    public void onArtistAdded(Player player) {
        this.effect.onArtistAdded(player);
    }

    /**
     * Triggers the effect when a builder is added.
     *
     * @param player the player adding the character
     */
    public void onBuilderAdded(Player player) {
        this.effect.onBuilderAdded(player);
    }

    /**
     * Triggers the effect when a gatherer is added.
     *
     * @param player the player adding the character
     */
    public void onGathererAdded(Player player) {
        this.effect.onGathererAdded(player);
    }

    /**
     * Triggers the effect when a hunter is added.
     *
     * @param player the player adding the character
     */
    public void onHunterAdded(Player player) {
        this.effect.onHunterAdded(player);
    }

    /**
     * Triggers the effect when an inventor is added.
     *
     * @param player the player adding the character
     */
    public void onInventorAdded(Player player) {
        this.effect.onInventorAdded(player);
    }

    /**
     * Triggers the effect when a shaman is added.
     *
     * @param player the player adding the character
     */
    public void onShamanAdded(Player player) {
        this.effect.onShamanAdded(player);
    }

    /**
     * Applies the effect of acquiring the building.
     *
     * @param player the player acquiring the building
     * @param gameState the current state of the game
     */
    public void applyAcquireEffect(Player player, GameState gameState) {
        this.effect.onAcquire(player, gameState);
    }

    /**
     * Applies the effect at the end of a round.
     *
     * @param player the player owning the building
     * @param gameState the current state of the game
     */
    public void applyRoundEnd(Player player, GameState gameState) {
        this.effect.onRoundEnd(player, gameState);
    }

    /**
     * Applies the effect when turn order is placed.
     *
     * @param player the player placing the turn order
     * @param gainedFood true if food was gained, false otherwise
     */
    public void applyTurnOrderPlaced(Player player, boolean gainedFood) {
        this.effect.onTurnOrderPlaced(player, gainedFood);
    }

    /**
     * Applies the effect when a set is completed.
     *
     * @param player the player completing the set
     */
    public void applySetCompleted(Player player) {
        this.effect.onSetCompleted(player);
    }

    /**
     * Applies the effect when an inventor pair is completed.
     *
     * @param player the player completing the pair
     */
    public void applyInventorPairCompleted(Player player) {
        this.effect.onInventorPairCompleted(player);
    }

    /**
     * Applies the effect during a hunt event.
     *
     * @param player the player involved in the event
     * @param gameState the current state of the game
     */
    public void applyHuntEvent(Player player, GameState gameState) {
        this.effect.onHuntEvent(player, gameState);
    }

    /**
     * Applies the effect during a sustenance event.
     *
     * @param player the player involved in the event
     * @param gameState the current state of the game
     */
    public void applySustenanceEvent(Player player, GameState gameState) {
        this.effect.onSustenanceEvent(player, gameState);
    }

    /**
     * Applies the effect during a cave paintings event.
     *
     * @param player the player involved in the event
     * @param gameState the current state of the game
     */
    public void applyCavePaintingsEvent(Player player, GameState gameState) {
        this.effect.onCavePaintingsEvent(player, gameState);
    }

    /**
     * Applies the effect during a shamanic ritual event.
     *
     * @param player the player involved in the event
     * @param gameState the current state of the game
     */
    public void applyShamanicRitualEvent(Player player, GameState gameState) {
        this.effect.onShamanicRitualEvent(player, gameState);
    }
}
