package it.polimi.ingsw.am32.client.model;

import it.polimi.ingsw.am32.client.assets.AssetManager;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A lightweight client-side player model is represented.
 */
public class LightPlayer {
    private final String nickname;
    private final TotemColor totemColor;
    private int food;
    private int prestige;
    private boolean isConnected;
    private final List<String> characterCards;
    private final List<String> buildingCards;

    /**
     * A light player is created from server-provided values.
     *
     * @param nickname the player nickname
     * @param totemColor the player totem color
     * @param food the current food amount
     * @param prestige the current prestige amount
     * @param isConnected true if the player is connected, false otherwise
     * @param characterCards the acquired character card identifiers
     * @param buildingCards the acquired building card identifiers
     */
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
     * An additional building card is checked against the player's available food.
     * The cumulative cost of buildings already selected in the ui is considered.
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
     * The discounted cost of a single building is calculated for this specific player.
     *
     * @param buildingId the building card identifier
     * @return the discounted building cost
     */
    private int getSingleBuildingCost(String buildingId) {
        var buildingDTO = AssetManager.getInstance().getBuildingCard(buildingId);
        if (buildingDTO == null) return 0;

        int baseCost = buildingDTO.getCost();

        // builder discounts from owned characters are summed
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

    /**
     * The player nickname is returned.
     *
     * @return the player nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * The player totem color is returned.
     *
     * @return the player totem color
     */
    public TotemColor getTotemColor() {
        return totemColor;
    }

    /**
     * The current food amount is returned.
     *
     * @return the current food amount
     */
    public int getFood() {
        return food;
    }

    /**
     * The current prestige amount is returned.
     *
     * @return the current prestige amount
     */
    public int getPrestige() {
        return prestige;
    }

    /**
     * The connection status is returned.
     *
     * @return true if the player is connected, false otherwise
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * The acquired character cards are returned as an unmodifiable list.
     *
     * @return the acquired character card identifiers
     */
    public List<String> getCharacterCards() {
        return Collections.unmodifiableList(characterCards);
    }

    /**
     * The acquired building cards are returned as an unmodifiable list.
     *
     * @return the acquired building card identifiers
     */
    public List<String> getBuildingCards() {
        return Collections.unmodifiableList(buildingCards);
    }

    /**
     * Food and prestige values are updated.
     *
     * @param food the new food amount
     * @param prestige the new prestige amount
     */
    public void updateStats(int food, int prestige) {
        this.food = food;
        this.prestige = prestige;
    }

    /**
     * The connection status is updated.
     *
     * @param connected true if the player is connected, false otherwise
     */
    public void setConnected(boolean connected) {
        this.isConnected = connected;
    }

    /**
     * A character card identifier is added if available.
     *
     * @param cardId the character card identifier
     */
    public void addCharacterCard(String cardId) {
        if (cardId != null) this.characterCards.add(cardId);
    }

    /**
     * A building card identifier is added if available.
     *
     * @param cardId the building card identifier
     */
    public void addBuildingCard(String cardId) {
        if (cardId != null) this.buildingCards.add(cardId);
    }
}
