package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.status;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.cards.events.EventCard;
import it.polimi.ingsw.am32.tiles.OfferTile;

import java.util.Objects;

public class ExtraShamanStarsEffect implements BuildingCardEffect {
    private int extraStars;

    public ExtraShamanStarsEffect(int extraStars) {
        if(extraStars < 0) {
            throw new IllegalArgumentException("extraStars cannot be negative");
        }

        this.extraStars = extraStars;
    }

    @Override
    public void onAcquire(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        // extra shaman stars are added to the player's total immediately upon acquisition
        player.addShamanStars(this.extraStars);
    }

    @Override public void onEvent(EventCard event, Player player, GameState gameState) {}
    @Override public void onRoundEnd(Player player, GameState gameState) {}
    @Override public void onTotemPlaced(OfferTile offerTile, Player player, GameState gameState) {}
    @Override public void onGameEnd(Player player, GameState gameState) {}
    @Override public void onCharacterAdded(Player player, CharacterCard newCard) {}
}
