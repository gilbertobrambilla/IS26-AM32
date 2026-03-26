package it.polimi.ingsw.am32.cards.acquirable.buildings;

import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.cards.events.EventCard;
import it.polimi.ingsw.am32.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.tiles.OfferTile;

public interface BuildingCardEffect {
    void onAcquire(Player player, GameState gameState);

    void onEvent(EventCard event, Player player, GameState gameState);

    void onRoundEnd(Player player, GameState gameState);

    void onGameEnd(Player player, GameState gameState);

    void onTotemPlaced(OfferTile offerTile, Player player, GameState gameState);

    void onCharacterAdded(Player player, CharacterCard newCard);
}
