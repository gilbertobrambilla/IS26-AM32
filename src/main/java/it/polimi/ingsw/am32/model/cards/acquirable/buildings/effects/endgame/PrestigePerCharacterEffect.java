package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.endgame;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.model.enums.CharacterType;

import java.util.Objects;

public class PrestigePerCharacterEffect implements BuildingCardEffect {
    private final CharacterType targetType;
    private final int pointsPerCharacter;

    public PrestigePerCharacterEffect(CharacterType targetType, int pointsPerCharacter) {
        Objects.requireNonNull(targetType, "targetType cannot be null");
        if (pointsPerCharacter < 0) {
            throw new IllegalArgumentException("pointsPerCharacter cannot be negative");
        }

        this.targetType = targetType;
        this.pointsPerCharacter = pointsPerCharacter;
    }

    @Override
    public void onGameEnd(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        player.addEndGamePrestigePerCharacterType(this.targetType, this.pointsPerCharacter);
    }

    @Override public void onAcquire(Player player, GameState gameState) {}
    @Override public void onRoundEnd(Player player, GameState gameState) {}
}
