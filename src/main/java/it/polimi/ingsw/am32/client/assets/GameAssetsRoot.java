package it.polimi.ingsw.am32.client.assets;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.am32.dto.setup.*;

import java.util.List;
import java.util.Map;

/**
 * Game assets root is defined to map the main json file.
 */
public class GameAssetsRoot {

    @JsonProperty("building_cards")
    private List<BuildingCardDTO> buildingCards;

    @JsonProperty("character_cards")
    private List<CharacterCardDTO> characterCards;

    @JsonProperty("event_cards")
    private List<EventCardDTO> eventCards;

    @JsonProperty("offer_tiles")
    private List<OfferTileDTO> offerTiles;

    @JsonProperty("turn_order_tiles")
    private List<TurnOrderTileDTO> turnOrderTiles;

    @JsonProperty("ui_assets")
    private Map<String, String> uiAssets;

    @JsonProperty("card_backs")
    private Map<String, String> cardBacks;

    /**
     * Game assets root is instantiated.
     */
    public GameAssetsRoot() {
        // default constructor is required by jackson
    }

    /**
     * Building cards are retrieved.
     *
     * @return the list of building cards.
     */
    public List<BuildingCardDTO> getBuildingCards() {
        // list is returned
        return buildingCards;
    }

    /**
     * Character cards are retrieved.
     *
     * @return the list of character cards.
     */
    public List<CharacterCardDTO> getCharacterCards() {
        // list is returned
        return characterCards;
    }

    /**
     * Event cards are retrieved.
     *
     * @return the list of event cards.
     */
    public List<EventCardDTO> getEventCards() {
        // list is returned
        return eventCards;
    }

    /**
     * Offer tiles are retrieved.
     *
     * @return the list of offer tiles.
     */
    public List<OfferTileDTO> getOfferTiles() {
        // list is returned
        return offerTiles;
    }

    /**
     * Turn order tiles are retrieved.
     *
     * @return the list of turn order tiles.
     */
    public List<TurnOrderTileDTO> getTurnOrderTiles() {
        // list is returned
        return turnOrderTiles;
    }

    /**
     * Ui assets are retrieved.
     *
     * @return the map of ui assets.
     */
    public Map<String, String> getUiAssets() {
        // map is returned
        return uiAssets;
    }

    /**
     * Card backs are retrieved.
     *
     * @return the map of card backs.
     */
    public Map<String, String> getCardBacks() {
        // map is returned
        return cardBacks;
    }
}
