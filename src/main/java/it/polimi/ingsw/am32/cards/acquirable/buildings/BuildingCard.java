package it.polimi.ingsw.am32.cards.acquirable.buildings;

import it.polimi.ingsw.am32.cards.acquirable.AcquirableCard;

import java.util.Objects;

public class BuildingCard extends AcquirableCard {
    private int cost;
    private BuildingCardEffect effect;

    public BuildingCard(String id, int era, int basePrestige, int cost, BuildingCardEffect effect) {
        super(id, era, basePrestige);

        if (cost < 0) {
            throw new IllegalArgumentException("cost cannot be negative");
        }
        Objects.requireNonNull(effect, "effect cannot be null");

        this.cost = cost;
        this.effect = effect;
    }

    public int getCost() {
        return this.cost;
    }

    public BuildingCardEffect getEffect() {
        return this.effect;
    }
}
