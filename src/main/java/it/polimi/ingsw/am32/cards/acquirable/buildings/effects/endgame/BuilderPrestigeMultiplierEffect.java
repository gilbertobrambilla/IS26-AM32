package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.endgame;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.cards.events.EventCard;
import it.polimi.ingsw.am32.tiles.OfferTile;

import java.util.Objects;

public class BuilderPrestigeMultiplierEffect implements BuildingCardEffect {
    private int multiplier;

    public BuilderPrestigeMultiplierEffect(int multiplier) {
        if(multiplier < 1) {
            throw new IllegalArgumentException("multiplier cannot be less than 1");
        }

        this.multiplier = multiplier;
    }

    @Override
    public void onGameEnd(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        // base prestige of builders is added during normal game end scoring
        // this effect adds the extra multiplied amount
        int baseBuilderPrestige = player.getBuildersTotalPrestige();
        int extraPrestige = baseBuilderPrestige * (this.multiplier - 1);
        player.addPrestige(extraPrestige);
    }

    @Override public void onAcquire(Player player, GameState gameState) {}
    @Override public void onEvent(EventCard event, Player player, GameState gameState) {}
    @Override public void onRoundEnd(Player player, GameState gameState) {}
    @Override public void onTotemPlaced(OfferTile offerTile, Player player, GameState gameState) {}
    @Override public void onCharacterAdded(Player player, CharacterCard newCard) {}
}
