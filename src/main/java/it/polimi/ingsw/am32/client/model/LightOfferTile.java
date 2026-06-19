package it.polimi.ingsw.am32.client.model;

/**
 * A lightweight client-side offer tile model is represented.
 */
public class LightOfferTile {
    private final String id;
    private String occupyingPlayer; // null if free

    /**
     * A light offer tile is created.
     *
     * @param tileId the offer tile identifier
     * @param occupyingPlayer the occupying player nickname, or null if free
     */
    public LightOfferTile(String tileId, String occupyingPlayer) {
        this.id = tileId;
        this.occupyingPlayer = occupyingPlayer;
    }

    /**
     * The offer tile identifier is returned.
     *
     * @return the offer tile identifier
     */
    public String getId() { return id; }

    /**
     * The occupying player nickname is returned.
     *
     * @return the occupying player nickname, or null if free
     */
    public String getOccupyingPlayer() { return occupyingPlayer; }

    /**
     * The occupying player nickname is updated.
     *
     * @param nickname the occupying player nickname, or null if free
     */
    public void setOccupyingPlayer(String nickname) {
        this.occupyingPlayer = nickname;
    }
}
