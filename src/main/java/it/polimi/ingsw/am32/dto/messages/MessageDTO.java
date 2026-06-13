package it.polimi.ingsw.am32.dto.messages;

import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

import java.io.Serializable;

/**
 * A serializable message exchanged with the client is represented.
 */
public interface MessageDTO extends Serializable {
    /**
     * The visitor is accepted to dispatch the message.
     *
     * @param visitor the client message visitor
     */
    void accept(ClientMessageVisitor visitor);
}
