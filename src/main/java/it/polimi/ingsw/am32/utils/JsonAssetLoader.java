package it.polimi.ingsw.am32.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am32.dto.setup.*;
import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * A utility for loading and parsing the assets json file is represented.
 */
public class JsonAssetLoader {
    private static JsonAssetLoader instance;
    private final ObjectMapper objectMapper;
    private final JsonNode rootNode;

    /**
     * The asset loader is initialized from the bundled assets file.
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
                    LogManager.getInstance().log(LogLevel.WARNING, "[ASSETS] Asset stream could not be closed.", e);
                }
            }
        }
    }

    /**
     * The singleton asset loader is returned.
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
     * The list of building card dto is returned.
     *
     * @return a list of building card dto
     */
    public List<BuildingCardDTO> getBuildingCards() {
        return extractList("buildingCards", BuildingCardDTO.class);
    }

    /**
     * The list of character card dto is returned.
     *
     * @return a list of character card dto
     */
    public List<CharacterCardDTO> getCharacterCards() {
        return extractList("characterCards", CharacterCardDTO.class);
    }

    /**
     * The list of event card dto is returned.
     *
     * @return a list of event card dto
     */
    public List<EventCardDTO> getEventCards() {
        return extractList("eventCards", EventCardDTO.class);
    }

    /**
     * The list of offer tile dto is returned.
     *
     * @return a list of offer tile dto
     */
    public List<OfferTileDTO> getOfferTiles() {
        return extractList("offerTiles", OfferTileDTO.class);
    }

    /**
     * The list of turn order tile dto is returned.
     *
     * @return a list of turn order tile dto
     */
    public List<TurnOrderTileDTO> getTurnOrderTiles() {
        return extractList("turnOrderTiles", TurnOrderTileDTO.class);
    }

    /**
     * The map of ui assets is returned.
     *
     * @return a map of ui asset paths
     */
    public Map<String, String> getUiAssets() {
        return extractMap("uiAssets");
    }

    /**
     * The map of card backs is returned.
     *
     * @return a map of card back paths
     */
    public Map<String, String> getCardBacks() {
        return extractMap("cardBacks");
    }

    /**
     * A list of dto is extracted from a specific json array node.
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
     * A map of string key-value pairs is extracted from a specific json object node.
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
