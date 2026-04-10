package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.status;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCardEffect;

import java.util.Objects;

public class ExtraShamanStarsEffect implements BuildingCardEffect {
    private final int extraStars;

    public ExtraShamanStarsEffect(int extraStars) {
        if (extraStars < 0) {
            throw new IllegalArgumentException("extraStars cannot be negative");
        }

        this.extraStars = extraStars;
    }

    @Override
    public void onAcquire(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        player.addShamanStars(this.extraStars);
    }

    @Override public void onRoundEnd(Player player, GameState gameState) {}
    @Override public void onGameEnd(Player player, GameState gameState) {}
}
