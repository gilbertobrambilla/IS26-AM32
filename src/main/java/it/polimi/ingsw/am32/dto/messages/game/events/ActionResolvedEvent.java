package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.PlayerStatsChangedDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.TotemReturnedDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

import java.util.List;

public class ActionResolvedEvent implements MessageDTO {
    private final String player;
    private final List<String> acquiredCharacterCardIds;
    private final List<String> acquiredBuildingCardIds;
    private final List<PlayerStatsChangedDTO> statsChanges;
    private final TotemReturnedDTO totemReturnDetails;
    private final String nextPlayer;
    private final boolean isAutoPlayed;

    public ActionResolvedEvent(String player, List<String> acquiredCharacterCardIds, List<String> acquiredBuildingCardIds, List<PlayerStatsChangedDTO> statsChanges, TotemReturnedDTO totemReturnDetails, String nextPlayer, boolean isAutoPlayed) {
        this.player = player;
        this.acquiredCharacterCardIds = List.copyOf(acquiredCharacterCardIds);
        this.acquiredBuildingCardIds = List.copyOf(acquiredBuildingCardIds);
        this.statsChanges = List.copyOf(statsChanges);
        this.totemReturnDetails = totemReturnDetails;
        this.nextPlayer = nextPlayer;
        this.isAutoPlayed = isAutoPlayed;
    }

    public String getPlayer() {
        return player;
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

    public String getNextPlayer() {
        return nextPlayer;
    }

    public boolean isAutoPlayed() {
        return isAutoPlayed;
    }

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
