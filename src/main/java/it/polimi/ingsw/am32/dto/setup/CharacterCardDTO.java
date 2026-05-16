package it.polimi.ingsw.am32.dto.setup;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Data transfer object representing a character card.
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
     * Default constructor.
     */
    public CharacterCardDTO() {
        // default initialization is performed by jackson
    }

    /**
     * Constructor with all parameters.
     *
     * @param id The id of the character card.
     * @param era The era of the character card.
     * @param basePrestige The base prestige.
     * @param minPlayers The minimum number of players.
     * @param characterType The character type.
     * @param characterParams The character parameters as a JSON node.
     * @param assetPath The path to the card asset.
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
     * Gets the id.
     *
     * @return The id of the character card.
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
     * @return The era of the character card.
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
     * Gets the minimum players.
     *
     * @return The minimum players.
     */
    public int getMinPlayers() {
        return this.minPlayers;
    }

    /**
     * Sets the minimum players.
     *
     * @param minPlayers The new minimum players to set.
     */
    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    /**
     * Gets the character type.
     *
     * @return The character type.
     */
    public String getCharacterType() {
        return this.characterType;
    }

    /**
     * Sets the character type.
     *
     * @param characterType The new character type to set.
     */
    public void setCharacterType(String characterType) {
        this.characterType = characterType;
    }

    /**
     * Gets the character parameters.
     *
     * @return The character parameters as a JSON node.
     */
    public JsonNode getCharacterParams() {
        return this.characterParams;
    }

    /**
     * Sets the character parameters.
     *
     * @param characterParams The new character parameters to set.
     */
    public void setCharacterParams(JsonNode characterParams) {
        this.characterParams = characterParams;
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
