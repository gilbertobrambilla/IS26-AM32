package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.LeaderboardEntryDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

import java.util.List;

/**
 * A leaderboard event is represented.
 */
public class LeaderboardEvent implements MessageDTO {
    private final List<LeaderboardEntryDTO> globalLeaderboard;
    private final int globalPosition;
    private final boolean dbAvailable;

    /**
     * A leaderboard event is initialized with global ranking data.
     *
     * @param globalLeaderboard the list of global leaderboard entries
     * @param globalPosition the calculated global position of the player
     * @param dbAvailable the flag indicating if the database is available
     */
    public LeaderboardEvent(List<LeaderboardEntryDTO> globalLeaderboard, int globalPosition, boolean dbAvailable) {
        this.globalLeaderboard = globalLeaderboard != null ? List.copyOf(globalLeaderboard) : null;
        this.globalPosition = globalPosition;
        this.dbAvailable = dbAvailable;
    }

    /**
     * The global leaderboard list is returned.
     *
     * @return the global leaderboard list
     */
    public List<LeaderboardEntryDTO> getGlobalLeaderboard() {
        return globalLeaderboard;
    }

    /**
     * The global position is returned.
     *
     * @return the player global position
     */
    public int getGlobalPosition() {
        return globalPosition;
    }

    /**
     * The database availability flag is returned.
     *
     * @return true if the database is available, false otherwise
     */
    public boolean isDbAvailable() {
        return dbAvailable;
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
