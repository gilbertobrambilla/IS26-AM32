package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.model.GameResult;

/**
 * Interface used to notify the lobby about game lifecycle events.
 */
public interface GameLifecycleCallback {

    /**
     * The termination of a game is processed.
     *
     * @param gameId the id of the ended game
     * @param result the final result of the game
     */
    void endGame(int gameId, GameResult result);

    /**
     * The termination of an abandoned game without calculating results is processed.
     *
     * @param gameId the id of the abandoned game
     */
    void abortGame(int gameId);
}
