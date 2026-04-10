package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.phases;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.model.enums.GamePhase;

import java.util.Objects;

public class ExtraCardOnRoundEndEffect implements BuildingCardEffect {

    @Override
    public void onRoundEnd(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        gameState.setCurrentPhase(GamePhase.EXTRA_ACQUISITION);
        gameState.setCurrentPlayer(player);
    }

    @Override public void onAcquire(Player player, GameState gameState) {}
    @Override public void onGameEnd(Player player, GameState gameState) {}
}
