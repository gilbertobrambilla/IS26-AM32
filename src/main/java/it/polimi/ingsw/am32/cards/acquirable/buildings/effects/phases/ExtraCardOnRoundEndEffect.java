package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.phases;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.cards.events.EventCard;
import it.polimi.ingsw.am32.enums.GamePhase;
import it.polimi.ingsw.am32.tiles.OfferTile;

import java.util.Objects;

public class ExtraCardOnRoundEndEffect implements BuildingCardEffect {

    @Override
    public void onRoundEnd(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        // extra acquisition phase is initiated directly by the building effect
        gameState.setCurrentPhase(GamePhase.EXTRA_ACQUISITION);
        gameState.setCurrentPlayer(player);

        // notify the game state or the player to allow an extra card draw
        // e.g., player.addExtraDraw(); or gameState.grantExtraDraw(player);
    }

    @Override public void onAcquire(Player player, GameState gameState) {}
    @Override public void onEvent(EventCard event, Player player, GameState gameState) {}
    @Override public void onTotemPlaced(OfferTile offerTile, Player player, GameState gameState) {}
    @Override public void onGameEnd(Player player, GameState gameState) {}
    @Override public void onCharacterAdded(Player player, CharacterCard newCard) {}
}
