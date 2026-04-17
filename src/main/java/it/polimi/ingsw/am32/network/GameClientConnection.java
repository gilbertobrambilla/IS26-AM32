package it.polimi.ingsw.am32.network;

import it.polimi.ingsw.am32.dto.results.GameResultDTO;
import it.polimi.ingsw.am32.model.enums.InvalidActionReason;
import it.polimi.ingsw.am32.model.observers.GameEvent;

public interface GameClientConnection extends ClientConnection {

    void receiveEvent(GameEvent event);

    void notifyError(InvalidActionReason reason, String message);

    void notifyGameSuspended();

    void notifyGameResumed();

    void notifyGameEnded(GameResultDTO resultDTO);
}