package it.polimi.ingsw.am32.model.dto.setup;

public class EventCardDTO {
    private final String id;
    private final int era;
    private final String eventType;
    private final String eventParams;

    public EventCardDTO(String id, int era, String eventType, String eventParams) {
        this.id = id;
        this.era = era;
        this.eventType = eventType;
        this.eventParams = eventParams;
    }

    public String getId() {
        return this.id;
    }

    public int getEra() {
        return this.era;
    }

    public String getEventType() {
        return this.eventType;
    }

    public String getEventParams() {
        return this.eventParams;
    }
}
