package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.model.GameResult;

public interface GameLifecycleCallback {
    void endGame(int gameId, GameResult result);

    /**
     * Processes the termination of an abandoned game without calculating results.
     *
     * @param gameId the ID of the abandoned game.
     */
    void abortGame(int gameId);
}
