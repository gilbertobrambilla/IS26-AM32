package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.events;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.enums.CharacterType;

import java.util.Objects;

public class ArtistSustenanceDiscountEffect implements BuildingCardEffect {
    private final int discountPerCharacter;

    public ArtistSustenanceDiscountEffect(int discountPerCharacter) {
        if (discountPerCharacter < 0) {
            throw new IllegalArgumentException("discountPerCharacter cannot be negative");
        }
        this.discountPerCharacter = discountPerCharacter;
    }

    @Override
    public void onAcquire(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        // discount is applied retroactively for already present artists
        player.addSustenanceDiscountPerCharacterType(CharacterType.ARTIST, this.discountPerCharacter);
    }

    @Override
    public void onArtistAdded(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        // effect is triggered only when an artist is added, without any conditions
        player.addSustenanceDiscount(this.discountPerCharacter);
    }

    @Override public void onRoundEnd(Player player, GameState gameState) {}
    @Override public void onGameEnd(Player player, GameState gameState) {}
}
