package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * A player-reconnected event is represented.
 */
public class PlayerReconnectedEvent implements MessageDTO {
    private final String player;

    /**
     * A player-reconnected event is initialized with the reconnected player nickname.
     *
     * @param player the reconnected player nickname
     */
    public PlayerReconnectedEvent(String player) {
        this.player = player;
    }

    /**
     * The reconnected player nickname is returned.
     *
     * @return the reconnected player nickname
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
