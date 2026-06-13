package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.PlayerStatsChangedDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * An extra acquisition resolution event is represented.
 */
public class ExtraAcquisitionResolvedEvent implements MessageDTO {
    private final String player;
    private final String acquiredCardId;
    private final PlayerStatsChangedDTO statsChange;
    private final boolean isAutoPlayed;

    /**
     * An extra acquisition resolution event is initialized with acquisition and statistics data.
     *
     * @param player the nickname of the player resolving the extra acquisition
     * @param acquiredCardId the acquired card id
     * @param statsChange the resulting statistics change
     * @param isAutoPlayed true if the extra acquisition was automatically played
     */
    public ExtraAcquisitionResolvedEvent(String player, String acquiredCardId, PlayerStatsChangedDTO statsChange, boolean isAutoPlayed) {
        this.player = player;
        this.acquiredCardId = acquiredCardId;
        this.statsChange = statsChange;
        this.isAutoPlayed = isAutoPlayed;
    }

    /**
     * The player nickname is returned.
     *
     * @return the player nickname
     */
    public String getPlayer() {
        return player;
    }

    /**
     * The acquired card id is returned.
     *
     * @return the acquired card id
     */
    public String getAcquiredCardId() {
        return acquiredCardId;
    }

    /**
     * The resulting statistics change is returned.
     *
     * @return the resulting statistics change
     */
    public PlayerStatsChangedDTO getStatsChange() {
        return statsChange;
    }

    /**
     * The autoplay flag is returned.
     *
     * @return true if the extra acquisition was automatically played
     */
    public boolean isAutoPlayed() {
        return isAutoPlayed;
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
