package it.polimi.ingsw.am32.client.assets;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am32.dto.setup.*;

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
    private final Map<String, String> assetPathIndex = new HashMap<>();

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
     * @return the asset manager instance.
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
     * Assets are loaded from the specified json file.
     *
     * @param resourcePath the path of the json asset file.
     */
    public void loadAssets(String resourcePath) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try (InputStream is = AssetManager.class.getResourceAsStream("/" + resourcePath);) {
            if (is == null) throw new RuntimeException("asset file not found: " + resourcePath);

            // stream is read once into a tree node
            JsonNode rootNode = mapper.readTree(is);

            // asset path index is built from raw json, no DTO needed
            buildAssetPathIndex(rootNode);

            // root is converted to typed dto for the rest of the maps
            GameAssetsRoot root = mapper.treeToValue(rootNode, GameAssetsRoot.class);
            populateMaps(root);

        } catch (Exception e) {
            throw new RuntimeException("failed to load assets", e);
        }
    }

    /**
     * Internal maps are populated with the parsed data.
     *
     * @param root the parsed game assets root.
     */
    private void populateMaps(GameAssetsRoot root) {
        // building cards are mapped
        if (root.getBuildingCards() != null) {
            root.getBuildingCards().forEach(card -> buildingCards.put(card.getId(), card));
        }
        // character cards are mapped
        if (root.getCharacterCards() != null) {
            root.getCharacterCards().forEach(card -> characterCards.put(card.getId(), card));
        }
        // event cards are mapped
        if (root.getEventCards() != null) {
            root.getEventCards().forEach(card -> eventCards.put(card.getId(), card));
        }
        // offer tiles are mapped
        if (root.getOfferTiles() != null) {
            root.getOfferTiles().forEach(tile -> offerTiles.put(tile.getId(), tile));
        }
        // turn order tiles are mapped
        if (root.getTurnOrderTiles() != null) {
            root.getTurnOrderTiles().forEach(tile -> turnOrderTiles.put(tile.getId(), tile));
        }
        // ui assets are mapped
        if (root.getUiAssets() != null) {
            uiAssets.putAll(root.getUiAssets());
        }
        // card backs are mapped
        if (root.getCardBacks() != null) {
            cardBacks.putAll(root.getCardBacks());
        }
    }

    private void buildAssetPathIndex(JsonNode rootNode) {
        String[] entityArrays = {"building_cards", "character_cards", "event_cards", "offer_tiles", "turn_order_tiles"};

        for (String arrayField : entityArrays) {
            JsonNode array = rootNode.get(arrayField);
            if (array == null || !array.isArray()) continue;

            for (JsonNode item : array) {
                JsonNode id = item.get("id");
                JsonNode assetPath = item.get("asset_path");
                if (id != null && assetPath != null) {
                    assetPathIndex.put(id.asText(), assetPath.asText());
                }
            }
        }
    }

    /**
     * Building card is retrieved by its identifier.
     *
     * @param id the card identifier.
     * @return the building card dto.
     */
    public BuildingCardDTO getBuildingCard(String id) {
        // card is returned
        return buildingCards.get(id);
    }

    /**
     * Character card is retrieved by its identifier.
     *
     * @param id the card identifier.
     * @return the character card dto.
     */
    public CharacterCardDTO getCharacterCard(String id) {
        // card is returned
        return characterCards.get(id);
    }

    /**
     * Event card is retrieved by its identifier.
     *
     * @param id the card identifier.
     * @return the event card dto.
     */
    public EventCardDTO getEventCard(String id) {
        // card is returned
        return eventCards.get(id);
    }

    /**
     * Offer tile is retrieved by its identifier.
     *
     * @param id the tile identifier.
     * @return the offer tile dto.
     */
    public OfferTileDTO getOfferTile(String id) {
        // tile is returned
        return offerTiles.get(id);
    }

    /**
     * Turn order tile is retrieved by its identifier.
     *
     * @param id the tile identifier.
     * @return the turn order tile dto.
     */
    public TurnOrderTileDTO getTurnOrderTile(String id) {
        // tile is returned
        return turnOrderTiles.get(id);
    }

    /**
     * Ui asset path is retrieved by its key.
     *
     * @param key the ui asset key.
     * @return the relative path.
     */
    public String getUiAsset(String key) {
        // path is returned
        return uiAssets.get(key);
    }

    /**
     * Card back path is retrieved by its key.
     *
     * @param key the card back key.
     * @return the relative path.
     */
    public String getCardBack(String key) {
        // path is returned
        return cardBacks.get(key);
    }

    public String getAssetPath(String id) {
        return assetPathIndex.get(id);
    }
}
