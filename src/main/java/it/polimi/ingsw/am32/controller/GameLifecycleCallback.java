package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.model.GameResult;

public interface GameLifecycleCallback {
    void endGame(int gameId, GameResult result);
}
