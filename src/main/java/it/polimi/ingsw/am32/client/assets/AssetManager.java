package it.polimi.ingsw.am32.client.assets;

import it.polimi.ingsw.am32.dto.setup.*;
import it.polimi.ingsw.am32.utils.JsonAssetLoader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Asset manager is implemented to store and provide game assets.
 */
public class AssetManager {
    private static AssetManager instance;

    private final Map<String, BuildingCardDTO> buildingCards;
    private final Map<String, CharacterCardDTO> characterCards;
    private final Map<String, EventCardDTO> eventCards;
    private final Map<String, OfferTileDTO> offerTiles;
    private final Map<String, TurnOrderTileDTO> turnOrderTiles;
    private final Map<String, String> uiAssets;
    private final Map<String, String> cardBacks;

    /**
     * Asset manager is instantiated.
     */
    private AssetManager() {
        // internal maps are initialized
        this.buildingCards = new HashMap<>();
        this.characterCards = new HashMap<>();
        this.eventCards = new HashMap<>();
        this.offerTiles = new HashMap<>();
        this.turnOrderTiles = new HashMap<>();
        this.uiAssets = new HashMap<>();
        this.cardBacks = new HashMap<>();
    }

    /**
     * Singleton instance is retrieved.
     *
     * @return the asset manager instance
     */
    public static AssetManager getInstance() {
        // instance is created if null
        if (instance == null) {
            instance = new AssetManager();
        }
        // instance is returned
        return instance;
    }

    /**
     * Assets are loaded from the shared JSON loader.
     */
    public void loadAssets() {
        // shared loader instance is retrieved
        JsonAssetLoader loader = JsonAssetLoader.getInstance();

        // building cards are mapped
        loader.getBuildingCards().forEach(card -> buildingCards.put(card.getId(), card));

        // character cards are mapped
        loader.getCharacterCards().forEach(card -> characterCards.put(card.getId(), card));

        // event cards are mapped
        loader.getEventCards().forEach(card -> eventCards.put(card.getId(), card));

        // offer tiles are mapped
        loader.getOfferTiles().forEach(tile -> offerTiles.put(tile.getId(), tile));

        // turn order tiles are mapped
        loader.getTurnOrderTiles().forEach(tile -> turnOrderTiles.put(tile.getId(), tile));

        // ui assets are mapped
        uiAssets.putAll(loader.getUiAssets());

        // card backs are mapped
        cardBacks.putAll(loader.getCardBacks());
    }

    /**
     * The raw input stream for the dictionary file is retrieved directly from the classloader.
     *
     * @param resourcePath the path of the JSON dictionary file
     * @return the input stream of the requested file
     */
    public InputStream getDictionaryStream(String resourcePath) {
        // input stream is opened using the class loader
        return AssetManager.class.getResourceAsStream(resourcePath);
    }

    /**
     * Building card is retrieved by its identifier.
     *
     * @param id the card identifier
     * @return the building card dto
     */
    public BuildingCardDTO getBuildingCard(String id) {
        // card is returned
        return buildingCards.get(id);
    }

    /**
     * Character card is retrieved by its identifier.
     *
     * @param id the card identifier
     * @return the character card dto
     */
    public CharacterCardDTO getCharacterCard(String id) {
        // card is returned
        return characterCards.get(id);
    }

    /**
     * Event card is retrieved by its identifier.
     *
     * @param id the card identifier
     * @return the event card dto
     */
    public EventCardDTO getEventCard(String id) {
        // card is returned
        return eventCards.get(id);
    }

    /**
     * Offer tile is retrieved by its identifier.
     *
     * @param id the tile identifier
     * @return the offer tile dto
     */
    public OfferTileDTO getOfferTile(String id) {
        // tile is returned
        return offerTiles.get(id);
    }

    /**
     * Turn order tile is retrieved by its identifier.
     *
     * @param id the tile identifier
     * @return the turn order tile dto
     */
    public TurnOrderTileDTO getTurnOrderTile(String id) {
        // tile is returned
        return turnOrderTiles.get(id);
    }

    /**
     * Ui asset path is retrieved by its key.
     *
     * @param key the ui asset key
     * @return the relative path
     */
    public String getUiAsset(String key) {
        // path is returned
        return uiAssets.get(key);
    }

    /**
     * Card back path is retrieved by its key.
     *
     * @param key the card back key
     * @return the relative path
     */
    public String getCardBack(String key) {
        // path is returned
        return cardBacks.get(key);
    }
}
