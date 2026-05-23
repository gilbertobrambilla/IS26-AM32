package it.polimi.ingsw.am32.model.enums;

/**
 * Lists the possible reasons for a lobby action failure.
 */
public enum LobbyErrorReason {
    /**
     * Indicates that the game is already full or has already started.
     */
    GAME_FULL_OR_STARTED,
    /**
     * Indicates that the requested game could not be found.
     */
    GAME_NOT_FOUND,
    /**
     * Indicates that the specified player count is invalid.
     */
    INVALID_PLAYER_COUNT,
    /**
     * Indicates that the attempt to join the game failed.
     */
    JOIN_FAILED
}
