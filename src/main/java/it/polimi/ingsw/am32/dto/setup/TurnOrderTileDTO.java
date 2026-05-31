package it.polimi.ingsw.am32.dto.setup;

import java.util.List;

/**
 * Data transfer object representing a turn order tile.
 */
public class TurnOrderTileDTO {
    private String id;
    private int requiredPlayers;
    private List<Integer> foodModifiers;
    private List<Integer> prestigeModifiers;
    private String assetPath;

    /**
     * Default constructor.
     */
    public TurnOrderTileDTO() {
        // default initialization is performed by jackson
    }

    /**
     * Constructor with all parameters.
     *
     * @param id The id of the turn order tile.
     * @param requiredPlayers The number of required players.
     * @param foodModifiers The food modifiers as a list of integers.
     * @param prestigeModifiers The prestige modifiers as a list of integers.
     * @param assetPath The path to the tile asset.
     */
    public TurnOrderTileDTO(String id, int requiredPlayers, List<Integer> foodModifiers, List<Integer> prestigeModifiers, String assetPath) {
        this.id = id;
        this.requiredPlayers = requiredPlayers;
        this.foodModifiers = foodModifiers;
        this.prestigeModifiers = prestigeModifiers;
        this.assetPath = assetPath;
    }

    /**
     * Gets the id.
     *
     * @return The id of the turn order tile.
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
     * Gets the required players.
     *
     * @return The required players.
     */
    public int getRequiredPlayers() {
        return this.requiredPlayers;
    }

    /**
     * Sets the required players.
     *
     * @param requiredPlayers The new required players to set.
     */
    public void setRequiredPlayers(int requiredPlayers) {
        this.requiredPlayers = requiredPlayers;
    }

    /**
     * Gets the food modifiers.
     *
     * @return The food modifiers.
     */
    public List<Integer> getFoodModifiers() {
        return this.foodModifiers;
    }

    /**
     * Sets the food modifiers.
     *
     * @param foodModifiers The new food modifiers to set.
     */
    public void setFoodModifiers(List<Integer> foodModifiers) {
        this.foodModifiers = foodModifiers;
    }

    /**
     * Gets the prestige modifiers.
     *
     * @return The prestige modifiers.
     */
    public List<Integer> getPrestigeModifiers() {
        return this.prestigeModifiers;
    }

    /**
     * Sets the prestige modifiers.
     *
     * @param prestigeModifiers The new prestige modifiers to set.
     */
    public void setPrestigeModifiers(List<Integer> prestigeModifiers) {
        this.prestigeModifiers = prestigeModifiers;
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
