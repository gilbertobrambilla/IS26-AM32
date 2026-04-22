package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.dto.messages.game.models.GameResultDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

public class GameEndedEvent implements MessageDTO {
    private final GameResultDTO results;

    public GameEndedEvent(GameResultDTO results) {
        this.results = results;
    }

    public GameResultDTO getResults() {
        return results;
    }

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
