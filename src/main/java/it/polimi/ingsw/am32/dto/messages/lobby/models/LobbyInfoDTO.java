package it.polimi.ingsw.am32.dto.messages.lobby.models;

import java.io.Serializable;
import java.util.List;

/**
 * A lobby information data transfer object is represented.
 */
public class LobbyInfoDTO implements Serializable {
    private final int gameId;
    private final List<String> currentPlayers;
    private final int currentPlayersCount;
    private final int targetPlayers;

    /**
     * A lobby information data transfer object is initialized with lobby summary data.
     *
     * @param gameId the game identifier
     * @param currentPlayersCount the current number of players
     * @param currentPlayers the current player nicknames
     * @param targetPlayers the required number of players
     */
    public LobbyInfoDTO(int gameId, int currentPlayersCount, List<String> currentPlayers, int targetPlayers) {
        this.gameId = gameId;
        this.currentPlayers = List.copyOf(currentPlayers);
        this.currentPlayersCount = currentPlayersCount;
        this.targetPlayers = targetPlayers;
    }

    /**
     * The game identifier is returned.
     *
     * @return the game identifier
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * The current player nicknames are returned.
     *
     * @return the current player nicknames
     */
    public List<String> getCurrentPlayers() {
        return currentPlayers;
    }

    /**
     * The current number of players is returned.
     *
     * @return the current number of players
     */
    public int getCurrentPlayersCount() {
        return currentPlayersCount;
    }

    /**
     * The required number of players is returned.
     *
     * @return the required number of players
     */
    public int getTargetPlayers() {
        return targetPlayers;
    }
}
