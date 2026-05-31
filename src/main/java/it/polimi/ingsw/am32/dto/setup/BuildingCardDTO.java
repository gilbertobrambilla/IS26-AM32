package it.polimi.ingsw.am32.dto.setup;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Data transfer object representing a building card.
 */
public class BuildingCardDTO {
    private String id;
    private int era;
    private int basePrestige;
    private int cost;
    private String effectCode;
    private JsonNode effectParams;
    private String assetPath;

    /**
     * Default constructor.
     */
    public BuildingCardDTO() {
        // default initialization is performed by jackson
    }

    /**
     * Constructor with all parameters.
     *
     * @param id The id of the building card.
     * @param era The era of the building card.
     * @param basePrestige The base prestige.
     * @param cost The cost of the building card.
     * @param effectCode The effect code.
     * @param effectParams The effect parameters as a JSON node.
     * @param assetPath The path to the card asset.
     */
    public BuildingCardDTO(String id, int era, int basePrestige, int cost, String effectCode, JsonNode effectParams, String assetPath) {
        this.id = id;
        this.era = era;
        this.basePrestige = basePrestige;
        this.cost = cost;
        this.effectCode = effectCode;
        this.effectParams = effectParams;
        this.assetPath = assetPath;
    }

    /**
     * Gets the id.
     *
     * @return The id of the building card.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the id.
     *
     * @param id The new id to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the era.
     *
     * @return The era of the building card.
     */
    public int getEra() {
        return this.era;
    }

    /**
     * Sets the era.
     *
     * @param era The new era to set.
     */
    public void setEra(int era) {
        this.era = era;
    }

    /**
     * Gets the base prestige.
     *
     * @return The base prestige.
     */
    public int getBasePrestige() {
        return this.basePrestige;
    }

    /**
     * Sets the base prestige.
     *
     * @param basePrestige The new base prestige to set.
     */
    public void setBasePrestige(int basePrestige) {
        this.basePrestige = basePrestige;
    }

    /**
     * Gets the cost.
     *
     * @return The cost of the building card.
     */
    public int getCost() {
        return this.cost;
    }

    /**
     * Sets the cost.
     *
     * @param cost The new cost to set.
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * Gets the effect code.
     *
     * @return The effect code.
     */
    public String getEffectCode() {
        return this.effectCode;
    }

    /**
     * Sets the effect code.
     *
     * @param effectCode The new effect code to set.
     */
    public void setEffectCode(String effectCode) {
        this.effectCode = effectCode;
    }

    /**
     * Gets the effect parameters.
     *
     * @return The effect parameters as a JSON node.
     */
    public JsonNode getEffectParams() {
        return this.effectParams;
    }

    /**
     * Sets the effect parameters.
     *
     * @param effectParams The new effect parameters to set.
     */
    public void setEffectParams(JsonNode effectParams) {
        this.effectParams = effectParams;
    }

    /**
     * Gets the asset path.
     *
     * @return The asset path.
     */
    public String getAssetPath() {
        return this.assetPath;
    }

    /**
     * Sets the asset path.
     *
     * @param assetPath The new asset path to set.
     */
    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }
}
