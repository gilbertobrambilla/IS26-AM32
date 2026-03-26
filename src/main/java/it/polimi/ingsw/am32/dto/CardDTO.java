package it.polimi.ingsw.am32.dto;

public class CardDTO {
    private final String id;
    private final int era;
    private final int cost;
    private final int prestigePoints;
    private final String type;

    public CardDTO(String id, int era, int cost, int prestigePoints, String type) {
        this.id = id;
        this.era = era;
        this.cost = cost;
        this.prestigePoints = prestigePoints;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public int getEra() {
        return era;
    }

    public int getCost() {
        return cost;
    }

    public int getPrestigePoints() {
        return prestigePoints;
    }

    public String getType() {
        return type;
    }
}
