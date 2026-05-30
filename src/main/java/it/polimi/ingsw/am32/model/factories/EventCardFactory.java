package it.polimi.ingsw.am32.model.factories;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.am32.model.cards.events.*;
import it.polimi.ingsw.am32.dto.setup.EventCardDTO;
import it.polimi.ingsw.am32.model.enums.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Factory responsible for creating event cards from dto.
 */
public class EventCardFactory implements CardFactory<EventCardDTO, EventCard> {

    /**
     * Initializes the event card factory.
     */
    public EventCardFactory() {
        // default initialization is performed
    }

    /**
     * Creates a list of cards from a list of dto.
     *
     * @param dtoList the list of dto
     * @return the list of created cards
     */
    @Override
    public List<EventCard> createCards(List<EventCardDTO> dtoList) {
        // dto list is checked for nullability
        Objects.requireNonNull(dtoList, "dtoList cannot be null");

        // list of cards is initialized
        List<EventCard> cards = new ArrayList<>();

        // dto are iterated to create cards
        for (EventCardDTO dto : dtoList) {
            // event type is extracted
            EventType type = EventType.valueOf(dto.getEventType());

            // JSON node is extracted directly from dto
            JsonNode params = dto.getEventParams();

            EventCard newCard;

            // card is instantiated based on the event type
            switch (type) {
                case CAVE_PAINTINGS:
                    newCard = new CavePaintingsEvent(
                            dto.getId(),
                            dto.getEra(),
                            getOptionalBoolean(params, JSONConsts.EVENT_IS_FINAL),
                            getRequiredInt(params, JSONConsts.EVENT_CAVE_PRESTIGE_BONUS, dto.getId()),
                            getRequiredInt(params, JSONConsts.EVENT_CAVE_BONUS_THRESH, dto.getId()),
                            getRequiredInt(params, JSONConsts.EVENT_CAVE_PRESTIGE_PENALTY, dto.getId())
                    );
                    break;

                case HUNT:
                    newCard = new HuntEvent(
                            dto.getId(),
                            dto.getEra(),
                            getOptionalBoolean(params, JSONConsts.EVENT_IS_FINAL),
                            getRequiredInt(params, JSONConsts.EVENT_HUNT_FOOD_BONUS, dto.getId()),
                            getRequiredInt(params, JSONConsts.EVENT_HUNT_PRESTIGE_BONUS, dto.getId())
                    );
                    break;

                case SHAMANIC_RITUAL:
                    newCard = new ShamanicRitualEvent(
                            dto.getId(),
                            dto.getEra(),
                            getOptionalBoolean(params, JSONConsts.EVENT_IS_FINAL),
                            getRequiredInt(params, JSONConsts.EVENT_SHAM_PRESTIGE_BONUS, dto.getId()),
                            getRequiredInt(params, JSONConsts.EVENT_SHAM_PRESTIGE_PENALTY, dto.getId())
                    );
                    break;

                case SUSTENANCE:
                    newCard = new SustenanceEvent(
                            dto.getId(),
                            dto.getEra(),
                            getOptionalBoolean(params, JSONConsts.EVENT_IS_FINAL),
                            getRequiredInt(params, JSONConsts.EVENT_SUST_FOOD_COST, dto.getId()),
                            getRequiredInt(params, JSONConsts.EVENT_SUST_PRESTIGE_PENALTY, dto.getId())
                    );
                    break;

                default:
                    // exception is thrown if type is not supported
                    throw new IllegalStateException("unexpected value: " + type + " for event card " + dto.getId());
            }

            // card is added to the list
            cards.add(newCard);
        }

        // list is returned
        return cards;
    }

    /**
     * Extracts a required integer from a JSON node.
     *
     * @param node the JSON node
     * @param fieldName the field name
     * @param cardId the card id
     * @return the extracted integer
     */
    private int getRequiredInt(JsonNode node, String fieldName, String cardId) {
        // node is checked for missing parameter
        if (!node.hasNonNull(fieldName)) {
            // exception is thrown
            throw new IllegalStateException("error: missing parameter '" + fieldName + "' in json for card " + cardId);
        }
        // integer value is returned
        return node.get(fieldName).asInt();
    }

    /**
     * Extracts an optional boolean from a JSON node.
     *
     * @param node the JSON node
     * @param fieldName the field name
     * @return the extracted boolean, or false if missing
     */
    private boolean getOptionalBoolean(JsonNode node, String fieldName) {
        // node is checked for the parameter
        if (node.hasNonNull(fieldName)) {
            // boolean value is returned
            return node.get(fieldName).asBoolean();
        }
        // default value is returned
        return false;
    }
}
