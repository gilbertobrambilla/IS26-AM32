package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;

public class PlayerDisconnectedEvent implements MessageDTO {
    private final String disconnectedPlayerNickname;

    public PlayerDisconnectedEvent(String disconnectedPlayerNickname) {
        this.disconnectedPlayerNickname = disconnectedPlayerNickname;
    }

    public String getDisconnectedPlayerNickname() {
        return disconnectedPlayerNickname;
    }
}
