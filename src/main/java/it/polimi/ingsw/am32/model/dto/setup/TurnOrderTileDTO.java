package it.polimi.ingsw.am32.model.dto.setup;

public class TurnOrderTileDTO {
    private final String id;
    private final int requiredPlayers;
    private final String foodModifiers;
    private final String prestigeModifiers;

    public TurnOrderTileDTO(String id, int requiredPlayers, String foodModifiers, String prestigeModifiers) {
        this.id = id;
        this.requiredPlayers = requiredPlayers;
        this.foodModifiers = foodModifiers;
        this.prestigeModifiers = prestigeModifiers;
    }

    public String getId() {
        return this.id;
    }

    public int getRequiredPlayers() {
        return this.requiredPlayers;
    }

    public String getFoodModifiers() {
        return this.foodModifiers;
    }

    public String getPrestigeModifiers() {
        return this.prestigeModifiers;
    }
}
