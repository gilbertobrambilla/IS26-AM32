package it.polimi.ingsw.am32.model.cards.acquirable.buildings;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.CardVisitor;
import it.polimi.ingsw.am32.model.cards.acquirable.AcquirableCard;

import java.util.Objects;

public class BuildingCard extends AcquirableCard {
    private final int cost;
    private final BuildingCardEffect effect;

    public BuildingCard(String id, int era, int basePrestige, int cost, BuildingCardEffect effect) {
        super(id, era, basePrestige);

        if (cost < 0) {
            throw new IllegalArgumentException("cost cannot be negative");
        }
        Objects.requireNonNull(effect, "effect cannot be null");

        this.cost = cost;
        this.effect = effect;
    }

    @Override
    public void accept(CardVisitor visitor) {
        Objects.requireNonNull(visitor, "visitor cannot be null");

        visitor.visit(this);
    }

    public int getCost() {
        return cost;
    }

    public int getDiscountedCost(int playerDiscount) {
        return Math.max(0, this.cost - playerDiscount);
    }

    public BuildingCardEffect getEffect() {
        return this.effect;
    }

    public void onGameEnd(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "game state cannot be null");

        // base prestige is added to the player
        player.addPrestige(this.getBasePrestige());

        // building endgame effect is triggered
        this.effect.onGameEnd(player, gameState);
    }


    public void onArtistAdded(Player player) {
        this.effect.onArtistAdded(player);
    }
    public void onBuilderAdded(Player player) {
        this.effect.onBuilderAdded(player);
    }
    public void onGathererAdded(Player player) {
        this.effect.onGathererAdded(player);
    }
    public void onHunterAdded(Player player) {
        this.effect.onHunterAdded(player);
    }
    public void onInventorAdded(Player player) {
        this.effect.onInventorAdded(player);
    }
    public void onShamanAdded(Player player) {
        this.effect.onShamanAdded(player);
    }


    public void applyAcquireEffect(Player player, GameState gameState) {
        this.effect.onAcquire(player, gameState);
    }
    public void applyRoundEnd(Player player, GameState gameState) {
        this.effect.onRoundEnd(player, gameState);
    }
    public void applyTurnOrderPlaced(Player player, boolean gainedFood) {
        this.effect.onTurnOrderPlaced(player, gainedFood);
    }
    public void applySetCompleted(Player player) {
        this.effect.onSetCompleted(player);
    }
    public void applyInventorPairCompleted(Player player) {
        this.effect.onInventorPairCompleted(player);
    }
    public void applyHuntEvent(Player player, GameState gameState) {
        this.effect.onHuntEvent(player, gameState);
    }
    public void applySustenanceEvent(Player player, GameState gameState) {
        this.effect.onSustenanceEvent(player, gameState);
    }
    public void applyCavePaintingsEvent(Player player, GameState gameState) {
        this.effect.onCavePaintingsEvent(player, gameState);
    }
    public void applyShamanicRitualEvent(Player player, GameState gameState) {
        this.effect.onShamanicRitualEvent(player, gameState);
    }
}
