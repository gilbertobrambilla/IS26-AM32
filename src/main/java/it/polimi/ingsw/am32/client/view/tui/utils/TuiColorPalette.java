package it.polimi.ingsw.am32.client.view.tui.utils;

/**
 * Utility class for managing ansi color codes for various game components.
 */
public class TuiColorPalette {

    /**
     * The color code is retrieved based on the character profession type.
     *
     * @param type the character profession type.
     * @return the ansi color string.
     */
    public static String getCharacterColor(String type) {
        // default color is returned if type is null
        if (type == null) return "\033[0m";

        // ansi sequence is selected based on the specific profession
        switch (type.toUpperCase()) {
            case "ARTIST": return "\033[38;5;135m";
            case "BUILDER": return "\033[38;5;214m";
            case "GATHERER": return "\033[38;5;70m";
            case "HUNTER": return "\033[38;5;196m";
            case "INVENTOR": return "\033[38;5;39m";
            case "SHAMAN": return "\033[38;5;93m";
            default: return "\033[0m";
        }
    }

    /**
     * The color code for building cards is retrieved.
     *
     * @return the ansi color string.
     */
    public static String getBuildingColor() {
        // light gray color is returned for buildings
        return "\033[38;5;250m";
    }

    /**
     * The color code for event cards is retrieved.
     *
     * @return the ansi color string.
     */
    public static String getEventColor() {
        // brown color is returned for events
        return "\033[38;5;130m";
    }

    /**
     * The default reset color code is retrieved.
     *
     * @return the ansi reset string.
     */
    public static String getReset() {
        // terminal color reset sequence is returned
        return "\033[0m";
    }
}
