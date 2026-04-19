package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.game.models.PlayerStatsChangedDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.TotemReturnedDTO;

import java.util.List;

public class ActionResolvedEvent {
    private final String playerNickname;
    private final List<String> acquiredCharacterCardIds;
    private final List<String> acquiredBuildingCardIds;
    private final List<PlayerStatsChangedDTO> statsChanges;
    private final TotemReturnedDTO totemReturnDetails;
    private final String nextPlayerNickname;
    private final boolean isAutoPlayed;

    public ActionResolvedEvent(String playerNickname,
                               List<String> acquiredCharacterCardIds,
                               List<String> acquiredBuildingCardIds,
                               List<PlayerStatsChangedDTO> statsChanges,
                               TotemReturnedDTO totemReturnDetails,
                               String nextPlayerNickname,
                               boolean isAutoPlayed) {
        this.playerNickname = playerNickname;
        this.acquiredCharacterCardIds = acquiredCharacterCardIds;
        this.acquiredBuildingCardIds = acquiredBuildingCardIds;
        this.statsChanges = statsChanges;
        this.totemReturnDetails = totemReturnDetails;
        this.nextPlayerNickname = nextPlayerNickname;
        this.isAutoPlayed = isAutoPlayed;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public List<String> getAcquiredCharacterCardIds() {
        return acquiredCharacterCardIds;
    }

    public List<String> getAcquiredBuildingCardIds() {
        return acquiredBuildingCardIds;
    }

    public List<PlayerStatsChangedDTO> getStatsChanges() {
        return statsChanges;
    }

    public TotemReturnedDTO getTotemReturnDetails() {
        return totemReturnDetails;
    }

    public String getNextPlayerNickname() {
        return nextPlayerNickname;
    }

    public boolean isAutoPlayed() {
        return isAutoPlayed;
    }
}
