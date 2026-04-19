package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.game.models.PlayerStatsChangedDTO;

import java.util.List;

public class ExtraAcquisitionResolvedEvent {
    private final String playerNickname;
    private final String acquiredCardId;
    private final List<PlayerStatsChangedDTO> statsChanges;
    private final String nextPlayerNickname;
    private final boolean isAutoPlayed;

    public ExtraAcquisitionResolvedEvent(String playerNickname, String acquiredCardId, List<PlayerStatsChangedDTO> statsChanges, String nextPlayerNickname, boolean isAutoPlayed) {
        this.playerNickname = playerNickname;
        this.acquiredCardId = acquiredCardId;
        this.statsChanges = statsChanges;
        this.nextPlayerNickname = nextPlayerNickname;
        this.isAutoPlayed = isAutoPlayed;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public String getAcquiredCardId() {
        return acquiredCardId;
    }

    public List<PlayerStatsChangedDTO> getStatsChanges() {
        return statsChanges;
    }

    public String getNextPlayerNickname() {
        return nextPlayerNickname;
    }

    public boolean isAutoPlayed() {
        return isAutoPlayed;
    }
}
