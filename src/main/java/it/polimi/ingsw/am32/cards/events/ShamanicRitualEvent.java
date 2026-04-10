package it.polimi.ingsw.am32.cards.events;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.enums.EventType;

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
    public void resolveEvent(GameState gameState) {
        Objects.requireNonNull(gameState, "gameState cannot be null");

        int maxStars = -1;
        int minStars = Integer.MAX_VALUE;

        // shamanic power is retrieved to determine highest and lowest values across all players
        for (Player player : gameState.getPlayers()) {
            int stars = player.getShamanStars();
            if (stars > maxStars) {
                maxStars = stars;
            }
            if (stars < minStars) {
                minStars = stars;
            }
        }

        // event result is delegated to each player and shamanic ritual specific effects are triggered
        for (Player player : gameState.getPlayers()) {
            int stars = player.getShamanStars();
            boolean isWinner = (stars == maxStars);
            boolean isLoser = (stars == minStars);

            player.applyShamanicRitualResult(isWinner, isLoser, this.prestigeBonus, this.prestigePenalty);
            player.triggerShamanicRitualEffects(gameState);
        }
    }
}
