package it.polimi.ingsw.am32.dto.messages.game.events;

public class PlayerReconnectedEvent {
    private final String reconnectedPlayerNickname;

    public PlayerReconnectedEvent(String reconnectedPlayerNickname) {
        this.reconnectedPlayerNickname = reconnectedPlayerNickname;
    }

    public String getReconnectedPlayerNickname() {
        return reconnectedPlayerNickname;
    }
}
