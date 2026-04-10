package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.endgame;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCardEffect;

import java.util.Objects;

public class BuilderPrestigeMultiplierEffect implements BuildingCardEffect {
    private final int multiplier;

    public BuilderPrestigeMultiplierEffect(int multiplier) {
        if (multiplier < 1) {
            throw new IllegalArgumentException("multiplier cannot be less than 1");
        }

        this.multiplier = multiplier;
    }

    @Override
    public void onGameEnd(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        player.addEndGameBuilderPrestigeMultiplier(this.multiplier);
    }

    @Override public void onAcquire(Player player, GameState gameState) {}
    @Override public void onRoundEnd(Player player, GameState gameState) {}
}
