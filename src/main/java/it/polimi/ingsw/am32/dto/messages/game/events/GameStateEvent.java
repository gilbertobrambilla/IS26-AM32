package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.GameStateDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

public class GameStateEvent implements MessageDTO {
    private final GameStateDTO gameState;

    public GameStateEvent(GameStateDTO gameState) {
        this.gameState = gameState;
    }

    public GameStateDTO getGameState() {
        return gameState;
    }

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
