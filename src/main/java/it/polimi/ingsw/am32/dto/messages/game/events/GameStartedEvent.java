package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.game.models.GameStateDTO;

public class GameStartedEvent {
    private final GameStateDTO initialGameState;
    private final String nextPlayerNickname;

    public GameStartedEvent(GameStateDTO initialGameState, String nextPlayerNickname) {
        this.initialGameState = initialGameState;
        this.nextPlayerNickname = nextPlayerNickname;
    }

    public GameStateDTO getInitialGameState() {
        return initialGameState;
    }

    public String getNextPlayerNickname() {
        return nextPlayerNickname;
    }
}
