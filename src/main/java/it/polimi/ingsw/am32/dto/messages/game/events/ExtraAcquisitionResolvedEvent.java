package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.PlayerStatsChangedDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

import java.util.List;

public class ExtraAcquisitionResolvedEvent implements MessageDTO {
    private final String player;
    private final String acquiredCardId;
    private final List<PlayerStatsChangedDTO> statsChanges;
    private final String nextPlayer;
    private final boolean isAutoPlayed;

    public ExtraAcquisitionResolvedEvent(String player, String acquiredCardId, List<PlayerStatsChangedDTO> statsChanges, String nextPlayer, boolean isAutoPlayed) {
        this.player = player;
        this.acquiredCardId = acquiredCardId;
        this.statsChanges = List.copyOf(statsChanges);
        this.nextPlayer = nextPlayer;
        this.isAutoPlayed = isAutoPlayed;
    }

    public String getPlayer() {
        return player;
    }

    public String getAcquiredCardId() {
        return acquiredCardId;
    }

    public List<PlayerStatsChangedDTO> getStatsChanges() {
        return statsChanges;
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
