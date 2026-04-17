package it.polimi.ingsw.am32.dto.setup;

public class OfferTileDTO {
    private final String id;
    private final int minPlayers;
    private final int actDrawsUpper;
    private final int actDrawsLower;
    private final int actFoodBonus;

    public OfferTileDTO(String id, int minPlayers, int actDrawsUpper, int actDrawsLower, int actFoodBonus) {
        this.id = id;
        this.minPlayers = minPlayers;
        this.actDrawsUpper = actDrawsUpper;
        this.actDrawsLower = actDrawsLower;
        this.actFoodBonus = actFoodBonus;
    }

    public String getId() {
        return this.id;
    }

    public int getMinPlayers() {
        return this.minPlayers;
    }

    public int getActDrawsUpper() {
        return this.actDrawsUpper;
    }

    public int getActDrawsLower() {
        return this.actDrawsLower;
    }

    public int getActFoodBonus() {
        return this.actFoodBonus;
    }
}
