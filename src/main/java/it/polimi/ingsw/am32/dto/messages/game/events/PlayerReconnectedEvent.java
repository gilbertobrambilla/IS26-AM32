package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;

public class PlayerReconnectedEvent implements MessageDTO {
    private final String reconnectedPlayerNickname;

    public PlayerReconnectedEvent(String reconnectedPlayerNickname) {
        this.reconnectedPlayerNickname = reconnectedPlayerNickname;
    }

    public String getReconnectedPlayerNickname() {
        return reconnectedPlayerNickname;
    }
}
