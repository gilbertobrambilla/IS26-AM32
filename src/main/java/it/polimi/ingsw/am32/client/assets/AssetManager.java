package it.polimi.ingsw.am32.client.assets;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am32.dto.setup.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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

    private AssetManager() {
        this.buildingCards = new HashMap<>();
        this.characterCards = new HashMap<>();
        this.eventCards = new HashMap<>();
        this.offerTiles = new HashMap<>();
        this.turnOrderTiles = new HashMap<>();
        this.uiAssets = new HashMap<>();
        this.cardBacks = new HashMap<>();
    }

    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    public void loadAssets(String resourcePath) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try (InputStream is = AssetManager.class.getResourceAsStream("/" + resourcePath)) {
            if (is == null) throw new RuntimeException("Asset file not found: " + resourcePath);

            JsonNode rootNode = mapper.readTree(is);

            buildAssetPathIndex(rootNode);

            parseBuildingCards(rootNode.get("building_cards"));
            parseCharacterCards(rootNode.get("character_cards"));
            parseEventCards(rootNode.get("event_cards"));
            parseOfferTiles(rootNode.get("offer_tiles"));
            parseTurnOrderTiles(rootNode.get("turn_order_tiles"));
            parseStringMap(rootNode.get("ui_assets"), uiAssets);
            parseStringMap(rootNode.get("card_backs"), cardBacks);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load assets", e);
        }
    }

    private void parseBuildingCards(JsonNode array) {
        if (array == null || !array.isArray()) return;
        for (JsonNode node : array) {
            BuildingCardDTO card = new BuildingCardDTO(
                    node.get("id").asText(),
                    node.get("era").asInt(),
                    node.get("base_prestige").asInt(),
                    node.get("cost").asInt(),
                    node.get("effect_code").asText(),
                    node.get("effect_params").toString()
            );
            buildingCards.put(card.getId(), card);
        }
    }

    private void parseCharacterCards(JsonNode array) {
        if (array == null || !array.isArray()) return;
        for (JsonNode node : array) {
            CharacterCardDTO card = new CharacterCardDTO(
                    node.get("id").asText(),
                    node.get("era").asInt(),
                    node.get("base_prestige").asInt(),
                    node.get("min_players").asInt(),
                    node.get("character_type").asText(),
                    node.get("character_params").toString()
            );
            characterCards.put(card.getId(), card);
        }
    }

    private void parseEventCards(JsonNode array) {
        if (array == null || !array.isArray()) return;
        for (JsonNode node : array) {
            EventCardDTO card = new EventCardDTO(
                    node.get("id").asText(),
                    node.get("era").asInt(),
                    node.get("event_type").asText(),
                    node.get("event_params").toString()
            );
            eventCards.put(card.getId(), card);
        }
    }

    private void parseOfferTiles(JsonNode array) {
        if (array == null || !array.isArray()) return;
        for (JsonNode node : array) {
            OfferTileDTO tile = new OfferTileDTO(
                    node.get("id").asText(),
                    node.get("min_players").asInt(),
                    node.get("act_draws_upper").asInt(),
                    node.get("act_draws_lower").asInt(),
                    node.get("act_food_bonus").asInt()
            );
            offerTiles.put(tile.getId(), tile);
        }
    }

    private void parseTurnOrderTiles(JsonNode array) {
        if (array == null || !array.isArray()) return;
        for (JsonNode node : array) {
            TurnOrderTileDTO tile = new TurnOrderTileDTO(
                    node.get("id").asText(),
                    node.get("required_players").asInt(),
                    node.get("food_modifiers").toString(),
                    node.get("prestige_modifiers").toString()
            );
            turnOrderTiles.put(tile.getId(), tile);
        }
    }

    private void parseStringMap(JsonNode node, Map<String, String> targetMap) {
        if (node == null || !node.isObject()) return;
        node.fields().forEachRemaining(entry -> {
            targetMap.put(entry.getKey(), entry.getValue().asText());
        });
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

    public BuildingCardDTO getBuildingCard(String id) {
        return buildingCards.get(id);
    }

    public CharacterCardDTO getCharacterCard(String id) {
        return characterCards.get(id);
    }

    public EventCardDTO getEventCard(String id) {
        return eventCards.get(id);
    }

    public OfferTileDTO getOfferTile(String id) {
        return offerTiles.get(id);
    }

    public TurnOrderTileDTO getTurnOrderTile(String id) {
        return turnOrderTiles.get(id);
    }

    public String getUiAsset(String key) {
        return uiAssets.get(key);
    }

    public String getCardBack(String key) {
        return cardBacks.get(key);
    }

    public String getAssetPath(String id) {
        return assetPathIndex.get(id);
    }
}
