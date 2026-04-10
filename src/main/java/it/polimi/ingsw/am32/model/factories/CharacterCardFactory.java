package it.polimi.ingsw.am32.model.factories;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.*;
import it.polimi.ingsw.am32.model.dto.setup.CharacterCardDTO;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import it.polimi.ingsw.am32.model.enums.InventionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CharacterCardFactory implements CardFactory<CharacterCardDTO, CharacterCard> {
    private final ObjectMapper objectMapper;

    public CharacterCardFactory() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<CharacterCard> createCards(List<CharacterCardDTO> dtoList) {
        Objects.requireNonNull(dtoList, "dtoList cannot be null");

        List<CharacterCard> cards = new ArrayList<>();

        for (CharacterCardDTO dto : dtoList) {
            CharacterType type = CharacterType.valueOf(dto.getCharacterType());

            JsonNode params;
            try {
                params = objectMapper.readTree(dto.getCharacterParams());
            } catch (Exception e) {
                throw new RuntimeException("error parsing json for character card " + dto.getId(), e);
            }

            CharacterCard newCard;

            switch (type) {
                case ARTIST:
                    newCard = new ArtistCard(
                            dto.getId(),
                            dto.getEra(),
                            dto.getBasePrestige(),
                            dto.getMinPlayers(),
                            getRequiredInt(params, JSONConsts.CHAR_ART_PRESTIGE, dto.getId())
                    );
                    break;

                case BUILDER:
                    newCard = new BuilderCard(
                            dto.getId(),
                            dto.getEra(),
                            dto.getBasePrestige(),
                            dto.getMinPlayers(),
                            getRequiredInt(params, JSONConsts.CHAR_BLD_DISCOUNT, dto.getId())
                    );
                    break;

                case GATHERER:
                    newCard = new GathererCard(
                            dto.getId(),
                            dto.getEra(),
                            dto.getBasePrestige(),
                            dto.getMinPlayers(),
                            getRequiredInt(params, JSONConsts.CHAR_GAT_DISCOUNT, dto.getId())
                    );
                    break;

                case HUNTER:
                    newCard = new HunterCard(
                            dto.getId(),
                            dto.getEra(),
                            dto.getBasePrestige(),
                            dto.getMinPlayers(),
                            getRequiredBoolean(params, JSONConsts.CHAR_HNT_HAS_MEAT, dto.getId())
                    );
                    break;

                case INVENTOR:
                    newCard = new InventorCard(
                            dto.getId(),
                            dto.getEra(),
                            dto.getBasePrestige(),
                            dto.getMinPlayers(),
                            InventionType.valueOf(getRequiredString(params, JSONConsts.CHAR_INV_TYPE, dto.getId()))
                    );
                    break;

                case SHAMAN:
                    newCard = new ShamanCard(
                            dto.getId(),
                            dto.getEra(),
                            dto.getBasePrestige(),
                            dto.getMinPlayers(),
                            getRequiredInt(params, JSONConsts.CHAR_SHA_STARS, dto.getId())
                    );
                    break;

                default:
                    throw new IllegalStateException("unexpected value: " + type + " for character card " + dto.getId());
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

    private String getRequiredString(JsonNode node, String fieldName, String cardId) {
        if (!node.hasNonNull(fieldName)) {
            throw new IllegalStateException("error: missing parameter '" + fieldName + "' in json for card " + cardId);
        }
        return node.get(fieldName).asText();
    }

    private boolean getRequiredBoolean(JsonNode node, String fieldName, String cardId) {
        if (!node.hasNonNull(fieldName)) {
            throw new IllegalStateException("error: missing parameter '" + fieldName + "' in json for card " + cardId);
        }
        return node.get(fieldName).asBoolean();
    }
}
