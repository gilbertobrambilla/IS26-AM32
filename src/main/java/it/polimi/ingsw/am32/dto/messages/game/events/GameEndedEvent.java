package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.GameResultDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * A game-ended event with local match results is represented.
 */
public class GameEndedEvent implements MessageDTO {
    private final GameResultDTO results;

    /**
     * A game-ended event is initialized with match results.
     *
     * @param results the results of the match
     */
    public GameEndedEvent(GameResultDTO results) {
        this.results = results;
    }

    /**
     * The match results are returned.
     *
     * @return the game result object
     */
    public GameResultDTO getResults() {
        return results;
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
