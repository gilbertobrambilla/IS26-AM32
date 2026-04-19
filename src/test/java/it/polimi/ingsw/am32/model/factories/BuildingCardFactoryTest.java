package it.polimi.ingsw.am32.model.factories;

import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.NoEffect;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.endgame.PrestigePerCharacterEffect;
import it.polimi.ingsw.am32.dto.setup.BuildingCardDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildingCardFactoryTest {

    private BuildingCardFactory factory;

    @BeforeEach
    void setUp() {
        factory = new BuildingCardFactory();
    }

    @Test
    void createCardsShouldReturnListOfValidBuildings() {
        List<BuildingCardDTO> dtoList = new ArrayList<>();

        dtoList.add(new BuildingCardDTO("b01", 1, 0, 1, "NO_EFFECT", "{}"));
        dtoList.add(new BuildingCardDTO("b02", 3, 0, 5, "ENDGAME_BUILDER_PRESTIGE_MULTIPLIER", "{\"multiplier\": 2}"));
        dtoList.add(new BuildingCardDTO("b03", 3, 0, 5, "ENDGAME_PRESTIGE_PER_CHARACTER", "{\"targetCharacter\": \"SHAMAN\", \"pointsPerCharacter\": 5}"));
        dtoList.add(new BuildingCardDTO("b04", 3, 0, 5, "ENDGAME_PRESTIGE_PER_SET", "{\"pointsPerSet\": 10}"));
        dtoList.add(new BuildingCardDTO("b05", 2, 0, 3, "SUSTENANCE_DISCOUNT_PER_CHARACTER", "{\"targetCharacter\": \"BUILDER\", \"discountPerCharacter\": 1}"));
        dtoList.add(new BuildingCardDTO("b06", 2, 0, 4, "EXTRA_RESOURCES_ON_EVENT", "{\"targetEvent\": \"HUNT\", \"targetCharacter\": \"HUNTER\", \"extraFood\": 1, \"extraPrestige\": 1}"));
        dtoList.add(new BuildingCardDTO("b07", 2, 0, 3, "EXTRA_CARD_ON_ROUND_END", "{}"));
        dtoList.add(new BuildingCardDTO("b08", 1, 0, 2, "EXTRA_FOOD_ON_TOTEM", "{\"extraFood\": 2}"));
        dtoList.add(new BuildingCardDTO("b09", 2, 0, 3, "FOOD_PER_INVENTOR_PAIR", "{\"extraFood\": 1}"));
        dtoList.add(new BuildingCardDTO("b10", 2, 0, 3, "FOOD_PER_SET", "{\"extraFood\": 1}"));
        dtoList.add(new BuildingCardDTO("b11", 2, 0, 3, "EXTRA_SHAMAN_STARS", "{\"extraStars\": 2}"));
        dtoList.add(new BuildingCardDTO("b12", 2, 0, 4, "SHAMANIC_DOUBLE_REWARD", "{}"));
        dtoList.add(new BuildingCardDTO("b13", 2, 0, 4, "SHAMANIC_PENALTY_IMMUNITY", "{}"));

        List<BuildingCard> cards = factory.createCards(dtoList);

        assertEquals(13, cards.size());
        assertEquals("b01", cards.get(0).getId());
        assertInstanceOf(NoEffect.class, cards.get(0).getEffect());
        assertInstanceOf(PrestigePerCharacterEffect.class, cards.get(2).getEffect());
    }

    @Test
    void createCardsShouldReturnCardsWithParameterizedEffects() {
        String jsonParams = "{\"targetCharacter\":\"BUILDER\", \"pointsPerCharacter\":2}";
        BuildingCardDTO dto = new BuildingCardDTO("BuildingID", 2, 5, 4, "ENDGAME_PRESTIGE_PER_CHARACTER", jsonParams);

        List<BuildingCard> cards = factory.createCards(List.of(dto));

        assertEquals(1, cards.size());
        assertInstanceOf(PrestigePerCharacterEffect.class, cards.get(0).getEffect());
    }

    @Test
    void createCardsShouldThrowExceptionIfJsonIsMalformed() {
        BuildingCardDTO dto = new BuildingCardDTO("err_json", 1, 0, 1, "NO_EFFECT", "invalid json{");
        assertThrows(RuntimeException.class, () -> factory.createCards(List.of(dto)));
    }

    @Test
    void createCardsShouldThrowExceptionIfRequiredIntParameterIsMissing() {
        BuildingCardDTO dto = new BuildingCardDTO("err_int", 1, 0, 1, "EXTRA_FOOD_ON_TOTEM", "{}");
        assertThrows(IllegalStateException.class, () -> factory.createCards(List.of(dto)));
    }

    @Test
    void createCardsShouldThrowExceptionIfRequiredStringParameterIsMissing() {
        BuildingCardDTO dto = new BuildingCardDTO("err_str", 1, 0, 1, "ENDGAME_PRESTIGE_PER_CHARACTER", "{\"pointsPerCharacter\": 5}");
        assertThrows(IllegalStateException.class, () -> factory.createCards(List.of(dto)));
    }

    @Test
    void createCardsShouldThrowExceptionIfEffectCodeIsInvalid() {
        BuildingCardDTO dto = new BuildingCardDTO("err_enum", 1, 0, 1, "NON_EXISTENT_EFFECT", "{}");
        assertThrows(IllegalArgumentException.class, () -> factory.createCards(List.of(dto)));
    }

}
