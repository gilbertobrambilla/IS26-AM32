package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * A player-disconnected event is represented.
 */
public class PlayerDisconnectedEvent implements MessageDTO {
    private final String player;

    /**
     * A player-disconnected event is initialized with the disconnected player nickname.
     *
     * @param player the disconnected player nickname
     */
    public PlayerDisconnectedEvent(String player) {
        this.player = player;
    }

    /**
     * The disconnected player nickname is returned.
     *
     * @return the disconnected player nickname
     */
    public String getPlayer() {
        return player;
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
