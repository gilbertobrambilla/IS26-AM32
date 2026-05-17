package it.polimi.ingsw.am32.client.view.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Shared dictionary is implemented as a singleton to translate raw keys into localized strings.
 */
public class GameDictionary {
    private static GameDictionary instance;
    private final Map<String, String> dictionary;
    private final ObjectMapper mapper;

    /**
     * Game dictionary is instantiated privately.
     */
    private GameDictionary() {
        // dictionary is initialized
        this.dictionary = new HashMap<>();
        // object mapper is instantiated
        this.mapper = new ObjectMapper();
    }

    /**
     * Singleton instance is retrieved.
     *
     * @return the game dictionary instance
     */
    public static synchronized GameDictionary getInstance() {
        // instance is created if null
        if (instance == null) {
            instance = new GameDictionary();
        }
        // instance is returned
        return instance;
    }

    /**
     * Dictionary is loaded and flattened from the provided input stream.
     *
     * @param is the input stream containing json data
     */
    public void loadFromJson(InputStream is) {
        try {
            if (is == null) {
                // error is logged if stream is missing
                System.err.println("[ERROR] Dictionary stream is null");
                return;
            }

            // JSON is parsed as a tree
            JsonNode root = mapper.readTree(is);

            // root categories are iterated to flatten the dictionary
            Iterator<Map.Entry<String, JsonNode>> categories = root.fields();
            while (categories.hasNext()) {
                JsonNode categoryNode = categories.next().getValue();
                Iterator<Map.Entry<String, JsonNode>> entries = categoryNode.fields();
                while (entries.hasNext()) {
                    Map.Entry<String, JsonNode> entry = entries.next();
                    // key and value are put into the flat map
                    this.dictionary.put(entry.getKey(), entry.getValue().asText());
                }
            }
        } catch (Exception e) {
            // exception is fully logged for debugging
            System.err.println("[ERROR] Failed to parse dictionary: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Term is translated using the loaded dictionary.
     *
     * @param key the term to be translated
     * @return the translated string or the original key if not found
     */
    public String translate(String key) {
        // translation is returned or fallback to key
        return dictionary.getOrDefault(key, key);
    }
}
