package it.polimi.ingsw.am32.dto.messages.lobby.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.model.enums.LobbyErrorReason;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * A lobby error event is represented.
 */
public class LobbyErrorEvent implements MessageDTO {
    private final LobbyErrorReason reason;
    private final String errorMessage;

    /**
     * A lobby error event is initialized with error details.
     *
     * @param reason the specific lobby error reason
     * @param errorMessage the descriptive error message
     */
    public LobbyErrorEvent(LobbyErrorReason reason, String errorMessage) {
        this.reason = reason;
        this.errorMessage = errorMessage;
    }

    /**
     * The lobby error reason is returned.
     *
     * @return the lobby error reason
     */
    public LobbyErrorReason getReason() {
        return reason;
    }

    /**
     * The descriptive error message is returned.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
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
