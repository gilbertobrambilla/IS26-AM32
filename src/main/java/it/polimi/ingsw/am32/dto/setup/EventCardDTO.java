package it.polimi.ingsw.am32.dto.setup;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Data transfer object representing an event card.
 */
public class EventCardDTO {
    private String id;
    private int era;
    private String eventType;
    private JsonNode eventParams;
    private String assetPath;

    /**
     * Default constructor.
     */
    public EventCardDTO() {
        // default initialization is performed by jackson
    }

    /**
     * Constructor with all parameters.
     *
     * @param id The id of the event card.
     * @param era The era of the event card.
     * @param eventType The type of the event.
     * @param eventParams The parameters of the event as a JSON node.
     * @param assetPath The path to the card asset.
     */
    public EventCardDTO(String id, int era, String eventType, JsonNode eventParams, String assetPath) {
        this.id = id;
        this.era = era;
        this.eventType = eventType;
        this.eventParams = eventParams;
        this.assetPath = assetPath;
    }

    /**
     * Gets the id.
     *
     * @return The id of the event card.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the id.
     *
     * @param id The new id to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the era.
     *
     * @return The era of the event card.
     */
    public int getEra() {
        return this.era;
    }

    /**
     * Sets the era.
     *
     * @param era The new era to set.
     */
    public void setEra(int era) {
        this.era = era;
    }

    /**
     * Gets the event type.
     *
     * @return The event type.
     */
    public String getEventType() {
        return this.eventType;
    }

    /**
     * Sets the event type.
     *
     * @param eventType The new event type to set.
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Gets the event parameters.
     *
     * @return The event parameters as a JSON node.
     */
    public JsonNode getEventParams() {
        return this.eventParams;
    }

    /**
     * Sets the event parameters.
     *
     * @param eventParams The new event parameters to set.
     */
    public void setEventParams(JsonNode eventParams) {
        this.eventParams = eventParams;
    }

    /**
     * Gets the asset path.
     *
     * @return The asset path.
     */
    public String getAssetPath() {
        return this.assetPath;
    }

    /**
     * Sets the asset path.
     *
     * @param assetPath The new asset path to set.
     */
    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }
}
