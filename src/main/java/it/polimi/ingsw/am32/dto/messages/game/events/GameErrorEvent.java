package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.model.enums.GameErrorReason;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

public class GameErrorEvent implements MessageDTO {
    private final String player;
    private final GameErrorReason reason;
    private final String errorMessage;

    public GameErrorEvent(String player, GameErrorReason reason, String errorMessage) {
        this.player = player;
        this.reason = reason;
        this.errorMessage = errorMessage;
    }

    public String getPlayer() {
        return player;
    }

    public GameErrorReason getReason() {
        return reason;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
