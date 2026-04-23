package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

public class PlayerDisconnectedEvent implements MessageDTO {
    private final String player;

    public PlayerDisconnectedEvent(String player) {
        this.player = player;
    }

    public String getPlayer() {
        return player;
    }

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
