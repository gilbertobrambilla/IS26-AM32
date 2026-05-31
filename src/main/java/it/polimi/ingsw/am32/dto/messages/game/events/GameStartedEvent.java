package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.GameStateDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * A game started event is represented.
 */
public class GameStartedEvent implements MessageDTO {
    private final GameStateDTO gameState;

    /**
     * A game started event is initialized with the initial game state.
     *
     * @param gameState the initial game state
     */
    public GameStartedEvent(GameStateDTO gameState) {
        this.gameState = gameState;
    }

    /**
     * The initial game state is returned.
     *
     * @return the game state
     */
    public GameStateDTO getGameState() {
        return gameState;
    }

    /**
     * The visitor is accepted to dispatch the event.
     *
     * @param visitor the client message visitor
     */
    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
