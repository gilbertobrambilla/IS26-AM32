package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

public class TotemPlacedEvent implements MessageDTO {
    private final String player;
    private final String offerTileId;
    private final String nextPlayer;
    private final boolean isAutoPlayed;

    public TotemPlacedEvent(String player, String offerTileId, String nextPlayer, boolean isAutoPlayed) {
        this.player = player;
        this.offerTileId = offerTileId;
        this.nextPlayer = nextPlayer;
        this.isAutoPlayed = isAutoPlayed;
    }

    public String getPlayer() {
        return player;
    }

    public String getOfferTileId() {
        return offerTileId;
    }

    public String getNextPlayer() {
        return nextPlayer;
    }

    public boolean isAutoPlayed() {
        return isAutoPlayed;
    }

    @Override
    public void accept(ClientMessageVisitor visitor) {
        visitor.visit(this);
    }
}
