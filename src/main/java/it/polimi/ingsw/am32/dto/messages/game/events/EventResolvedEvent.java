package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.PlayerStatsChangedDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

import java.util.List;

public class EventResolvedEvent implements MessageDTO {
    private final String eventCardId;
    private final List<PlayerStatsChangedDTO> statsChanges;
    private final String nextPlayer;

    public EventResolvedEvent(String eventCardId, List<PlayerStatsChangedDTO> statsChanges, String nextPlayer) {
        this.eventCardId = eventCardId;
        this.statsChanges = List.copyOf(statsChanges);
        this.nextPlayer = nextPlayer;
    }

    public String getEventCardId() {
        return eventCardId;
    }

    public List<PlayerStatsChangedDTO> getStatsChanges() {
        return statsChanges;
    }

    public String getNextPlayer() {
        return nextPlayer;
    }

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
