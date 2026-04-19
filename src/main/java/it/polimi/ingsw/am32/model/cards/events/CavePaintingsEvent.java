package it.polimi.ingsw.am32.model.cards.events;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.EventType;
import it.polimi.ingsw.am32.model.enums.StatChangeReason;
import it.polimi.ingsw.am32.model.receipts.PlayerStatsChangeReceipt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CavePaintingsEvent extends EventCard {
    private final int prestigeBonus;
    private final int bonusThreshold;
    private final int prestigePenalty;

    public CavePaintingsEvent(String id, int era, boolean isFinal, int prestigeBonus, int bonusThreshold, int prestigePenalty) {
        super(id, era, isFinal);

        if (prestigeBonus < 0) {
            throw new IllegalArgumentException("prestigeBonus cannot be negative");
        }
        if (bonusThreshold < 0) {
            throw new IllegalArgumentException("bonusThreshold cannot be negative");
        }
        if (prestigePenalty < 0) {
            throw new IllegalArgumentException("prestigePenalty cannot be negative");
        }

        this.prestigeBonus = prestigeBonus;
        this.bonusThreshold = bonusThreshold;
        this.prestigePenalty = prestigePenalty;
    }

    @Override
    public EventType getEventType() {
        return EventType.CAVE_PAINTINGS;
    }

    @Override
    public List<PlayerStatsChangeReceipt> resolveEvent(GameState gameState) {
        Objects.requireNonNull(gameState, "gameState cannot be null");
        List<PlayerStatsChangeReceipt> receipts = new ArrayList<>();

        for (Player player : gameState.getPlayers()) {
            // stats are recorded before resolution
            int preFood = player.getFood();
            int prePrestige = player.getPrestige();

            player.applyCavePaintingsEvent(this.prestigeBonus, this.bonusThreshold, this.prestigePenalty);
            player.triggerCavePaintingsEffects(gameState);

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
                    StatChangeReason.CAVE_PAINTINGS.toString()
            ));
        }
        return receipts;
    }
}
