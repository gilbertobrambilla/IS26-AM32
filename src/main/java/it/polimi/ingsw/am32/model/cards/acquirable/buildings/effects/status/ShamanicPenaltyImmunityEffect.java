package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.status;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;

import java.util.Objects;

public class ShamanicPenaltyImmunityEffect implements BuildingCardEffect {

    @Override
    public void onAcquire(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        player.setShamanicImmunity(true);
    }

    @Override public void onRoundEnd(Player player, GameState gameState) {}
    @Override public void onGameEnd(Player player, GameState gameState) {}
}
