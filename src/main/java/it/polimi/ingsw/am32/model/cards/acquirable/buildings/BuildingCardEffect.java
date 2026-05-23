package it.polimi.ingsw.am32.model.cards.acquirable.buildings;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;

/**
 * Defines the effects applied by a building card.
 */
public interface BuildingCardEffect {

    /**
     * Applies the effect when the building is acquired.
     *
     * @param player the player acquiring the building
     * @param gameState the current state of the game
     */
    void onAcquire(Player player, GameState gameState);

    /**
     * Applies the effect at the end of a round.
     *
     * @param player the player owning the building
     * @param gameState the current state of the game
     */
    void onRoundEnd(Player player, GameState gameState);

    /**
     * Applies the effect at the end of the game.
     *
     * @param player the player owning the building
     * @param gameState the current state of the game
     */
    void onGameEnd(Player player, GameState gameState);

    /**
     * Applies the effect when an artist is added.
     *
     * @param player the player adding the character
     */
    default void onArtistAdded(Player player) {
    }

    /**
     * Applies the effect when a builder is added.
     *
     * @param player the player adding the character
     */
    default void onBuilderAdded(Player player) {
    }

    /**
     * Applies the effect when a gatherer is added.
     *
     * @param player the player adding the character
     */
    default void onGathererAdded(Player player) {
    }

    /**
     * Applies the effect when a hunter is added.
     *
     * @param player the player adding the character
     */
    default void onHunterAdded(Player player) {
    }

    /**
     * Applies the effect when an inventor is added.
     *
     * @param player the player adding the character
     */
    default void onInventorAdded(Player player) {
    }

    /**
     * Applies the effect when a shaman is added.
     *
     * @param player the player adding the character
     */
    default void onShamanAdded(Player player) {
    }

    /**
     * Applies the effect during a hunt event.
     *
     * @param player the player involved in the event
     * @param gameState the current state of the game
     */
    default void onHuntEvent(Player player, GameState gameState) {
    }

    /**
     * Applies the effect during a sustenance event.
     *
     * @param player the player involved in the event
     * @param gameState the current state of the game
     */
    default void onSustenanceEvent(Player player, GameState gameState) {
    }

    /**
     * Applies the effect during a cave paintings event.
     *
     * @param player the player involved in the event
     * @param gameState the current state of the game
     */
    default void onCavePaintingsEvent(Player player, GameState gameState) {
    }

    /**
     * Applies the effect during a shamanic ritual event.
     *
     * @param player the player involved in the event
     * @param gameState the current state of the game
     */
    default void onShamanicRitualEvent(Player player, GameState gameState) {
    }

    /**
     * Applies the effect when a set is completed.
     *
     * @param player the player completing the set
     */
    default void onSetCompleted(Player player) {
    }

    /**
     * Applies the effect when an inventor pair is completed.
     *
     * @param player the player completing the pair
     */
    default void onInventorPairCompleted(Player player) {
    }

    /**
     * Applies the effect when turn order is placed.
     *
     * @param player the player placing the turn order
     * @param gainedFood true if food was gained, false otherwise
     */
    default void onTurnOrderPlaced(Player player, boolean gainedFood) {
    }
}
