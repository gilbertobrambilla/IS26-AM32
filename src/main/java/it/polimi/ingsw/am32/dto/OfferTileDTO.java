package it.polimi.ingsw.am32.dto;

public class OfferTileDTO {
    private final String tileId;
    private final PlayerIdentificationDTO occupyingPlayer;

    public OfferTileDTO(String tileId, PlayerIdentificationDTO occupyingPlayer) {
        this.tileId = tileId;
        this.occupyingPlayer = occupyingPlayer;
    }

    public String getTileId() {
        return tileId;
    }

    public PlayerIdentificationDTO getOccupyingPlayer() {
        return occupyingPlayer;
    }
}
