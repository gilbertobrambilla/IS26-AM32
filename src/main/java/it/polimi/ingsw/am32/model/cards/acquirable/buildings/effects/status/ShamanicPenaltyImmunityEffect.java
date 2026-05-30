package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.status;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;

import java.util.Objects;

/**
 * Defines a status effect that grants immunity to penalties during shamanic rituals.
 */
public class ShamanicPenaltyImmunityEffect implements BuildingCardEffect {

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
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        player.setShamanicImmunity(true);
    }

    /**
     * Applies the effect at the end of a round.
     *
     * @param player the player owning the building
     * @param gameState the current state of the game
     */
    @Override
    public void onRoundEnd(Player player, GameState gameState) {
    }
}
