package it.polimi.ingsw.am32.model.enums;

/**
 * Lists the possible reasons for a game action failure.
 */
public enum GameErrorReason {
    /**
     * Indicates that the player is not currently on an offer tile.
     */
    PLAYER_NOT_ON_TILE,
    /**
     * Indicates that the requested card could not be found.
     */
    CARD_NOT_FOUND,
    /**
     * Indicates that an event card cannot be selected.
     */
    EVENT_CARD_NOT_ALLOWED,
    /**
     * Indicates that the number of selected items is invalid.
     */
    INVALID_SELECTION_AMOUNT,
    /**
     * Indicates that there is not enough food to perform the action.
     */
    NOT_ENOUGH_FOOD,
    /**
     * Indicates that the target tile is already occupied.
     */
    TILE_ALREADY_OCCUPIED,
    /**
     * Indicates that the turn order tile has no available spaces.
     */
    TURN_ORDER_TILE_FULL,
    /**
     * Indicates that the specified offer tile could not be found.
     */
    OFFER_TILE_NOT_FOUND,
    /**
     * Indicates that the pregame host disconnected and destroyed the lobby.
     */
    PREGAME_DESTROYED_HOST_DISCONNECTED,
    /**
     * Indicates that the action was denied because the game is suspended.
     */
    ACTION_DENIED_GAME_SUSPENDED,
    /**
     * Indicates that it is not the player's turn.
     */
    NOT_YOUR_TURN,
    /**
     * Indicates that the action is invalid for the current game phase.
     */
    INVALID_PHASE,
    /**
     * Indicates that the game initialization process failed.
     */
    GAME_INITIALIZATION_FAILED,
    /**
     * Indicates an unexpected internal game error.
     */
    INTERNAL_ERROR
}
