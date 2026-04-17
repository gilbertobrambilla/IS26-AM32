package it.polimi.ingsw.am32.dto.setup;

public class BuildingCardDTO {
    private final String id;
    private final int era;
    private final int basePrestige;
    private final int cost;
    private final String effectCode;
    private final String effectParams;

    public BuildingCardDTO(String id, int era, int basePrestige, int cost, String effectCode, String effectParams) {
        this.id = id;
        this.era = era;
        this.basePrestige = basePrestige;
        this.cost = cost;
        this.effectCode = effectCode;
        this.effectParams = effectParams;
    }

    public String getId() {
        return this.id;
    }

    public int getEra() {
        return this.era;
    }

    public int getBasePrestige() {
        return this.basePrestige;
    }

    public int getCost() {
        return this.cost;
    }

    public String getEffectCode() {
        return this.effectCode;
    }

    public String getEffectParams() {
        return this.effectParams;
    }
}
