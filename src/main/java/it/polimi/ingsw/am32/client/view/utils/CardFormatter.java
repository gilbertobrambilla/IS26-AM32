package it.polimi.ingsw.am32.client.view.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am32.dto.setup.BuildingCardDTO;
import it.polimi.ingsw.am32.dto.setup.CharacterCardDTO;
import it.polimi.ingsw.am32.dto.setup.EventCardDTO;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Card formatter is implemented to translate raw dto data into user-friendly strings.
 */
public class CardFormatter {

    private static final Map<String, String> dictionary = new HashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static boolean isLoaded = false;

    /**
     * Card formatter is instantiated.
     */
    public CardFormatter() {

    }

    /**
     * Dictionary is loaded from the specified resource file.
     *
     * @param resourcePath the path of the json dictionary file.
     */
    public void loadDictionary(String resourcePath) {
        if (isLoaded) return;

        try (InputStream is = CardFormatter.class.getResourceAsStream("/" + resourcePath)) {
            if (is == null) {
                System.err.println("[CardFormatter] Dictionary not found: " + resourcePath);
                return;
            }

            // root node is read generically to handle nested structure
            JsonNode rootNode = mapper.readTree(is);

            // all nested entries are flattened into the dictionary
            flattenInto(rootNode, dictionary);
            isLoaded = true;
            System.out.println("[CardFormatter] Dictionary successfully loaded in memory.");
        } catch (Exception e) {
            System.err.println("[CardFormatter] Dictionary could not be loaded: " + e.getMessage());
        }
    }

    private void flattenInto(JsonNode node, Map<String, String> target) {
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                if (entry.getValue().isTextual()) {
                    // leaf node — add directly to dictionary
                    target.put(entry.getKey(), entry.getValue().asText());
                } else {
                    // nested object — recurse
                    flattenInto(entry.getValue(), target);
                }
            });
        }
    }

    /**
     * Building card is formatted into a readable string.
     *
     * @param building the building card dto.
     * @return the formatted description.
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
     * @param character the character card dto.
     * @return the formatted description.
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
     * @param event the event card dto.
     * @return the formatted description.
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
     * @param template the base text template.
     * @param jsonParams the json string containing parameters.
     * @return the string with injected parameters.
     */
    private String injectParameters(String template, String jsonParams) {
        // null or empty params are checked
        if (jsonParams == null || jsonParams.trim().isEmpty() || "{}".equals(jsonParams)) {
            // plain template is returned
            return template;
        }

        String result = template;
        try {
            // parameters are parsed into a map
            Map<String, Object> params = mapper.readValue(jsonParams, new TypeReference<Map<String, Object>>() {});

            // map entries are iterated
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                // value is converted to string
                String valueStr = String.valueOf(entry.getValue());
                // translated value is fetched from dictionary
                String translatedValue = dictionary.getOrDefault(valueStr, valueStr);
                // placeholder is replaced in the template
                result = result.replace("{" + entry.getKey() + "}", translatedValue);
            }
        } catch (Exception e) {
            // formatting fallback is applied in case of parsing error
            result += " " + jsonParams;
        }

        // final formatted string is returned
        return result;
    }
}
