package it.polimi.ingsw.am32.cards.events;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.enums.EventType;

import java.util.Objects;

public class CavePaintingsEvent extends EventCard {
    private int prestigeBonus;
    private int bonusThreshold;
    private int prestigePenalty;

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
    public void resolveEvent(GameState gameState) {
        Objects.requireNonNull(gameState, "gameState cannot be null");

        for (Player player : gameState.getPlayers()) {
            int artists = player.getArtistCount();

            if (artists < this.bonusThreshold) {
                player.removePrestige(this.prestigePenalty);
            } else {
                player.addPrestige(artists * this.prestigeBonus);
            }
        }
    }
}
