package it.polimi.ingsw.am32.dto.setup;

/**
 * Data transfer object representing an offer tile.
 */
public class OfferTileDTO {
    private String id;
    private int minPlayers;
    private int drawsFromUpperRow;
    private int drawsFromLowerRow;
    private int foodBonus;
    private String assetPath;

    /**
     * Default constructor.
     */
    public OfferTileDTO() {
        // default initialization is performed by jackson
    }

    /**
     * Constructor with all parameters.
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
     * Gets the id.
     *
     * @return the id of the offer tile
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the minimum players.
     *
     * @return the minimum players
     */
    public int getMinPlayers() {
        return this.minPlayers;
    }

    /**
     * Sets the minimum players.
     *
     * @param minPlayers the new minimum players to set
     */
    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    /**
     * Gets the upper row draws.
     *
     * @return the number of cards to draw from the upper row
     */
    public int getDrawsFromUpperRow() {
        return this.drawsFromUpperRow;
    }

    /**
     * Sets the upper row draws.
     *
     * @param drawsFromUpperRow the new number of draws to set
     */
    public void setDrawsFromUpperRow(int drawsFromUpperRow) {
        this.drawsFromUpperRow = drawsFromUpperRow;
    }

    /**
     * Gets the lower row draws.
     *
     * @return the number of cards to draw from the lower row
     */
    public int getDrawsFromLowerRow() {
        return this.drawsFromLowerRow;
    }

    /**
     * Sets the lower row draws.
     *
     * @param drawsFromLowerRow the new number of draws to set
     */
    public void setDrawsFromLowerRow(int drawsFromLowerRow) {
        this.drawsFromLowerRow = drawsFromLowerRow;
    }

    /**
     * Gets the food bonus.
     *
     * @return the food bonus
     */
    public int getFoodBonus() {
        return this.foodBonus;
    }

    /**
     * Sets the food bonus.
     *
     * @param foodBonus the new food bonus to set
     */
    public void setFoodBonus(int foodBonus) {
        this.foodBonus = foodBonus;
    }

    /**
     * Gets the asset path.
     *
     * @return the asset path
     */
    public String getAssetPath() {
        return this.assetPath;
    }

    /**
     * Sets the asset path.
     *
     * @param assetPath the new asset path to set
     */
    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }
}
