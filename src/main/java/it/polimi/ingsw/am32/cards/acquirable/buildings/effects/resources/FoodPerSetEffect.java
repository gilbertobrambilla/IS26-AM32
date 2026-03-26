package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.resources;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.cards.events.EventCard;
import it.polimi.ingsw.am32.tiles.OfferTile;

import java.util.Objects;

public class FoodPerSetEffect implements BuildingCardEffect {
    private int extraFood;

    public FoodPerSetEffect(int extraFood) {
        if(extraFood < 0) {
            throw new IllegalArgumentException("extraFood cannot be negative");
        }

        this.extraFood = extraFood;
    }

    @Override
    public void onAcquire(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        // extra food is granted based on the number of completed sets at the exact moment of acquisition
        int sets = player.getCompletedSetsCount();
        player.addFood(sets * this.extraFood);
    }

    @Override public void onEvent(EventCard event, Player player, GameState gameState) {}
    @Override public void onRoundEnd(Player player, GameState gameState) {}
    @Override public void onTotemPlaced(OfferTile offerTile, Player player, GameState gameState) {}
    @Override public void onGameEnd(Player player, GameState gameState) {}
    @Override public void onCharacterAdded(Player player, CharacterCard newCard) {}
}
