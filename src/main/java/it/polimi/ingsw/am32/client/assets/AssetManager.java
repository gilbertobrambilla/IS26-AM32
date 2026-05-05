package it.polimi.ingsw.am32.client.assets;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am32.dto.setup.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Asset manager is implemented to store and provide game assets.
 */
public class AssetManager {

    private final Map<String, BuildingCardDTO> buildingCards;
    private final Map<String, CharacterCardDTO> characterCards;
    private final Map<String, EventCardDTO> eventCards;
    private final Map<String, OfferTileDTO> offerTiles;
    private final Map<String, TurnOrderTileDTO> turnOrderTiles;

    /**
     * Asset manager is instantiated.
     */
    public AssetManager() {
        // internal maps are initialized
        this.buildingCards = new HashMap<>();
        this.characterCards = new HashMap<>();
        this.eventCards = new HashMap<>();
        this.offerTiles = new HashMap<>();
        this.turnOrderTiles = new HashMap<>();
    }

    /**
     * Assets are loaded from the specified json file.
     *
     * @param resourcePath the path of the json asset file.
     */
    public void loadAssets(String resourcePath) {
        // object mapper is created
        ObjectMapper mapper = new ObjectMapper();
        // unknown properties are ignored
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            // input stream is opened from resources
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (is != null) {
                // json is deserialized into the root object
                GameAssetsRoot root = mapper.readValue(is, GameAssetsRoot.class);
                // maps are populated
                populateMaps(root);
            }
        } catch (Exception e) {
            // exception is ignored or logged safely
            System.err.println("assets could not be loaded");
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
}
