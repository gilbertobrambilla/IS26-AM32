package it.polimi.ingsw.am32.dto.messages.game.models;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A game result data transfer object is represented.
 */
public class GameResultDTO implements Serializable {
    private final List<String> winners;
    private final Map<String, int[]> matchScores;

    /**
     * A game result data transfer object is initialized with winners and scores.
     *
     * @param winners the list of nicknames of the winners
     * @param matchScores the map containing the match scores of the players
     */
    public GameResultDTO(List<String> winners, Map<String, int[]> matchScores) {
        this.winners = List.copyOf(winners);
        this.matchScores = copyMatchScores(matchScores);
    }

    /**
     * The list of winners is returned.
     *
     * @return the list of winners
     */
    public List<String> getWinners() {
        return winners;
    }

    /**
     * The match scores are returned.
     *
     * @return the map of match scores
     */
    public Map<String, int[]> getMatchScores() {
        return copyMatchScores(matchScores);
    }

    /**
     * An immutable defensive copy of the match scores is returned.
     *
     * @param source the source match scores
     * @return the copied match scores
     */
    private static Map<String, int[]> copyMatchScores(Map<String, int[]> source) {
        Map<String, int[]> copy = new LinkedHashMap<>();
        for (Map.Entry<String, int[]> entry : source.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().clone());
        }
        return Collections.unmodifiableMap(copy);
    }

}
