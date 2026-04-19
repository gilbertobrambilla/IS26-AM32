package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.resources;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;

import java.util.Objects;

public class FoodPerSetEffect implements BuildingCardEffect {
    private final int extraFood;

    public FoodPerSetEffect(int extraFood) {
        if (extraFood < 0) {
            throw new IllegalArgumentException("extraFood cannot be negative");
        }

        this.extraFood = extraFood;
    }

    @Override
    public void onSetCompleted(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        // food is granted to the player for the completed set
        player.grantFoodForNewSet(this.extraFood);
    }

    @Override public void onAcquire(Player player, GameState gameState) {}
    @Override public void onRoundEnd(Player player, GameState gameState) {}
    @Override public void onGameEnd(Player player, GameState gameState) {}
}
