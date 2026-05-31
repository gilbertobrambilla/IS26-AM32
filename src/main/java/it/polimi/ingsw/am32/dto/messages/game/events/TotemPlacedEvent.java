package it.polimi.ingsw.am32.dto.messages.game.events;

import it.polimi.ingsw.am32.dto.messages.MessageDTO;
import it.polimi.ingsw.am32.network.client.ClientMessageVisitor;

/**
 * A totem-placed event is represented.
 */
public class TotemPlacedEvent implements MessageDTO {
    private final String player;
    private final String offerTileId;
    private final String nextPlayer;
    private final boolean isAutoPlayed;

    /**
     * A totem-placed event is initialized with placement and turn data.
     *
     * @param player the nickname of the player placing the totem
     * @param offerTileId the id of the selected offer tile
     * @param nextPlayer the nickname of the next player
     * @param isAutoPlayed true if the placement was automatically played
     */
    public TotemPlacedEvent(String player, String offerTileId, String nextPlayer, boolean isAutoPlayed) {
        this.player = player;
        this.offerTileId = offerTileId;
        this.nextPlayer = nextPlayer;
        this.isAutoPlayed = isAutoPlayed;
    }

    /**
     * The player nickname is returned.
     *
     * @return the player nickname
     */
    public String getPlayer() {
        return player;
    }

    /**
     * The selected offer tile id is returned.
     *
     * @return the offer tile id
     */
    public String getOfferTileId() {
        return offerTileId;
    }

    /**
     * The next player nickname is returned.
     *
     * @return the next player nickname
     */
    public String getNextPlayer() {
        return nextPlayer;
    }

    /**
     * The autoplay flag is returned.
     *
     * @return true if the placement was automatically played
     */
    public boolean isAutoPlayed() {
        return isAutoPlayed;
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
