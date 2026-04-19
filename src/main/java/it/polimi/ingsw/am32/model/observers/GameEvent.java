package it.polimi.ingsw.am32.model.observers;

import it.polimi.ingsw.am32.model.dto.GameStateDTO;

public abstract class GameEvent {
    private String message;
    private GameStateDTO snapshot;

    protected GameEvent(String message, GameStateDTO snapshot) {
        this.message = message;
        this.snapshot = snapshot;
    }

    public String getMessage() {
        return this.message;
    }

    public GameStateDTO getSnapshot() {
        return this.snapshot;
    }
}
