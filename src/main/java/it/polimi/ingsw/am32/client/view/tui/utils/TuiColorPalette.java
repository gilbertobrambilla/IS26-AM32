package it.polimi.ingsw.am32.client.view.tui.utils;

import it.polimi.ingsw.am32.model.enums.TotemColor;

/**
 * Ansi color codes for various game components are managed.
 */
public class TuiColorPalette {

    /**
     * The color code is retrieved based on the character profession type.
     *
     * @param type the character profession type
     * @return the ansi color string
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
     * Totem ansi color code is retrieved based on the totem color enum.
     *
     * @param color the enum representing the totem color
     * @return the ansi color string
     */
    public static String getTotemColor(TotemColor color) {
        if (color == null) {
            return "\033[0m";
        }

        // ansi code is mapped to high contrast 256-color palette
        switch (color.name().toUpperCase()) {
            case "RED": return "\033[38;5;167m";
            case "YELLOW": return "\033[38;5;214m";
            case "BLUE": return "\033[38;5;37m";
            case "BLACK":
            case "BROWN": return "\033[38;5;161m";
            case "WHITE":
            case "GREEN": return "\033[38;5;255m";
            default:
                return "\033[0m";
        }
    }

    /**
     * The color code for building cards is retrieved.
     *
     * @return the ansi color string
     */
    public static String getBuildingColor() {
        // light gray color is returned for buildings
        return "\033[38;5;250m";
    }

    /**
     * The color code for event cards is retrieved.
     *
     * @return the ansi color string
     */
    public static String getEventColor() {
        // brown color is returned for events
        return "\033[38;5;130m";
    }

    /**
     * The default reset color code is retrieved.
     *
     * @return the ansi reset string
     */
    public static String getReset() {
        // terminal color reset sequence is returned
        return "\033[0m";
    }
}
