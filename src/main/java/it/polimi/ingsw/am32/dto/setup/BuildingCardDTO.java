package it.polimi.ingsw.am32.dto.setup;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * A building card setup data transfer object is represented.
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
     * A building card setup data transfer object is initialized by Jackson.
     */
    public BuildingCardDTO() {
        // default initialization is performed by jackson
    }

    /**
     * A building card setup data transfer object is initialized with full card data.
     *
     * @param id the building card id
     * @param era the building card era
     * @param basePrestige the base prestige
     * @param cost the building card cost
     * @param effectCode the effect code
     * @param effectParams the effect parameters as a json node
     * @param assetPath the card asset path
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
     * The building card id is returned.
     *
     * @return the building card id
     */
    public String getId() {
        return this.id;
    }

    /**
     * The building card id is set.
     *
     * @param id the building card id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * The building card era is returned.
     *
     * @return the building card era
     */
    public int getEra() {
        return this.era;
    }

    /**
     * The building card era is set.
     *
     * @param era the building card era
     */
    public void setEra(int era) {
        this.era = era;
    }

    /**
     * The base prestige is returned.
     *
     * @return the base prestige
     */
    public int getBasePrestige() {
        return this.basePrestige;
    }

    /**
     * The base prestige is set.
     *
     * @param basePrestige the base prestige
     */
    public void setBasePrestige(int basePrestige) {
        this.basePrestige = basePrestige;
    }

    /**
     * The building card cost is returned.
     *
     * @return the building card cost
     */
    public int getCost() {
        return this.cost;
    }

    /**
     * The building card cost is set.
     *
     * @param cost the building card cost
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * The effect code is returned.
     *
     * @return the effect code
     */
    public String getEffectCode() {
        return this.effectCode;
    }

    /**
     * The effect code is set.
     *
     * @param effectCode the effect code
     */
    public void setEffectCode(String effectCode) {
        this.effectCode = effectCode;
    }

    /**
     * The effect parameters are returned.
     *
     * @return the effect parameters as a json node
     */
    public JsonNode getEffectParams() {
        return this.effectParams;
    }

    /**
     * The effect parameters are set.
     *
     * @param effectParams the effect parameters as a json node
     */
    public void setEffectParams(JsonNode effectParams) {
        this.effectParams = effectParams;
    }

    /**
     * The card asset path is returned.
     *
     * @return the card asset path
     */
    public String getAssetPath() {
        return this.assetPath;
    }

    /**
     * The card asset path is set.
     *
     * @param assetPath the card asset path
     */
    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }
}
