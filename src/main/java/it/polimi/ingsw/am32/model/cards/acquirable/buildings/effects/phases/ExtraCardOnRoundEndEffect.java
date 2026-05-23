package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.phases;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.model.enums.GamePhase;

import java.util.Objects;

/**
 * Defines a phase effect that triggers an extra card acquisition at the end of the round.
 */
public class ExtraCardOnRoundEndEffect implements BuildingCardEffect {

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
     * Applies the effect upon acquiring the building.
     *
     * @param player the player acquiring the building
     * @param gameState the current state of the game
     */
    @Override
    public void onAcquire(Player player, GameState gameState) {
    }

    /**
     * Applies the effect at the end of a round.
     *
     * @param player the player owning the building
     * @param gameState the current state of the game
     */
    @Override
    public void onRoundEnd(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        gameState.setCurrentPhase(GamePhase.EXTRA_ACQUISITION);
        gameState.setCurrentPlayer(player);
    }
}
