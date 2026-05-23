package it.polimi.ingsw.am32.model.factories;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.*;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.NoEffect;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.endgame.*;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.events.*;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.phases.*;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.resources.*;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.status.*;
import it.polimi.ingsw.am32.dto.setup.BuildingCardDTO;
import it.polimi.ingsw.am32.model.enums.BuildingEffectCode;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import it.polimi.ingsw.am32.model.enums.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Factory responsible for creating building cards from dto.
 */
public class BuildingCardFactory implements CardFactory<BuildingCardDTO, BuildingCard> {

    /**
     * Initializes the building card factory.
     */
    public BuildingCardFactory() {
        // default initialization is performed
    }

    /**
     * Creates a list of cards from a list of dto.
     *
     * @param dtoList the list of dto
     * @return the list of created cards
     */
    @Override
    public List<BuildingCard> createCards(List<BuildingCardDTO> dtoList) {
        // dto list is checked for nullability
        Objects.requireNonNull(dtoList, "dtoList cannot be null");

        // list of cards is initialized
        List<BuildingCard> cards = new ArrayList<>();

        // dto are iterated to create cards
        for (BuildingCardDTO dto : dtoList) {
            // building effect code is extracted
            BuildingEffectCode type = BuildingEffectCode.valueOf(dto.getEffectCode());

            // JSON node is extracted directly from dto
            JsonNode params = dto.getEffectParams();

            BuildingCardEffect effect;

            // effect is instantiated based on the type
            switch (type) {
                // endgame effects
                case ENDGAME_BUILDER_PRESTIGE_MULTIPLIER:
                    effect = new BuilderPrestigeMultiplierEffect(
                            getRequiredInt(params, JSONConsts.BUILD_MULTIPLIER, dto.getId())
                    );
                    break;
                case ENDGAME_PRESTIGE_PER_CHARACTER:
                    effect = new PrestigePerCharacterEffect(
                            CharacterType.valueOf(getRequiredString(params, JSONConsts.BUILD_TARGET_CHAR, dto.getId())),
                            getRequiredInt(params, JSONConsts.BUILD_PTS_PER_CHAR, dto.getId())
                    );
                    break;
                case ENDGAME_PRESTIGE_PER_SET:
                    effect = new PrestigePerSetEffect(
                            getRequiredInt(params, JSONConsts.BUILD_PTS_PER_SET, dto.getId())
                    );
                    break;

                // event effects
                case SUSTENANCE_DISCOUNT_PER_CHARACTER:
                    CharacterType targetChar = CharacterType.valueOf(getRequiredString(params, JSONConsts.BUILD_TARGET_CHAR, dto.getId()));
                    int discount = getRequiredInt(params, JSONConsts.BUILD_DISC_PER_CHAR, dto.getId());

                    if (targetChar == CharacterType.ARTIST) {
                        effect = new ArtistSustenanceDiscountEffect(discount);
                    } else if (targetChar == CharacterType.INVENTOR) {
                        effect = new InventorSustenanceDiscountEffect(discount);
                    } else if (targetChar == CharacterType.GATHERER) {
                        effect = new GathererSustenanceDiscountEffect(discount);
                    } else {
                        // exception is thrown if type is not supported
                        throw new IllegalStateException("unsupported character type for sustenance discount building " + dto.getId());
                    }
                    break;
                case EXTRA_RESOURCES_ON_EVENT:
                    EventType eventType = EventType.valueOf(getRequiredString(params, JSONConsts.BUILD_TARGET_EVENT, dto.getId()));
                    CharacterType charType = CharacterType.valueOf(getRequiredString(params, JSONConsts.BUILD_TARGET_CHAR, dto.getId()));
                    int exFood = getRequiredInt(params, JSONConsts.BUILD_EXTRA_FOOD, dto.getId());
                    int exPrestige = getRequiredInt(params, JSONConsts.BUILD_EXTRA_PRESTIGE, dto.getId());

                    // effect class is instantiated based on the target event
                    if (eventType == EventType.HUNT) {
                        effect = new ExtraResourcesOnHuntEffect(charType, exFood, exPrestige);
                    } else if (eventType == EventType.CAVE_PAINTINGS) {
                        effect = new ExtraResourcesOnCavePaintingsEffect(charType, exFood, exPrestige);
                    } else {
                        // exception is thrown if event type is not supported
                        throw new IllegalStateException("unsupported event type for building");
                    }
                    break;

                // phase effects
                case EXTRA_CARD_ON_ROUND_END:
                    effect = new ExtraCardOnRoundEndEffect();
                    break;
                case EXTRA_FOOD_ON_TOTEM:
                    effect = new ExtraFoodOnTotemEffect(
                            getRequiredInt(params, JSONConsts.BUILD_EXTRA_FOOD, dto.getId())
                    );
                    break;

                // resource effects
                case FOOD_PER_INVENTOR_PAIR:
                    effect = new FoodPerInventorPairEffect(
                            getRequiredInt(params, JSONConsts.BUILD_EXTRA_FOOD, dto.getId())
                    );
                    break;
                case FOOD_PER_SET:
                    effect = new FoodPerSetEffect(
                            getRequiredInt(params, JSONConsts.BUILD_EXTRA_FOOD, dto.getId())
                    );
                    break;

                // status effects
                case EXTRA_SHAMAN_STARS:
                    effect = new ExtraShamanStarsEffect(
                            getRequiredInt(params, JSONConsts.BUILD_EXTRA_STARS, dto.getId())
                    );
                    break;
                case SHAMANIC_DOUBLE_REWARD:
                    effect = new ShamanicDoubleRewardEffect();
                    break;
                case SHAMANIC_PENALTY_IMMUNITY:
                    effect = new ShamanicPenaltyImmunityEffect();
                    break;

                // no effect
                case NO_EFFECT:
                    effect = new NoEffect();
                    break;

                default:
                    // exception is thrown if type is not supported
                    throw new IllegalStateException("unexpected value: " + type + " for building card " + dto.getId());
            }

            // card is created
            BuildingCard newCard = new BuildingCard(
                    dto.getId(),
                    dto.getEra(),
                    dto.getBasePrestige(),
                    dto.getCost(),
                    effect
            );

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
}
