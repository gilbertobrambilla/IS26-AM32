package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.endgame;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.model.enums.CharacterType;

import java.util.Objects;

/**
 * Defines an endgame effect that awards prestige points for a specific character type.
 */
public class PrestigePerCharacterEffect implements BuildingCardEffect {
    private final CharacterType targetType;
    private final int pointsPerCharacter;

    /**
     * Initializes the effect with the target character type and the points per character.
     *
     * @param targetType the type of character targeted by the effect
     * @param pointsPerCharacter the number of points awarded per character
     */
    public PrestigePerCharacterEffect(CharacterType targetType, int pointsPerCharacter) {
        Objects.requireNonNull(targetType, "targetType cannot be null");
        if (pointsPerCharacter < 0) {
            throw new IllegalArgumentException("pointsPerCharacter cannot be negative");
        }

        this.targetType = targetType;
        this.pointsPerCharacter = pointsPerCharacter;
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
        Objects.requireNonNull(gameState, "gameState cannot be null");

        player.addEndGamePrestigePerCharacterType(this.targetType, this.pointsPerCharacter);
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
