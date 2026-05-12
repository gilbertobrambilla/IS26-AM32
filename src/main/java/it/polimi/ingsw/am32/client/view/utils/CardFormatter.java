package it.polimi.ingsw.am32.client.view.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am32.dto.setup.BuildingCardDTO;
import it.polimi.ingsw.am32.dto.setup.CharacterCardDTO;
import it.polimi.ingsw.am32.dto.setup.EventCardDTO;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Card formatter is implemented to translate raw dto data into user-friendly strings.
 */
public class CardFormatter {

    private final Map<String, String> dictionary;
    private final ObjectMapper mapper;

    /**
     * Card formatter is instantiated.
     */
    public CardFormatter() {
        // dictionary is initialized
        this.dictionary = new HashMap<>();
        // object mapper is instantiated for dictionary parsing only
        this.mapper = new ObjectMapper();
    }

    /**
     * Dictionary is loaded from the specified resource file.
     *
     * @param resourcePath the path of the JSON dictionary file
     */
    public void loadDictionary(String resourcePath) {
        try {
            // input stream is opened from resources
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (is != null) {
                // json is deserialized into the dictionary map
                Map<String, String> loadedData = mapper.readValue(is, new TypeReference<>() {});
                // dictionary is populated
                this.dictionary.putAll(loadedData);
            }
        } catch (Exception e) {
            // exception is ignored or logged safely
            System.err.println("dictionary could not be loaded");
        }
    }

    /**
     * Building card is formatted into a readable string.
     *
     * @param building the building card dto
     * @return the formatted description
     */
    public String formatBuilding(BuildingCardDTO building) {
        // effect code is retrieved
        String effectCode = building.getEffectCode();
        // base template is fetched from dictionary
        String template = dictionary.getOrDefault(effectCode, effectCode);

        // formatted string is returned
        return injectParameters(template, building.getEffectParams());
    }

    /**
     * Character card is formatted into a readable string.
     *
     * @param character the character card dto
     * @return the formatted description
     */
    public String formatCharacter(CharacterCardDTO character) {
        // character type is retrieved
        String type = character.getCharacterType();
        // base template is fetched from dictionary
        String template = dictionary.getOrDefault(type, type);

        // formatted string is returned
        return injectParameters(template, character.getCharacterParams());
    }

    /**
     * Event card is formatted into a readable string.
     *
     * @param event the event card dto
     * @return the formatted description
     */
    public String formatEvent(EventCardDTO event) {
        // event type is retrieved
        String type = event.getEventType();
        // base template is fetched from dictionary
        String template = dictionary.getOrDefault(type, type);

        // formatted string is returned
        return injectParameters(template, event.getEventParams());
    }

    /**
     * Json parameters are injected into the text template.
     *
     * @param template the base text template
     * @param jsonParams the JSON node containing parameters
     * @return the string with injected parameters
     */
    private String injectParameters(String template, JsonNode jsonParams) {
        // null or empty params are checked
        if (jsonParams == null || jsonParams.isEmpty()) {
            // plain template is returned
            return template;
        }

        String result = template;

        // parameters fields are iterated
        Iterator<Map.Entry<String, JsonNode>> fields = jsonParams.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            // value is converted to string
            String valueStr = entry.getValue().asText();
            // translated value is fetched from dictionary
            String translatedValue = dictionary.getOrDefault(valueStr, valueStr);
            // placeholder is replaced in the template
            result = result.replace("{" + entry.getKey() + "}", translatedValue);
        }

        // final formatted string is returned
        return result;
    }
}
