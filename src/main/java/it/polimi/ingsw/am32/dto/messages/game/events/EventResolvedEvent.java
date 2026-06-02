package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.PlayerStatsChangedDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

import java.util.List;

/**
 * An event-card resolution event is represented.
 */
public class EventResolvedEvent implements MessageDTO {
    private final String eventCardId;
    private final List<PlayerStatsChangedDTO> statsChanges;

    /**
     * An event-card resolution event is initialized with the resolved card id and statistics changes.
     *
     * @param eventCardId the id of the resolved event card
     * @param statsChanges the resulting player statistics changes
     */
    public EventResolvedEvent(String eventCardId, List<PlayerStatsChangedDTO> statsChanges) {
        this.eventCardId = eventCardId;
        this.statsChanges = List.copyOf(statsChanges);
    }

    /**
     * The resolved event card id is returned.
     *
     * @return the event card id
     */
    public String getEventCardId() {
        return eventCardId;
    }

    /**
     * The resulting player statistics changes are returned.
     *
     * @return the player statistics changes
     */
    public List<PlayerStatsChangedDTO> getStatsChanges() {
        return statsChanges;
    }

    /**
     * The visitor is accepted to dispatch the event.
     *
     * @param visitor the client message visitor
     */
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
