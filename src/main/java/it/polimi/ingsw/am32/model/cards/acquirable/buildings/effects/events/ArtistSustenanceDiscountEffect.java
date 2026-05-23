package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.events;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.model.enums.CharacterType;

import java.util.Objects;

/**
 * Defines an event effect that applies a sustenance discount for artists.
 */
public class ArtistSustenanceDiscountEffect implements BuildingCardEffect {
    private final int discountPerCharacter;

    /**
     * Initializes the effect with the specified discount per character.
     *
     * @param discountPerCharacter the discount amount per character
     */
    public ArtistSustenanceDiscountEffect(int discountPerCharacter) {
        if (discountPerCharacter < 0) {
            throw new IllegalArgumentException("discountPerCharacter cannot be negative");
        }
        this.discountPerCharacter = discountPerCharacter;
    }

    /**
     * Triggers the endgame effect for the player.
     *
     * @param player the player owning the building
     * @param gameState the current state of the game
     */
    @Override
    public void onGameEnd(Player player, GameState gameState) {
    }

    /**
     * Triggers the effect when an artist is added.
     *
     * @param player the player adding the character
     */
    @Override
    public void onArtistAdded(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        player.addSustenanceDiscount(this.discountPerCharacter);
    }

    /**
     * Applies the effect upon acquiring the building.
     *
     * @param player the player acquiring the building
     * @param gameState the current state of the game
     */
    @Override
    public void onAcquire(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        // discount is applied retroactively for already present artists
        player.addSustenanceDiscountPerCharacterType(CharacterType.ARTIST, this.discountPerCharacter);
    }

    /**
     * Applies the effect at the end of a round.
     *
     * @param player the player owning the building
     * @param gameState the current state of the game
     */
    @Override
    public void onRoundEnd(Player player, GameState gameState) {
    }
}
