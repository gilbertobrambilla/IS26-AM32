package it.polimi.ingsw.am32.cards.events;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.enums.EventType;

import java.util.Objects;

public class ShamanicRitualEvent extends EventCard {
    private int prestigeBonus;
    private int prestigePenalty;

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

        // find max and min shaman stars across all players
        for (Player player : gameState.getPlayers()) {
            int stars = player.getShamanStars();
            if (stars > maxStars) {
                maxStars = stars;
            }
            if (stars < minStars) {
                minStars = stars;
            }
        }

        // independent ifs allow applying both bonus and penalty in case of a total tie
        // apply bonuses and penalties checking building status effects
        for (Player player : gameState.getPlayers()) {
            int stars = player.getShamanStars();

            if (stars == maxStars) {
                int finalBonus = player.hasDoubleShamanicBonus() ? (this.prestigeBonus * 2) : this.prestigeBonus;
                player.addPrestige(finalBonus);
            }
            if (stars == minStars && !player.hasShamanicImmunity()) {
                player.removePrestige(this.prestigePenalty);
            }
        }
    }
}
