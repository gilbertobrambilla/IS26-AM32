package it.polimi.ingsw.am32.factories;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am32.cards.events.*;
import it.polimi.ingsw.am32.dto.setup.EventCardDTO;
import it.polimi.ingsw.am32.enums.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventCardFactory implements CardFactory<EventCardDTO, EventCard> {
    private final ObjectMapper objectMapper;

    public EventCardFactory() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<EventCard> createCards(List<EventCardDTO> dtoList) {
        Objects.requireNonNull(dtoList, "dtoList cannot be null");

        List<EventCard> cards = new ArrayList<>();

        for (EventCardDTO dto : dtoList) {
            EventType type = EventType.valueOf(dto.getEventType());

            JsonNode params;
            try {
                params = objectMapper.readTree(dto.getEventParams());
            } catch (Exception e) {
                throw new RuntimeException("error parsing json for event card " + dto.getId(), e);
            }

            EventCard newCard;

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
                    throw new IllegalStateException("unexpected value: " + type + " for event card " + dto.getId());
            }

            cards.add(newCard);
        }

        return cards;
    }

    private int getRequiredInt(JsonNode node, String fieldName, String cardId) {
        if (!node.hasNonNull(fieldName)) {
            throw new IllegalStateException("error: missing parameter '" + fieldName + "' in json for card " + cardId);
        }
        return node.get(fieldName).asInt();
    }

    private boolean getOptionalBoolean(JsonNode node, String fieldName) {
        if (node.hasNonNull(fieldName)) {
            return node.get(fieldName).asBoolean();
        }
        return false;
    }
}
