package it.polimi.ingsw.am32.model.cards.acquirable.buildings;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;

public interface BuildingCardEffect {
    void onAcquire(Player player, GameState gameState);
    void onRoundEnd(Player player, GameState gameState);
    void onGameEnd(Player player, GameState gameState);

    default void onArtistAdded(Player player) {}
    default void onBuilderAdded(Player player) {}
    default void onGathererAdded(Player player) {}
    default void onHunterAdded(Player player) {}
    default void onInventorAdded(Player player) {}
    default void onShamanAdded(Player player) {}

    default void onHuntEvent(Player player, GameState gameState) {}
    default void onSustenanceEvent(Player player, GameState gameState) {}
    default void onCavePaintingsEvent(Player player, GameState gameState) {}
    default void onShamanicRitualEvent(Player player, GameState gameState) {}

    default void onSetCompleted(Player player) {}
    default void onInventorPairCompleted(Player player) {}

    default void onTurnOrderPlaced(Player player, boolean gainedFood) {}
}
