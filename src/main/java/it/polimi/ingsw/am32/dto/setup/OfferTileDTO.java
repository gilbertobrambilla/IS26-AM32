package it.polimi.ingsw.am32.dto.setup;

/**
 * An offer tile setup data transfer object is represented.
 */
public class OfferTileDTO {
    private String id;
    private int minPlayers;
    private int drawsFromUpperRow;
    private int drawsFromLowerRow;
    private int foodBonus;
    private String assetPath;

    /**
     * Jackson initializes an offer tile setup data transfer object.
     */
    public OfferTileDTO() {
        // default initialization is performed by jackson
    }

    /**
     * An offer tile setup data transfer object is initialized with full tile data.
     *
     * @param id the id of the offer tile
     * @param minPlayers the minimum number of players
     * @param drawsFromUpperRow the number of cards to draw from the upper row
     * @param drawsFromLowerRow the number of cards to draw from the lower row
     * @param foodBonus the food bonus granted by the tile
     * @param assetPath the path to the tile asset
     */
    public OfferTileDTO(String id, int minPlayers, int drawsFromUpperRow, int drawsFromLowerRow, int foodBonus, String assetPath) {
        // fields are assigned
        this.id = id;
        this.minPlayers = minPlayers;
        this.drawsFromUpperRow = drawsFromUpperRow;
        this.drawsFromLowerRow = drawsFromLowerRow;
        this.foodBonus = foodBonus;
        this.assetPath = assetPath;
    }

    /**
     * The offer tile id is returned.
     *
     * @return the id of the offer tile
     */
    public String getId() {
        return this.id;
    }

    /**
     * The offer tile id is set.
     *
     * @param id the id of the offer tile
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * The minimum number of players is returned.
     *
     * @return the minimum players
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
     * The upper row draw count is returned.
     *
     * @return the number of cards to draw from the upper row
     */
    public int getDrawsFromUpperRow() {
        return this.drawsFromUpperRow;
    }

    /**
     * The upper row draw count is set.
     *
     * @param drawsFromUpperRow the number of cards to draw from the upper row
     */
    public void setDrawsFromUpperRow(int drawsFromUpperRow) {
        this.drawsFromUpperRow = drawsFromUpperRow;
    }

    /**
     * The lower row draw count is returned.
     *
     * @return the number of cards to draw from the lower row
     */
    public int getDrawsFromLowerRow() {
        return this.drawsFromLowerRow;
    }

    /**
     * The lower row draw count is set.
     *
     * @param drawsFromLowerRow the number of cards to draw from the lower row
     */
    public void setDrawsFromLowerRow(int drawsFromLowerRow) {
        this.drawsFromLowerRow = drawsFromLowerRow;
    }

    /**
     * The food bonus is returned.
     *
     * @return the food bonus
     */
    public int getFoodBonus() {
        return this.foodBonus;
    }

    /**
     * The food bonus is set.
     *
     * @param foodBonus the food bonus
     */
    public void setFoodBonus(int foodBonus) {
        this.foodBonus = foodBonus;
    }

    /**
     * The tile asset path is returned.
     *
     * @return the asset path
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
