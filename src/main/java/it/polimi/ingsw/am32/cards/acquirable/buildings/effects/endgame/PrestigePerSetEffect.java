package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.endgame;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.cards.events.EventCard;
import it.polimi.ingsw.am32.tiles.OfferTile;

import java.util.Objects;

public class PrestigePerSetEffect implements BuildingCardEffect {
    private int pointsPerSet;

    public PrestigePerSetEffect(int pointsPerSet) {
        if(pointsPerSet < 0) {
            throw new IllegalArgumentException("pointsPerSet cannot be negative");
        }

        this.pointsPerSet = pointsPerSet;
    }

    @Override
    public void onGameEnd(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        int sets = player.getCompletedSetsCount();
        player.addPrestige(sets * this.pointsPerSet);
    }

    @Override public void onAcquire(Player player, GameState gameState) {}
    @Override public void onEvent(EventCard event, Player player, GameState gameState) {}
    @Override public void onRoundEnd(Player player, GameState gameState) {}
    @Override public void onTotemPlaced(OfferTile offerTile, Player player, GameState gameState) {}
    @Override public void onCharacterAdded(Player player, CharacterCard newCard) {}
}
