package it.polimi.ingsw.am32.model.cards.events;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.cards.Card;
import it.polimi.ingsw.am32.model.cards.CardVisitor;
import it.polimi.ingsw.am32.model.enums.EventType;
import it.polimi.ingsw.am32.model.receipts.PlayerStatsChangeReceipt;

import java.util.List;
import java.util.Objects;

public abstract class EventCard extends Card implements Comparable<EventCard> {
    private final boolean isFinal;

    public EventCard(String id, int era, boolean isFinal) {
        super(id, era);
        this.isFinal = isFinal;
    }

    @Override
    public void accept(CardVisitor visitor) {
        Objects.requireNonNull(visitor, "visitor cannot be null");

        visitor.visit(this);
    }

    @Override
    public int compareTo(EventCard other) {
        Objects.requireNonNull(other, "other event card cannot be null");

        // priority is checked to push specific events to the end
        int priorityComparison = Integer.compare(this.getResolutionPriority(), other.getResolutionPriority());
        if (priorityComparison != 0) {
            return priorityComparison;
        }

        // remaining events are sorted naturally by era in ascending order
        return Integer.compare(this.getEra(), other.getEra());
    }

    protected int getResolutionPriority() {
        // standard priority is returned
        return 1;
    }

    public boolean isFinal() {
        return this.isFinal;
    }

    public abstract EventType getEventType();
    public abstract List<PlayerStatsChangeReceipt> resolveEvent(GameState gameState);
}
