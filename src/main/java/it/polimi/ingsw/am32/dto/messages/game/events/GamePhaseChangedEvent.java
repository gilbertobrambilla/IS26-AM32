package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.model.enums.GamePhase;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

public class GamePhaseChangedEvent implements MessageDTO {
    private final GamePhase phase;
    private final String nextPlayer;

    public GamePhaseChangedEvent(GamePhase phase, String nextPlayer) {
        this.phase = phase;
        this.nextPlayer = nextPlayer;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public String getNextPlayer() {
        return nextPlayer;
    }

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
