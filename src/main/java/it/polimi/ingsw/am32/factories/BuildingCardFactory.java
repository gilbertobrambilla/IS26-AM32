package it.polimi.ingsw.am32.factories;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am32.cards.acquirable.buildings.*;
import it.polimi.ingsw.am32.cards.acquirable.buildings.effects.NoEffect;
import it.polimi.ingsw.am32.cards.acquirable.buildings.effects.endgame.*;
import it.polimi.ingsw.am32.cards.acquirable.buildings.effects.events.*;
import it.polimi.ingsw.am32.cards.acquirable.buildings.effects.phases.*;
import it.polimi.ingsw.am32.cards.acquirable.buildings.effects.resources.*;
import it.polimi.ingsw.am32.cards.acquirable.buildings.effects.status.*;
import it.polimi.ingsw.am32.dto.setup.BuildingCardDTO;
import it.polimi.ingsw.am32.enums.BuildingEffectCode;
import it.polimi.ingsw.am32.enums.CharacterType;
import it.polimi.ingsw.am32.enums.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BuildingCardFactory implements CardFactory<BuildingCardDTO, BuildingCard> {
    private final ObjectMapper objectMapper;

    public BuildingCardFactory() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<BuildingCard> createCards(List<BuildingCardDTO> dtoList) {
        Objects.requireNonNull(dtoList, "dtoList cannot be null");

        List<BuildingCard> cards = new ArrayList<>();

        for (BuildingCardDTO dto : dtoList) {
            BuildingEffectCode type = BuildingEffectCode.valueOf(dto.getEffectCode());

            JsonNode params;
            try {
                params = objectMapper.readTree(dto.getEffectParams());
            } catch (Exception e) {
                throw new RuntimeException("error parsing json for building card " + dto.getId(), e);
            }

            BuildingCardEffect effect;

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
                    effect = new SustenanceDiscountPerCharacterEffect(
                            CharacterType.valueOf(getRequiredString(params, JSONConsts.BUILD_TARGET_CHAR, dto.getId())),
                            getRequiredInt(params, JSONConsts.BUILD_DISC_PER_CHAR, dto.getId())
                    );
                    break;
                case EXTRA_RESOURCES_ON_EVENT:
                    effect = new ExtraResourcesOnEventEffect(
                            EventType.valueOf(getRequiredString(params, JSONConsts.BUILD_TARGET_EVENT, dto.getId())),
                            CharacterType.valueOf(getRequiredString(params, JSONConsts.BUILD_TARGET_CHAR, dto.getId())),
                            getRequiredInt(params, JSONConsts.BUILD_EXTRA_FOOD, dto.getId()),
                            getRequiredInt(params, JSONConsts.BUILD_EXTRA_PRESTIGE, dto.getId())
                    );
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
                    throw new IllegalStateException("unexpected value: " + type + " for building card " + dto.getId());
            }

            BuildingCard newCard = new BuildingCard(
                    dto.getId(),
                    dto.getEra(),
                    dto.getBasePrestige(),
                    dto.getCost(),
                    effect
            );

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
}
