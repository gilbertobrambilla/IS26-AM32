package it.polimi.ingsw.am32.controller;

import it.polimi.ingsw.am32.dto.results.GameResultDTO;
import it.polimi.ingsw.am32.model.enums.InvalidActionReason;
import it.polimi.ingsw.am32.model.observers.GameObserver;

public interface GameView extends GameObserver {
    void notifyError(InvalidActionReason reason, String message);
    void notifyGameSuspended();
    void notifyGameResumed();
    void notifyGameEnded(GameResultDTO resultDTO);
}
