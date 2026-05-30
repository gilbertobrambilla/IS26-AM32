package it.polimi.ingsw.am32.client.model;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LightPlayer {
    private final String nickname;
    private final TotemColor totemColor;
    private int food;
    private int prestige;
    private boolean isConnected;
    private final List<String> characterCards;
    private final List<String> buildingCards;

    public LightPlayer(String nickname, TotemColor totemColor, int food, int prestige, boolean isConnected, List<String> characterCards, List<String> buildingCards) {
        this.nickname = nickname;
        this.totemColor = totemColor;
        this.food = food;
        this.prestige = prestige;
        this.isConnected = isConnected;
        this.characterCards = new ArrayList<>(characterCards);
        this.buildingCards = new ArrayList<>(buildingCards);
    }

    /**
     * Checks if the player can afford an additional building card,
     * considering the cumulative cost of the buildings they have already selected in the UI.
     *
     * @param alreadySelectedBuildingIds the list of building IDs already selected in the UI
     * @param additionalBuildingId the extra building ID to evaluate
     * @return true if the player has enough food, false otherwise
     */
    public boolean canAffordAdditionalBuilding(List<String> alreadySelectedBuildingIds, String additionalBuildingId) {
        if (additionalBuildingId == null || !additionalBuildingId.startsWith("building_")) {
            return true;
        }

        int totalCost = 0;
        for (String id : alreadySelectedBuildingIds) {
            totalCost += getSingleBuildingCost(id);
        }
        int additionalCost = getSingleBuildingCost(additionalBuildingId);

        return (totalCost + additionalCost) <= this.getFood();
    }

    /**
     * Calculates the discounted cost of a single building for this specific player.
     */
    private int getSingleBuildingCost(String buildingId) {
        var buildingDTO = AssetManager.getInstance().getBuildingCard(buildingId);
        if (buildingDTO == null) return 0;

        int baseCost = buildingDTO.getCost();

        // Sum the buildingDiscount values of all "Builder" characters owned by the player
        int discount = this.getCharacterCards().stream()
                .map(id -> AssetManager.getInstance().getCharacterCard(id))
                .filter(dto -> dto != null && "BUILDER".equals(dto.getCharacterType()))
                .mapToInt(dto -> {
                    var params = dto.getCharacterParams();
                    if (params != null && params.has("buildingDiscount")) {
                        return params.get("buildingDiscount").asInt();
                    }
                    return 0;
                })
                .sum();

        return Math.max(0, baseCost - discount);
    }

    public String getNickname() {
        return nickname;
    }

    public TotemColor getTotemColor() {
        return totemColor;
    }

    public int getFood() {
        return food;
    }

    public int getPrestige() {
        return prestige;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public List<String> getCharacterCards() {
        return Collections.unmodifiableList(characterCards);
    }

    public List<String> getBuildingCards() {
        return Collections.unmodifiableList(buildingCards);
    }

    public void updateStats(int food, int prestige) {
        this.food = food;
        this.prestige = prestige;
    }

    public void setConnected(boolean connected) {
        this.isConnected = connected;
    }

    public void addCharacterCard(String cardId) {
        if (cardId != null) this.characterCards.add(cardId);
    }

    public void addBuildingCard(String cardId) {
        if (cardId != null) this.buildingCards.add(cardId);
    }
}
