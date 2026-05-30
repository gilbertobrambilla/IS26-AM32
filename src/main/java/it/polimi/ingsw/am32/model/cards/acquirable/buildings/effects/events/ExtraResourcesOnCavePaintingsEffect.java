package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.events;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.model.enums.CharacterType;

import java.util.Objects;

/**
 * Defines an effect that awards extra resources during a cave paintings event based on a target character type.
 */
public class ExtraResourcesOnCavePaintingsEffect implements BuildingCardEffect {
    private final CharacterType targetCharacter;
    private final int extraFood;
    private final int extraPrestige;

    /**
     * Initializes the effect with the target character type and the extra resources.
     *
     * @param targetCharacter the character type targeted by the effect
     * @param extraFood the extra food awarded per target character
     * @param extraPrestige the extra prestige awarded per target character
     */
    public ExtraResourcesOnCavePaintingsEffect(CharacterType targetCharacter, int extraFood, int extraPrestige) {
        Objects.requireNonNull(targetCharacter, "targetCharacter cannot be null");
        if (extraFood < 0) {
            throw new IllegalArgumentException("extraFood cannot be negative");
        }
        if (extraPrestige < 0) {
            throw new IllegalArgumentException("extraPrestige cannot be negative");
        }

        this.targetCharacter = targetCharacter;
        this.extraFood = extraFood;
        this.extraPrestige = extraPrestige;
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
     * Applies the effect during a cave paintings event.
     *
     * @param player the player involved in the event
     * @param gameState the current state of the game
     */
    @Override
    public void onCavePaintingsEvent(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        player.addResourcesPerCharacterType(this.targetCharacter, this.extraFood, this.extraPrestige);
    }
}
