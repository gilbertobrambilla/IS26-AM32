package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.events;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.enums.CharacterType;

import java.util.Objects;

public class ExtraResourcesOnCavePaintingsEffect implements BuildingCardEffect {
    private final CharacterType targetCharacter;
    private final int extraFood;
    private final int extraPrestige;

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

    @Override
    public void onCavePaintingsEvent(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        player.addResourcesPerCharacterType(this.targetCharacter, this.extraFood, this.extraPrestige);
    }

    @Override public void onAcquire(Player player, GameState gameState) {}
    @Override public void onRoundEnd(Player player, GameState gameState) {}
    @Override public void onGameEnd(Player player, GameState gameState) {}
}
