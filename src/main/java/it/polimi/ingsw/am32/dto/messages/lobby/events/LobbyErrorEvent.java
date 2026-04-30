package it.polimi.ingsw.am32.dto.messages.lobby.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.model.enums.LobbyErrorReason;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * Event representing a failed action within the lobby.
 */
public class LobbyErrorEvent implements MessageDTO {
    private final LobbyErrorReason reason;
    private final String errorMessage;

    /**
     * Instantiates a new lobby error event.
     *
     * @param reason the specific reason for the error.
     * @param errorMessage the descriptive error message.
     */
    public LobbyErrorEvent(LobbyErrorReason reason, String errorMessage) {
        this.reason = reason;
        this.errorMessage = errorMessage;
    }

    public LobbyErrorReason getReason() {
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
