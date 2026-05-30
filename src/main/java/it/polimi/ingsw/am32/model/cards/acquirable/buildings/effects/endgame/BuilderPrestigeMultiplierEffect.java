package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.endgame;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;

import java.util.Objects;

/**
 * Defines an endgame effect that adds a prestige multiplier based on builders.
 */
public class BuilderPrestigeMultiplierEffect implements BuildingCardEffect {
    private final int multiplier;

    /**
     * Initializes the effect with the given multiplier.
     *
     * @param multiplier the value of the prestige multiplier
     */
    public BuilderPrestigeMultiplierEffect(int multiplier) {
        if (multiplier < 1) {
            throw new IllegalArgumentException("multiplier cannot be less than 1");
        }

        this.multiplier = multiplier;
    }

    /**
     * Triggers the endgame effect for the player.
     *
     * @param player the player receiving the multiplier
     * @param gameState the current state of the game
     */
    @Override
    public void onGameEnd(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        player.addEndGameBuilderPrestigeMultiplier(this.multiplier);
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
}
