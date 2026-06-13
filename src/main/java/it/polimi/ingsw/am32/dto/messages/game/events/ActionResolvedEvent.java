package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.PlayerStatsChangedDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

import java.util.List;

/**
 * An action resolution event is represented.
 */
public class ActionResolvedEvent implements MessageDTO {
    private final String player;
    private final List<String> acquiredCharacterCardIds;
    private final List<String> acquiredBuildingCardIds;
    private final PlayerStatsChangedDTO statsChange;
    private final int turnOrderPosition;
    private final boolean isAutoPlayed;

    /**
     * An action resolution event is initialized with acquisition, statistics, and turn order data.
     *
     * @param player the nickname of the player resolving the action
     * @param acquiredCharacterCardIds the acquired character card ids
     * @param acquiredBuildingCardIds the acquired building card ids
     * @param statsChange the resulting statistics change
     * @param turnOrderPosition the assigned turn order position
     * @param isAutoPlayed true if the action was automatically played
     */
    public ActionResolvedEvent(String player, List<String> acquiredCharacterCardIds, List<String> acquiredBuildingCardIds, PlayerStatsChangedDTO statsChange, int turnOrderPosition, boolean isAutoPlayed) {
        this.player = player;
        this.acquiredCharacterCardIds = List.copyOf(acquiredCharacterCardIds);
        this.acquiredBuildingCardIds = List.copyOf(acquiredBuildingCardIds);
        this.statsChange = statsChange;
        this.turnOrderPosition = turnOrderPosition;
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
     * The acquired character card ids are returned.
     *
     * @return the acquired character card ids
     */
    public List<String> getAcquiredCharacterCardIds() {
        return acquiredCharacterCardIds;
    }

    /**
     * The acquired building card ids are returned.
     *
     * @return the acquired building card ids
     */
    public List<String> getAcquiredBuildingCardIds() {
        return acquiredBuildingCardIds;
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
     * The assigned turn order position is returned.
     *
     * @return the turn order position
     */
    public int getTurnOrderPosition() {
        return turnOrderPosition;
    }

    /**
     * The autoplay flag is returned.
     *
     * @return true if the action was automatically played
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
