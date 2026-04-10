package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.endgame;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;

import java.util.Objects;

public class PrestigePerSetEffect implements BuildingCardEffect {
    private final int pointsPerSet;

    public PrestigePerSetEffect(int pointsPerSet) {
        if (pointsPerSet < 0) {
            throw new IllegalArgumentException("pointsPerSet cannot be negative");
        }

        this.pointsPerSet = pointsPerSet;
    }

    @Override
    public void onGameEnd(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "game state cannot be null");

        player.addEndGamePrestigePerSet(this.pointsPerSet);
    }

    @Override public void onAcquire(Player player, GameState gameState) {}
    @Override public void onRoundEnd(Player player, GameState gameState) {}
}
