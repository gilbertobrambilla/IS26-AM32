package it.polimi.ingsw.am32.dto.messages;

import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

import java.io.Serializable;

public interface MessageDTO extends Serializable {
    /**
     * Accepts a visitor to dispatch the message.
     *
     * @param visitor The visitor to be accepted.
     */
    void accept(ClientMessageVisitor visitor);
}
