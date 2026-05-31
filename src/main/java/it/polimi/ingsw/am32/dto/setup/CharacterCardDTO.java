package it.polimi.ingsw.am32.dto.setup;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * A character card setup data transfer object is represented.
 */
public class CharacterCardDTO {
    private String id;
    private int era;
    private int basePrestige;
    private int minPlayers;
    private String characterType;
    private JsonNode characterParams;
    private String assetPath;

    /**
     * A character card setup data transfer object is initialized by Jackson.
     */
    public CharacterCardDTO() {
        // default initialization is performed by jackson
    }

    /**
     * A character card setup data transfer object is initialized with full card data.
     *
     * @param id the character card id
     * @param era the character card era
     * @param basePrestige the base prestige
     * @param minPlayers the minimum number of players
     * @param characterType the character type
     * @param characterParams the character parameters as a json node
     * @param assetPath the card asset path
     */
    public CharacterCardDTO(String id, int era, int basePrestige, int minPlayers, String characterType, JsonNode characterParams, String assetPath) {
        this.id = id;
        this.era = era;
        this.basePrestige = basePrestige;
        this.minPlayers = minPlayers;
        this.characterType = characterType;
        this.characterParams = characterParams;
        this.assetPath = assetPath;
    }

    /**
     * The character card id is returned.
     *
     * @return the character card id
     */
    public String getId() {
        return this.id;
    }

    /**
     * The character card id is set.
     *
     * @param id the character card id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * The character card era is returned.
     *
     * @return the character card era
     */
    public int getEra() {
        return this.era;
    }

    /**
     * The character card era is set.
     *
     * @param era the character card era
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
     * The minimum number of players is returned.
     *
     * @return the minimum number of players
     */
    public int getMinPlayers() {
        return this.minPlayers;
    }

    /**
     * The minimum number of players is set.
     *
     * @param minPlayers the minimum number of players
     */
    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    /**
     * The character type is returned.
     *
     * @return the character type
     */
    public String getCharacterType() {
        return this.characterType;
    }

    /**
     * The character type is set.
     *
     * @param characterType the character type
     */
    public void setCharacterType(String characterType) {
        this.characterType = characterType;
    }

    /**
     * The character parameters are returned.
     *
     * @return the character parameters as a json node
     */
    public JsonNode getCharacterParams() {
        return this.characterParams;
    }

    /**
     * The character parameters are set.
     *
     * @param characterParams the character parameters as a json node
     */
    public void setCharacterParams(JsonNode characterParams) {
        this.characterParams = characterParams;
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
