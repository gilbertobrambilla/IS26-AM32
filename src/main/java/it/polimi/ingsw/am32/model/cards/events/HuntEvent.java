package it.polimi.ingsw.am32.model.cards.events;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.EventType;

import java.util.Objects;

public class HuntEvent extends EventCard {
    private final int foodBonus;
    private final int prestigeBonus;

    public HuntEvent(String id, int era, boolean isFinal, int foodBonus, int prestigeBonus) {
        super(id, era, isFinal);

        if (foodBonus < 0) {
            throw new IllegalArgumentException("foodBonus cannot be negative");
        }
        if (prestigeBonus < 0) {
            throw new IllegalArgumentException("prestigeBonus cannot be negative");
        }

        this.foodBonus = foodBonus;
        this.prestigeBonus = prestigeBonus;
    }

    @Override
    public EventType getEventType() {
        return EventType.HUNT;
    }

    @Override
    public void resolveEvent(GameState gameState) {
        Objects.requireNonNull(gameState, "gameState cannot be null");

        for (Player player : gameState.getPlayers()) {
            player.applyHuntEvent(this.foodBonus, this.prestigeBonus);
            player.triggerHuntEffects(gameState);
        }
    }
}
