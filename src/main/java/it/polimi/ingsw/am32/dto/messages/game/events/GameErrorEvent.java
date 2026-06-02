package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.model.enums.GameErrorReason;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * A game error event is represented.
 */
public class GameErrorEvent implements MessageDTO {
    private final String player;
    private final GameErrorReason reason;
    private final String errorMessage;

    /**
     * A game error event is initialized with player and error details.
     *
     * @param player the nickname of the player associated with the error
     * @param reason the specific game error reason
     * @param errorMessage the descriptive error message
     */
    public GameErrorEvent(String player, GameErrorReason reason, String errorMessage) {
        this.player = player;
        this.reason = reason;
        this.errorMessage = errorMessage;
    }

    /**
     * The player nickname is returned.
     *
     * @return the player nickname
     */
    public String getPlayer() {
        return player;
    }

    /**
     * The game error reason is returned.
     *
     * @return the game error reason
     */
    public GameErrorReason getReason() {
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
