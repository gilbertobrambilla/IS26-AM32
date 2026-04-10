package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.events;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.enums.CharacterType;

import java.util.Objects;

public class GathererSustenanceDiscountEffect implements BuildingCardEffect {
    private final int discountPerCharacter;

    public GathererSustenanceDiscountEffect(int discountPerCharacter) {
        if (discountPerCharacter < 0) {
            throw new IllegalArgumentException("discountPerCharacter cannot be negative");
        }
        this.discountPerCharacter = discountPerCharacter;
    }

    @Override
    public void onAcquire(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        player.addSustenanceDiscountPerCharacterType(CharacterType.GATHERER, this.discountPerCharacter);
    }

    @Override
    public void onGathererAdded(Player player) {
        Objects.requireNonNull(player, "player cannot be null");
        player.addSustenanceDiscount(this.discountPerCharacter);
    }

    @Override public void onRoundEnd(Player player, GameState gameState) {}
    @Override public void onGameEnd(Player player, GameState gameState) {}
}
