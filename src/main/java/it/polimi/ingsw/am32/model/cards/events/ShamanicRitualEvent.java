package it.polimi.ingsw.am32.model.cards.events;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.EventType;
import it.polimi.ingsw.am32.model.enums.StatChangeReason;
import it.polimi.ingsw.am32.model.receipts.PlayerStatsChangeReceipt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShamanicRitualEvent extends EventCard {
    private final int prestigeBonus;
    private final int prestigePenalty;

    public ShamanicRitualEvent(String id, int era, boolean isFinal, int prestigeBonus, int prestigePenalty) {
        super(id, era, isFinal);

        if (prestigeBonus < 0) {
            throw new IllegalArgumentException("prestigeBonus cannot be negative");
        }
        if (prestigePenalty < 0) {
            throw new IllegalArgumentException("prestigePenalty cannot be negative");
        }

        this.prestigeBonus = prestigeBonus;
        this.prestigePenalty = prestigePenalty;
    }

    @Override
    public EventType getEventType() {
        return EventType.SHAMANIC_RITUAL;
    }

    @Override
    public List<PlayerStatsChangeReceipt> resolveEvent(GameState gameState) {
        Objects.requireNonNull(gameState, "gameState cannot be null");
        List<PlayerStatsChangeReceipt> receipts = new ArrayList<>();

        int maxStars = -1;
        int minStars = Integer.MAX_VALUE;

        for (Player player : gameState.getPlayers()) {
            int stars = player.getShamanStars();
            if (stars > maxStars) maxStars = stars;
            if (stars < minStars) minStars = stars;
        }

        for (Player player : gameState.getPlayers()) {
            // stats are recorded before resolution
            int preFood = player.getFood();
            int prePrestige = player.getPrestige();

            int stars = player.getShamanStars();
            boolean isWinner = (stars == maxStars);
            boolean isLoser = (stars == minStars);

            player.applyShamanicRitualResult(isWinner, isLoser, this.prestigeBonus, this.prestigePenalty);
            player.triggerShamanicRitualEffects(gameState);

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
                    StatChangeReason.SHAMANIC_RITUAL.toString()
            ));
        }
        return receipts;
    }
}
