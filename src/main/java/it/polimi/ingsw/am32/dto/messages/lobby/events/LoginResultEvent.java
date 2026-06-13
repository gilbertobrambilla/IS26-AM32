package it.polimi.ingsw.am32.dto.messages.lobby.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * A login result event is represented.
 */
public class LoginResultEvent implements MessageDTO {
    private final boolean isSuccess;
    private final String message;

    /**
     * A login result event is initialized with authentication outcome data.
     *
     * @param isSuccess true if the authentication was successful, false otherwise
     * @param message the text message associated with the result
     */
    public LoginResultEvent(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    /**
     * The authentication success flag is returned.
     *
     * @return true if the authentication was successful, false otherwise
     */
    public boolean isSuccess() {
        return isSuccess;
    }

    /**
     * The result message is returned.
     *
     * @return the result message
     */
    public String getMessage() {
        return message;
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
