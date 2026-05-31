package it.polimi.ingsw.am32.dto.setup;

import java.util.List;

/**
 * A turn order tile setup data transfer object is represented.
 */
public class TurnOrderTileDTO {
    private String id;
    private int requiredPlayers;
    private List<Integer> foodModifiers;
    private List<Integer> prestigeModifiers;
    private String assetPath;

    /**
     * A turn order tile setup data transfer object is initialized by Jackson.
     */
    public TurnOrderTileDTO() {
        // default initialization is performed by jackson
    }

    /**
     * A turn order tile setup data transfer object is initialized with full tile data.
     *
     * @param id the turn order tile id
     * @param requiredPlayers the number of required players
     * @param foodModifiers the food modifiers
     * @param prestigeModifiers the prestige modifiers
     * @param assetPath the tile asset path
     */
    public TurnOrderTileDTO(String id, int requiredPlayers, List<Integer> foodModifiers, List<Integer> prestigeModifiers, String assetPath) {
        this.id = id;
        this.requiredPlayers = requiredPlayers;
        this.foodModifiers = foodModifiers;
        this.prestigeModifiers = prestigeModifiers;
        this.assetPath = assetPath;
    }

    /**
     * The turn order tile id is returned.
     *
     * @return the turn order tile id
     */
    public String getId() {
        return this.id;
    }

    /**
     * The turn order tile id is set.
     *
     * @param id the turn order tile id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * The number of required players is returned.
     *
     * @return the number of required players
     */
    public int getRequiredPlayers() {
        return this.requiredPlayers;
    }

    /**
     * The number of required players is set.
     *
     * @param requiredPlayers the number of required players
     */
    public void setRequiredPlayers(int requiredPlayers) {
        this.requiredPlayers = requiredPlayers;
    }

    /**
     * The food modifiers are returned.
     *
     * @return the food modifiers
     */
    public List<Integer> getFoodModifiers() {
        return this.foodModifiers;
    }

    /**
     * The food modifiers are set.
     *
     * @param foodModifiers the food modifiers
     */
    public void setFoodModifiers(List<Integer> foodModifiers) {
        this.foodModifiers = foodModifiers;
    }

    /**
     * The prestige modifiers are returned.
     *
     * @return the prestige modifiers
     */
    public List<Integer> getPrestigeModifiers() {
        return this.prestigeModifiers;
    }

    /**
     * The prestige modifiers are set.
     *
     * @param prestigeModifiers the prestige modifiers
     */
    public void setPrestigeModifiers(List<Integer> prestigeModifiers) {
        this.prestigeModifiers = prestigeModifiers;
    }

    /**
     * The tile asset path is returned.
     *
     * @return the tile asset path
     */
    public String getAssetPath() {
        return this.assetPath;
    }

    /**
     * The tile asset path is set.
     *
     * @param assetPath the tile asset path
     */
    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }
}
