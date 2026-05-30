package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.phases;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;

import java.util.Objects;

/**
 * Defines a phase effect that provides extra food when a turn order is placed.
 */
public class ExtraFoodOnTotemEffect implements BuildingCardEffect {
    private final int extraFood;

    /**
     * Initializes the effect with the specified amount of extra food.
     *
     * @param extraFood the amount of extra food provided
     */
    public ExtraFoodOnTotemEffect(int extraFood) {
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
     * Applies the effect when a turn order is placed.
     *
     * @param player the player placing the turn order
     * @param gainedFood true if food was gained, false otherwise
     */
    @Override
    public void onTurnOrderPlaced(Player player, boolean gainedFood) {
        Objects.requireNonNull(player, "player cannot be null");

        if (gainedFood) {
            // the building provides one extra food if food was gained from the turn order tile space
            player.addFood(this.extraFood);
        }
    }
}
