package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.GameResultDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * Event dispatched to notify the end of the game with local match results.
 */
public class GameEndedEvent implements MessageDTO {
    private final GameResultDTO results;

    /**
     * A new game-ended event is constructed.
     *
     * @param results the results of the match
     */
    public GameEndedEvent(GameResultDTO results) {
        this.results = results;
    }

    /**
     * The match results are retrieved.
     *
     * @return the game result object
     */
    public GameResultDTO getResults() {
        return results;
    }

    /**
     * The visitor is accepted to process the message.
     *
     * @param visitor the client message visitor
     */
    @Override
    public void accept(ClientMessageVisitor visitor) {
        // event is dispatched to the visitor
        visitor.visit(this);
    }
}
