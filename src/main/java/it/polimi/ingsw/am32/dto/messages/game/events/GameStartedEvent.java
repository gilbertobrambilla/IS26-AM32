package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.GameStateDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

public class GameStartedEvent implements MessageDTO {
    private final GameStateDTO gameState;
    private final String nextPlayer;

    public GameStartedEvent(GameStateDTO gameState, String nextPlayer) {
        this.gameState = gameState;
        this.nextPlayer = nextPlayer;
    }

    public GameStateDTO getGameState() {
        return gameState;
    }

    public String getNextPlayer() {
        return nextPlayer;
    }

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
