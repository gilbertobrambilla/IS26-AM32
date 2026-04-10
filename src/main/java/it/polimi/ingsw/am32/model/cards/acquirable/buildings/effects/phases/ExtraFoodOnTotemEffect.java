package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.phases;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;

import java.util.Objects;

public class ExtraFoodOnTotemEffect implements BuildingCardEffect {
    private final int extraFood;

    public ExtraFoodOnTotemEffect(int extraFood) {
        if (extraFood < 0) {
            throw new IllegalArgumentException("extraFood cannot be negative");
        }

        this.extraFood = extraFood;
    }

    @Override
    public void onTurnOrderPlaced(Player player, boolean gainedFood) {
        Objects.requireNonNull(player, "player cannot be null");

        if (gainedFood) {
            // one extra food is provided by the building if food was gained from the turn order tile space
            player.addFood(this.extraFood);
        }
    }

    @Override public void onAcquire(Player player, GameState gameState) {}
    @Override public void onRoundEnd(Player player, GameState gameState) {}
    @Override public void onGameEnd(Player player, GameState gameState) {}
}
