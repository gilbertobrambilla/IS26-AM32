package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.model.enums.GamePhase;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * A game phase change event is represented.
 */
public class GamePhaseChangedEvent implements MessageDTO {
    private final GamePhase phase;
    private final String currentPlayer;

    /**
     * A game phase change event is initialized with phase and current player data.
     *
     * @param phase the new game phase
     * @param currentPlayer the nickname of the current player
     */
    public GamePhaseChangedEvent(GamePhase phase, String currentPlayer) {
        this.phase = phase;
        this.currentPlayer = currentPlayer;
    }

    /**
     * The new game phase is returned.
     *
     * @return the game phase
     */
    public GamePhase getPhase() {
        return phase;
    }

    /**
     * The current player nickname is returned.
     *
     * @return the current player nickname
     */
    public String getCurrentPlayer() {
        return currentPlayer;
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
