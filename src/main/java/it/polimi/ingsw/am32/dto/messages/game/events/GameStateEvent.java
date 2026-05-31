package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.GameStateDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * A game state synchronization event is represented.
 */
public class GameStateEvent implements MessageDTO {
    private final GameStateDTO gameState;

    /**
     * A game state synchronization event is initialized with a full game state.
     *
     * @param gameState the full game state
     */
    public GameStateEvent(GameStateDTO gameState) {
        this.gameState = gameState;
    }

    /**
     * The full game state is returned.
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
