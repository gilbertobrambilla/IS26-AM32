package it.polimi.ingsw.am32.model.factories;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.*;
import it.polimi.ingsw.am32.dto.setup.CharacterCardDTO;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import it.polimi.ingsw.am32.model.enums.InventionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Factory responsible for creating character cards from dto.
 */
public class CharacterCardFactory implements CardFactory<CharacterCardDTO, CharacterCard> {

    /**
     * Default constructor.
     */
    public CharacterCardFactory() {
        // default initialization is performed
    }

    @Override
    public List<CharacterCard> createCards(List<CharacterCardDTO> dtoList) {
        // dto list is checked for nullability
        Objects.requireNonNull(dtoList, "dtoList cannot be null");

        // list of cards is initialized
        List<CharacterCard> cards = new ArrayList<>();

        // dto are iterated to create cards
        for (CharacterCardDTO dto : dtoList) {
            // character type is extracted
            CharacterType type = CharacterType.valueOf(dto.getCharacterType());

            // JSON node is extracted directly from dto
            JsonNode params = dto.getCharacterParams();

            CharacterCard newCard;

            // card is instantiated based on the character type
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
                    // exception is thrown if type is not supported
                    throw new IllegalStateException("unexpected value: " + type + " for character card " + dto.getId());
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
     * Extracts a required string from a JSON node.
     *
     * @param node the JSON node
     * @param fieldName the field name
     * @param cardId the card id
     * @return the extracted string
     */
    private String getRequiredString(JsonNode node, String fieldName, String cardId) {
        // node is checked for missing parameter
        if (!node.hasNonNull(fieldName)) {
            // exception is thrown
            throw new IllegalStateException("error: missing parameter '" + fieldName + "' in json for card " + cardId);
        }
        // string value is returned
        return node.get(fieldName).asText();
    }

    /**
     * Extracts a required boolean from a JSON node.
     *
     * @param node the JSON node
     * @param fieldName the field name
     * @param cardId the card id
     * @return the extracted boolean
     */
    private boolean getRequiredBoolean(JsonNode node, String fieldName, String cardId) {
        // node is checked for missing parameter
        if (!node.hasNonNull(fieldName)) {
            // exception is thrown
            throw new IllegalStateException("error: missing parameter '" + fieldName + "' in json for card " + cardId);
        }
        // boolean value is returned
        return node.get(fieldName).asBoolean();
    }
}
