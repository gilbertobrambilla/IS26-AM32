package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.events;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.cards.events.EventCard;
import it.polimi.ingsw.am32.enums.CharacterType;
import it.polimi.ingsw.am32.tiles.OfferTile;

import java.util.Objects;

public class SustenanceDiscountPerCharacterEffect implements BuildingCardEffect {
    private CharacterType targetCharacter;
    private int discountPerCharacter;

    public SustenanceDiscountPerCharacterEffect(CharacterType targetCharacter, int discountPerCharacter) {
        Objects.requireNonNull(targetCharacter, "targetCharacter cannot be null");
        if(discountPerCharacter < 0) {
            throw new IllegalArgumentException("discountPerCharacter cannot be negative");
        }

        this.targetCharacter = targetCharacter;
        this.discountPerCharacter = discountPerCharacter;
    }

    @Override
    public void onAcquire(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        // calculate initial discount based on characters already owned
        int initialCount = 0;
        switch (this.targetCharacter) {
            case ARTIST: initialCount = player.getArtistCount(); break;
            case INVENTOR: initialCount = player.getInventorCount(); break;
            case GATHERER: initialCount = player.getGathererCount(); break;
        }
        player.addSustenanceDiscount(initialCount * this.discountPerCharacter);
    }

    @Override
    public void onCharacterAdded(Player player, CharacterCard newCard) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(newCard, "newCard cannot be null");

        // dynamically increase discount when a matching character is acquired later
        if (newCard.getCharacterType() == this.targetCharacter) {
            player.addSustenanceDiscount(this.discountPerCharacter);
        }
    }

    @Override public void onEvent(EventCard event, Player player, GameState gameState) {}
    @Override public void onRoundEnd(Player player, GameState gameState) {}
    @Override public void onGameEnd(Player player, GameState gameState) {}
    @Override public void onTotemPlaced(OfferTile offerTile, Player player, GameState gameState) {}
}
