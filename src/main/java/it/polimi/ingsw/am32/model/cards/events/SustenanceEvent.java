package it.polimi.ingsw.am32.model.cards.events;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.EventType;
import it.polimi.ingsw.am32.model.enums.StatChangeReason;
import it.polimi.ingsw.am32.model.receipts.PlayerStatsChangeReceipt;

import java.util.ArrayList;
import java.util.List;
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
    public List<PlayerStatsChangeReceipt> resolveEvent(GameState gameState) {
        Objects.requireNonNull(gameState, "gameState cannot be null");
        List<PlayerStatsChangeReceipt> receipts = new ArrayList<>();

        for (Player player : gameState.getPlayers()) {
            // stats are recorded before resolution
            int preFood = player.getFood();
            int prePrestige = player.getPrestige();

            player.applySustenanceEvent(this.foodCost, this.prestigePenalty);
            player.triggerSustenanceEffects(gameState);

            // stats are recorded after resolution
            int postFood = player.getFood();
            int postPrestige = player.getPrestige();

            // receipt is created to notify client of resolution results
            receipts.add(new PlayerStatsChangeReceipt(
                    player.getNickname(),
                    postFood - preFood,
                    postPrestige - prePrestige,
                    postFood,
                    postPrestige,
                    StatChangeReason.SUSTENANCE.toString()
            ));
        }
        return receipts;
    }

    @Override
    protected int getResolutionPriority() {
        // lower priority is returned for sustenance event
        return 2;
    }
}
