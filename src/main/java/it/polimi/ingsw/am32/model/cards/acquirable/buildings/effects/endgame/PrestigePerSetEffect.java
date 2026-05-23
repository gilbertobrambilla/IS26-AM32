package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.endgame;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;

import java.util.Objects;

/**
 * Defines an endgame effect that awards prestige points for completed sets of characters.
 */
public class PrestigePerSetEffect implements BuildingCardEffect {
    private final int pointsPerSet;

    /**
     * Initializes the effect with the number of points per set.
     *
     * @param pointsPerSet the number of points awarded per completed set
     */
    public PrestigePerSetEffect(int pointsPerSet) {
        if (pointsPerSet < 0) {
            throw new IllegalArgumentException("pointsPerSet cannot be negative");
        }

        this.pointsPerSet = pointsPerSet;
    }

    /**
     * Triggers the endgame effect for the player.
     *
     * @param player the player receiving the prestige points
     * @param gameState the current state of the game
     */
    @Override
    public void onGameEnd(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "game state cannot be null");

        player.addEndGamePrestigePerSet(this.pointsPerSet);
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
