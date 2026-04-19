package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.game.models.PlayerStatsChangedDTO;

import java.util.List;

public class EventResolvedEvent {
    private final String eventCardId;
    private final List<PlayerStatsChangedDTO> playersStatChanges;
    private final String nextPlayerNickname;

    public EventResolvedEvent(String eventCardId, List<PlayerStatsChangedDTO> playersStatChanges, String nextPlayerNickname) {
        this.eventCardId = eventCardId;
        this.playersStatChanges = playersStatChanges;
        this.nextPlayerNickname = nextPlayerNickname;
    }

    public String getEventCardId() {
        return eventCardId;
    }

    public List<PlayerStatsChangedDTO> getPlayersStatChanges() {
        return playersStatChanges;
    }

    public String getNextPlayerNickname() {
        return nextPlayerNickname;
    }
}
