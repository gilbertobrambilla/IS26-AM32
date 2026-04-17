package it.polimi.ingsw.am32.model.factories;

import it.polimi.ingsw.am32.model.cards.acquirable.characters.*;
import it.polimi.ingsw.am32.dto.setup.CharacterCardDTO;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CharacterCardFactoryTest {

    private CharacterCardFactory factory;

    @BeforeEach
    void setUp() {
        factory = new CharacterCardFactory();
    }

    @Test
    void createCardsShouldReturnListOfValidCharacterCards() {
        List<CharacterCardDTO> dtoList = new ArrayList<>();

        dtoList.add(new CharacterCardDTO("char_72", 1, 0, 2, "SHAMAN", "{\"sha_shaman_stars\": 2}"));
        dtoList.add(new CharacterCardDTO("char_14", 2, 2, 4, "BUILDER", "{\"bld_building_discount\": 1}"));
        dtoList.add(new CharacterCardDTO("char_54", 3, 5, 2, "INVENTOR", "{\"inv_invention_type\": \"POTTERY\"}"));
        dtoList.add(new CharacterCardDTO("char_01", 1, 0, 0, "ARTIST", "{\"art_prestige_per_pair\": 10}"));
        dtoList.add(new CharacterCardDTO("char_26", 1, 0, 3, "GATHERER", "{\"gat_sustenance_discount\": 3}"));
        dtoList.add(new CharacterCardDTO("char_39", 1, 0, 3, "HUNTER", "{\"hnt_has_meat\": true}"));

        List<CharacterCard> cards = factory.createCards(dtoList);

        assertEquals(6, cards.size());
        assertEquals(CharacterType.SHAMAN, cards.get(0).getCharacterType());
        assertEquals(CharacterType.BUILDER, cards.get(1).getCharacterType());
        assertEquals(CharacterType.INVENTOR, cards.get(2).getCharacterType());
        assertEquals(CharacterType.ARTIST, cards.get(3).getCharacterType());
        assertEquals(CharacterType.GATHERER, cards.get(4).getCharacterType());
        assertEquals(CharacterType.HUNTER, cards.get(5).getCharacterType());
    }

    @Test
    void createCardsShouldThrowExceptionIfJsonIsMissingRequiredField() {
        List<CharacterCardDTO> dtoList = new ArrayList<>();
        dtoList.add(new CharacterCardDTO("Shaman_Broken", 1, 0, 2, "SHAMAN", "{\"wrong_key\": 2}"));

        assertThrows(IllegalStateException.class, () -> factory.createCards(dtoList));
    }

    @Test
    void createCardsShouldLetIllegalArgumentExceptionPropagateWhenEraIsInvalid() {
        List<CharacterCardDTO> dtoList = new ArrayList<>();
        dtoList.add(new CharacterCardDTO("Gatherer_Invalid", 5, 0, 2, "GATHERER", "{\"gat_sustenance_discount\": 3}"));

        assertThrows(IllegalArgumentException.class, () -> factory.createCards(dtoList));
    }

    @Test
    void createCardsShouldLetIllegalArgumentExceptionPropagateWhenDiscountIsNegative() {
        List<CharacterCardDTO> dtoList = new ArrayList<>();
        dtoList.add(new CharacterCardDTO("Builder_Negative", 1, 0, 2, "BUILDER", "{\"bld_building_discount\": -5}"));

        assertThrows(IllegalArgumentException.class, () -> factory.createCards(dtoList));
    }

    @Test
    void createCardsShouldThrowExceptionIfCharacterTypeIsUnknown() {
        List<CharacterCardDTO> dtoList = new ArrayList<>();
        dtoList.add(new CharacterCardDTO("Invalid_Type", 1, 0, 2, "NON_ESISTE", "{}"));

        assertThrows(IllegalArgumentException.class, () -> factory.createCards(dtoList));
    }

    @Test
    void createCardsShouldThrowExceptionIfJsonIsMalformed() {
        List<CharacterCardDTO> dtoList = new ArrayList<>();
        dtoList.add(new CharacterCardDTO("Malformed_JSON", 1, 0, 2, "SHAMAN", "{broken_json: "));

        assertThrows(RuntimeException.class, () -> factory.createCards(dtoList));
    }

    @Test
    void createCardsShouldThrowExceptionIfRequiredStringIsMissing() {
        List<CharacterCardDTO> dtoList = new ArrayList<>();
        dtoList.add(new CharacterCardDTO("Inventor_Broken", 3, 5, 2, "INVENTOR", "{\"wrong_key\": \"POTTERY\"}"));

        assertThrows(IllegalStateException.class, () -> factory.createCards(dtoList));
    }

    @Test
    void createCardsShouldThrowExceptionIfRequiredBooleanIsMissing() {
        List<CharacterCardDTO> dtoList = new ArrayList<>();
        dtoList.add(new CharacterCardDTO("Hunter_Broken", 1, 0, 3, "HUNTER", "{\"wrong_key\": true}"));

        assertThrows(IllegalStateException.class, () -> factory.createCards(dtoList));
    }
}
