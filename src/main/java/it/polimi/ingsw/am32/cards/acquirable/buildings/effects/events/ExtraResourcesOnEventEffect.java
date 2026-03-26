package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.events;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.cards.events.EventCard;
import it.polimi.ingsw.am32.enums.CharacterType;
import it.polimi.ingsw.am32.enums.EventType;
import it.polimi.ingsw.am32.tiles.OfferTile;

import java.util.Objects;

public class ExtraResourcesOnEventEffect implements BuildingCardEffect {
    private EventType targetEvent;
    private CharacterType targetCharacter;
    private int extraFood;
    private int extraPrestige;

    public ExtraResourcesOnEventEffect(EventType targetEvent, CharacterType targetCharacter, int extraFood, int extraPrestige) {
        Objects.requireNonNull(targetEvent, "targetEvent cannot be null");
        Objects.requireNonNull(targetCharacter, "targetCharacter cannot be null");
        if(extraFood < 0) {
            throw new IllegalArgumentException("extraFood cannot be negative");
        }
        if(extraPrestige < 0) {
            throw new IllegalArgumentException("extraPrestige cannot be negative");
        }

        this.targetEvent = targetEvent;
        this.targetCharacter = targetCharacter;
        this.extraFood = extraFood;
        this.extraPrestige = extraPrestige;
    }

    @Override
    public void onEvent(EventCard event, Player player, GameState gameState) {
        Objects.requireNonNull(event, "event cannot be null");
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        // trigger effect only if the current event matches the building requirement
        if (event.getEventType() == this.targetEvent) {
            int count = 0;
            switch (this.targetCharacter) {
                case ARTIST: count = player.getArtistCount(); break;
                case HUNTER: count = player.getHunterCount(); break;
                // add others if needed in the future
            }

            player.addFood(count * this.extraFood);
            player.addPrestige(count * this.extraPrestige);
        }
    }

    @Override public void onAcquire(Player player, GameState gameState) {}
    @Override public void onRoundEnd(Player player, GameState gameState) {}
    @Override public void onGameEnd(Player player, GameState gameState) {}
    @Override public void onTotemPlaced(OfferTile offerTile, Player player, GameState gameState) {}
    @Override public void onCharacterAdded(Player player, CharacterCard newCard) {}
}
