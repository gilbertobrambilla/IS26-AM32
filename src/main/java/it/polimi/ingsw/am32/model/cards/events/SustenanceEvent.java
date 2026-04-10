package it.polimi.ingsw.am32.model.cards.events;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.EventType;

import java.util.Objects;

public class SustenanceEvent extends EventCard {
    private final int foodCost;
    private final int prestigePenalty;

    public SustenanceEvent(String id, int era, boolean isFinal, int foodCost, int prestigePenalty) {
        super(id, era, isFinal);

        if (foodCost < 0) {
            throw new IllegalArgumentException("foodCost cannot be negative");
        }
        if (prestigePenalty < 0) {
            throw new IllegalArgumentException("prestigePenalty cannot be negative");
        }

        this.foodCost = foodCost;
        this.prestigePenalty = prestigePenalty;
    }

    @Override
    public EventType getEventType() {
        return EventType.SUSTENANCE;
    }

    @Override
    public void resolveEvent(GameState gameState) {
        Objects.requireNonNull(gameState, "gameState cannot be null");

        for (Player player : gameState.getPlayers()) {
            player.applySustenanceEvent(this.foodCost, this.prestigePenalty);
            player.triggerSustenanceEffects(gameState);
        }
    }

    @Override
    protected int getResolutionPriority() {
        // lower priority is returned for sustenance event
        return 2;
    }
}
