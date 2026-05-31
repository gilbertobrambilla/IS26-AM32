package it.polimi.ingsw.am32.dto.setup;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * An event card setup data transfer object is represented.
 */
public class EventCardDTO {
    private String id;
    private int era;
    private String eventType;
    private JsonNode eventParams;
    private String assetPath;

    /**
     * An event card setup data transfer object is initialized by Jackson.
     */
    public EventCardDTO() {
        // default initialization is performed by jackson
    }

    /**
     * An event card setup data transfer object is initialized with full card data.
     *
     * @param id the event card id
     * @param era the event card era
     * @param eventType the event type
     * @param eventParams the event parameters as a json node
     * @param assetPath the card asset path
     */
    public EventCardDTO(String id, int era, String eventType, JsonNode eventParams, String assetPath) {
        this.id = id;
        this.era = era;
        this.eventType = eventType;
        this.eventParams = eventParams;
        this.assetPath = assetPath;
    }

    /**
     * The event card id is returned.
     *
     * @return the event card id
     */
    public String getId() {
        return this.id;
    }

    /**
     * The event card id is set.
     *
     * @param id the event card id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * The event card era is returned.
     *
     * @return the event card era
     */
    public int getEra() {
        return this.era;
    }

    /**
     * The event card era is set.
     *
     * @param era the event card era
     */
    public void setEra(int era) {
        this.era = era;
    }

    /**
     * The event type is returned.
     *
     * @return the event type
     */
    public String getEventType() {
        return this.eventType;
    }

    /**
     * The event type is set.
     *
     * @param eventType the event type
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * The event parameters are returned.
     *
     * @return the event parameters as a json node
     */
    public JsonNode getEventParams() {
        return this.eventParams;
    }

    /**
     * The event parameters are set.
     *
     * @param eventParams the event parameters as a json node
     */
    public void setEventParams(JsonNode eventParams) {
        this.eventParams = eventParams;
    }

    /**
     * The card asset path is returned.
     *
     * @return the card asset path
     */
    public String getAssetPath() {
        return this.assetPath;
    }

    /**
     * The card asset path is set.
     *
     * @param assetPath the card asset path
     */
    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }
}
