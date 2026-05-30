package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.resources;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;

import java.util.Objects;

/**
 * Defines a resource effect that awards extra food for each completed set of characters.
 */
public class FoodPerSetEffect implements BuildingCardEffect {
    private final int extraFood;

    /**
     * Initializes the effect with the specified amount of extra food.
     *
     * @param extraFood the amount of extra food awarded
     */
    public FoodPerSetEffect(int extraFood) {
        if (extraFood < 0) {
            throw new IllegalArgumentException("extraFood cannot be negative");
        }

        this.extraFood = extraFood;
    }

    /**
     * Triggers the endgame effect for the player.
     *
     * @param player the player owning the building
     * @param gameState the current state of the game
     */
    @Override
    public void onGameEnd(Player player, GameState gameState) {
    }

    /**
     * Applies the effect upon acquiring the building.
     *
     * @param player the player acquiring the building
     * @param gameState the current state of the game
     */
    @Override
    public void onAcquire(Player player, GameState gameState) {
    }

    /**
     * Applies the effect at the end of a round.
     *
     * @param player the player owning the building
     * @param gameState the current state of the game
     */
    @Override
    public void onRoundEnd(Player player, GameState gameState) {
    }

    /**
     * Applies the effect when a set is completed.
     *
     * @param player the player completing the set
     */
    @Override
    public void onSetCompleted(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        // food is granted to the player for the completed set
        player.grantFoodForNewSet(this.extraFood);
    }
}
