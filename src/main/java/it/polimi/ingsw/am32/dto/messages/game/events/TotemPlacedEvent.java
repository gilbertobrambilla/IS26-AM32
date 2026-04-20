package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;

public class TotemPlacedEvent implements MessageDTO {
    private final String playerNickname;
    private final String offerTileId;
    private final String nextPlayerNickname;
    private final boolean isAutoPlayed;

    public TotemPlacedEvent(String playerNickname, String offerTileId, String nextPlayerNickname, boolean isAutoPlayed) {
        this.playerNickname = playerNickname;
        this.offerTileId = offerTileId;
        this.nextPlayerNickname = nextPlayerNickname;
        this.isAutoPlayed = isAutoPlayed;
    }

    public String getPlayerNickname() {
        return playerNickname;
    }

    public String getOfferTileId() {
        return offerTileId;
    }

    public String getNextPlayerNickname() {
        return nextPlayerNickname;
    }

    public boolean isAutoPlayed() {
        return isAutoPlayed;
    }
}
