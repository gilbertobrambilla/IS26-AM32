package it.polimi.ingsw.am32.dto.messages.game.events;

public class PlayerDisconnectedEvent {
    private final String disconnectedPlayerNickname;

    public PlayerDisconnectedEvent(String disconnectedPlayerNickname) {
        this.disconnectedPlayerNickname = disconnectedPlayerNickname;
    }

    public String getDisconnectedPlayerNickname() {
        return disconnectedPlayerNickname;
    }
}
