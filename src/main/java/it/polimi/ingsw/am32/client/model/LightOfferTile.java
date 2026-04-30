package it.polimi.ingsw.am32.client.model;

public class LightOfferTile {
    private final String id;
    private String occupyingPlayer; // null if free

    public LightOfferTile(String tileId, String occupyingPlayer) {
        this.id = tileId;
        this.occupyingPlayer = occupyingPlayer;
    }

    public String getId() { return id; }
    public String getOccupyingPlayer() { return occupyingPlayer; }

    public void setOccupyingPlayer(String nickname) {
        this.occupyingPlayer = nickname;
    }
}
