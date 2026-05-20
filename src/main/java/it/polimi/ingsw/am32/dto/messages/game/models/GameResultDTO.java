package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Data transfer object encapsulating the results of a single match.
 */
public class GameResultDTO implements Serializable {
    private final List<String> winners;
    private final Map<String, int[]> matchScores;

    /**
     * A new game result object is constructed.
     *
     * @param winners the list of winners nicknames
     * @param matchScores the map containing the match scores of the players
     */
    public GameResultDTO(List<String> winners, Map<String, int[]> matchScores) {
        this.winners = List.copyOf(winners);
        this.matchScores = Map.copyOf(matchScores);
    }

    /**
     * The winners list is retrieved.
     *
     * @return the list of winners
     */
    public List<String> getWinners() {
        return winners;
    }

    /**
     * The match scores are retrieved.
     *
     * @return the map of match scores
     */
    public Map<String, int[]> getMatchScores() {
        return matchScores;
    }
}
