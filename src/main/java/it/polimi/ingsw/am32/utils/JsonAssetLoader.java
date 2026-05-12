package it.polimi.ingsw.am32.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am32.dto.setup.*;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Utility class responsible for loading and parsing the assets json file.
 */
public class JsonAssetLoader {
    private static JsonAssetLoader instance;
    private final ObjectMapper objectMapper;
    private final JsonNode rootNode;

    /**
     * Private constructor to enforce the singleton pattern.
     */
    private JsonAssetLoader() {
        // object mapper is initialized
        this.objectMapper = new ObjectMapper();

        InputStream is = null;

        try {
            // input stream is opened for assets
            is = getClass().getResourceAsStream("/assets/assets.json");

            // exception is thrown if file is not found
            if (is == null) {
                throw new RuntimeException("/assets/assets.json file not found");
            }

            // json file is parsed into a tree model
            this.rootNode = objectMapper.readTree(is);

        } catch (Exception e) {
            // runtime exception is thrown if parsing fails
            throw new RuntimeException("error reading /assets/assets.json", e);
        } finally {
            // input stream is safely closed
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    // exception during closure is caught and logged
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns the singleton instance of the loader.
     *
     * @return the instance of JsonAssetLoader
     */
    public static synchronized JsonAssetLoader getInstance() {
        // instance is created if null
        if (instance == null) {
            instance = new JsonAssetLoader();
        }
        return instance;
    }

    /**
     * Retrieves the list of building card dto.
     *
     * @return a list of building card dto
     */
    public List<BuildingCardDTO> getBuildingCards() {
        return extractList("buildingCards", BuildingCardDTO.class);
    }

    /**
     * Retrieves the list of character card dto.
     *
     * @return a list of character card dto
     */
    public List<CharacterCardDTO> getCharacterCards() {
        return extractList("characterCards", CharacterCardDTO.class);
    }

    /**
     * Retrieves the list of event card dto.
     *
     * @return a list of event card dto
     */
    public List<EventCardDTO> getEventCards() {
        return extractList("eventCards", EventCardDTO.class);
    }

    /**
     * Retrieves the list of offer tile dto.
     *
     * @return a list of offer tile dto
     */
    public List<OfferTileDTO> getOfferTiles() {
        return extractList("offerTiles", OfferTileDTO.class);
    }

    /**
     * Retrieves the list of turn order tile dto.
     *
     * @return a list of turn order tile dto
     */
    public List<TurnOrderTileDTO> getTurnOrderTiles() {
        return extractList("turnOrderTiles", TurnOrderTileDTO.class);
    }

    /**
     * Retrieves the map of ui assets.
     *
     * @return a map of ui asset paths
     */
    public Map<String, String> getUiAssets() {
        return extractMap("uiAssets");
    }

    /**
     * Retrieves the map of card backs.
     *
     * @return a map of card back paths
     */
    public Map<String, String> getCardBacks() {
        return extractMap("cardBacks");
    }

    /**
     * Extracts a list of dto from a specific json array node.
     *
     * @param fieldName the name of the JSON array field
     * @param clazz the class of the dto
     * @param <T> the type parameter of the dto
     * @return a list of parsed dto
     */
    private <T> List<T> extractList(String fieldName, Class<T> clazz) {
        // json node corresponding to the field is retrieved
        JsonNode arrayNode = rootNode.get(fieldName);

        // exception is thrown if node is missing or not an array
        if (arrayNode == null || !arrayNode.isArray()) {
            throw new RuntimeException("field " + fieldName + " is missing or not an array");
        }

        // node is converted to a list of dto
        try {
            return objectMapper.readerForListOf(clazz).readValue(arrayNode);
        } catch (Exception e) {
            throw new RuntimeException("error mapping " + fieldName + " to " + clazz.getSimpleName(), e);
        }
    }

    /**
     * Extracts a map of string key-value pairs from a specific json object node.
     *
     * @param fieldName the name of the JSON object field
     * @return a map of parsed string pairs
     */
    private Map<String, String> extractMap(String fieldName) {
        // json node corresponding to the field is retrieved
        JsonNode objectNode = rootNode.get(fieldName);

        // exception is thrown if node is missing or not an object
        if (objectNode == null || !objectNode.isObject()) {
            throw new RuntimeException("field " + fieldName + " is missing or not an object");
        }

        // node is converted to a map of strings
        try {
            return objectMapper.convertValue(objectNode, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("error mapping " + fieldName + " to map", e);
        }
    }
}
