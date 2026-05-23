package it.polimi.ingsw.am32.model.cards.events;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.cards.Card;
import it.polimi.ingsw.am32.model.cards.CardVisitor;
import it.polimi.ingsw.am32.model.enums.EventType;
import it.polimi.ingsw.am32.model.receipts.PlayerStatsChangeReceipt;

import java.util.List;
import java.util.Objects;

/**
 * Represents an abstract event card in the game.
 */
public abstract class EventCard extends Card implements Comparable<EventCard> {
    private final boolean isFinal;

    /**
     * Initializes the event card with its basic attributes.
     *
     * @param id the unique identifier of the card
     * @param era the era the card belongs to
     * @param isFinal flag indicating if it is a final event
     */
    public EventCard(String id, int era, boolean isFinal) {
        super(id, era);
        this.isFinal = isFinal;
    }

    /**
     * Accepts a visitor for the card.
     *
     * @param visitor the visitor to accept
     */
    @Override
    public void accept(CardVisitor visitor) {
        Objects.requireNonNull(visitor, "visitor cannot be null");

        visitor.visit(this);
    }

    /**
     * Compares this event card with another to determine resolution order.
     *
     * @param other the other event card to compare with
     * @return a negative integer, zero, or a positive integer as this card is less than, equal to, or greater than the specified card
     */
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

    /**
     * Provides the event type.
     *
     * @return the type of the event
     */
    public abstract EventType getEventType();

    /**
     * Resolves the event by applying its effects to the game state.
     *
     * @param gameState the current state of the game
     * @return a list of receipts detailing the changes in player statistics
     */
    public abstract List<PlayerStatsChangeReceipt> resolveEvent(GameState gameState);

    /**
     * Provides the resolution priority of the event.
     *
     * @return the standard priority of the event
     */
    protected int getResolutionPriority() {
        // standard priority is returned
        return 1;
    }

    /**
     * Indicates whether the event is a final event.
     *
     * @return true if the event is final, false otherwise
     */
    public boolean isFinal() {
        return this.isFinal;
    }
}
