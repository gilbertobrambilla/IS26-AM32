package it.polimi.ingsw.am32.client.view.tui.utils;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.am32.client.view.utils.GameDictionary;
import it.polimi.ingsw.am32.dto.setup.BuildingCardDTO;
import it.polimi.ingsw.am32.dto.setup.CharacterCardDTO;
import it.polimi.ingsw.am32.dto.setup.EventCardDTO;

/**
 * Card formatter is implemented as a singleton to translate raw dto data into compact tui strings.
 */
public class CardFormatter {
    private static CardFormatter instance;

    /**
     * Card formatter is instantiated privately.
     */
    private CardFormatter() {
        // internal maps and mappers are removed to respect srp
    }

    /**
     * Singleton instance is retrieved.
     *
     * @return the card formatter instance
     */
    public static synchronized CardFormatter getInstance() {
        // instance is created if null
        if (instance == null) {
            instance = new CardFormatter();
        }
        // instance is returned
        return instance;
    }

    /**
     * Character card is formatted into a readable string using specific parameter logic.
     *
     * @param character the character card dto
     * @return the formatted description
     */
    public String formatCharacter(CharacterCardDTO character) {
        // character type and params are retrieved
        String type = character.getCharacterType();
        JsonNode params = character.getCharacterParams();
        StringBuilder sb = new StringBuilder();

        // character logic is branched based on raw dto type
        switch (type.toUpperCase()) {
            case "HUNTER":
                // meat icon is appended if present
                if (params.has("hasMeat") && params.get("hasMeat").asInt() == 1) sb.append("🍖");
                break;
            case "SHAMAN":
                // stars icons are appended
                if (params.has("shamanStars")) {
                    int stars = params.get("shamanStars").asInt();
                    sb.append("⭐".repeat(Math.max(0, stars)));
                }
                break;
            case "INVENTOR":
                // invention is retrieved and translated via shared dictionary
                if (params.has("invention")) {
                    String inv = params.get("invention").asText();
                    sb.append(GameDictionary.getInstance().translate(inv));
                }
                break;
            case "GATHERER":
                // sustenance discount is formatted
                if (params.has("sustenanceDiscount")) sb.append("-").append(params.get("sustenanceDiscount").asInt()).append(" 🍖");
                break;
            case "BUILDER":
                // building discount is formatted
                if (params.has("buildingDiscount")) sb.append("-").append(params.get("buildingDiscount").asInt()).append(" 🍖");
                break;
            case "ARTIST":
                break;
            default:
                return "";
        }
        return sb.toString();
    }

    /**
     * Event card is formatted into a highly compact string with strategic newlines.
     *
     * @param event the event card dto
     * @return the formatted description
     */
    public String formatEvent(EventCardDTO event) {
        // event type and params are retrieved
        String type = event.getEventType();
        JsonNode params = event.getEventParams();

        if (params == null || params.isEmpty()) return "";

        // event logic is branched based on raw dto type
        switch (type.toUpperCase()) {
            case "CAVE_PAINTINGS":
                int bThreshold = params.has("bonusThreshold") ? params.get("bonusThreshold").asInt() : 0;
                int pBonus = params.has("prestigeBonus") ? params.get("prestigeBonus").asInt() : 0;
                int pPenalty = params.has("prestigePenalty") ? params.get("prestigePenalty").asInt() : 0;
                // newline is inserted to separate the conditions
                return "≥" + bThreshold + " Art: +" + pBonus + " 🏆\n<" + bThreshold + " Art: -" + pPenalty + " 🏆";

            case "HUNT":
                int hFood = params.has("foodBonus") ? params.get("foodBonus").asInt() : 0;
                int hPrestige = params.has("prestigeBonus") ? params.get("prestigeBonus").asInt() : 0;
                return "+" + hFood + " 🍖 & +" + hPrestige + " 🏆\nper Hunter";

            case "SHAMANIC_RITUAL":
                int sBonus = params.has("prestigeBonus") ? params.get("prestigeBonus").asInt() : 0;
                int sPenalty = params.has("prestigePenalty") ? params.get("prestigePenalty").asInt() : 0;
                return "Max ⭐: +" + sBonus + " 🏆\nMin ⭐: -" + sPenalty + " 🏆";

            case "SUSTENANCE":
                int foodCost = params.has("foodCost") ? params.get("foodCost").asInt() : 0;
                int unfedPenalty = params.has("prestigePenalty") ? params.get("prestigePenalty").asInt() : 0;
                return "-" + foodCost + " 🍖 / Char\nUnfed: -" + unfedPenalty + " 🏆";

            default:
                return "";
        }
    }

    /**
     * Building card is formatted into a highly compact string.
     *
     * @param building the building card dto
     * @return the formatted description
     */
    public String formatBuilding(BuildingCardDTO building) {
        String code = building.getEffectCode();
        JsonNode params = building.getEffectParams();

        if (params == null) return "";

        // shared dictionary instance is retrieved
        GameDictionary dict = GameDictionary.getInstance();

        switch (code.toUpperCase()) {
            case "SUSTENANCE_DISCOUNT_PER_CHARACTER":
                int discount = params.has("discountPerCharacter") ? params.get("discountPerCharacter").asInt() : 0;
                String target = params.has("targetCharacter") ? params.get("targetCharacter").asText() : "Char";
                return "-" + discount + " 🍖 / " + dict.translate(target);
            case "SHAMANIC_PENALTY_IMMUNITY":
                return "Immune to\nShaman penalty";
            case "EXTRA_FOOD_ON_TOTEM":
                int fTotem = params.has("extraFood") ? params.get("extraFood").asInt() : 0;
                return "+" + fTotem + " 🍖 on Totem Placed";
            case "FOOD_PER_INVENTOR_PAIR":
                int fPair = params.has("extraFood") ? params.get("extraFood").asInt() : 0;
                return "+" + fPair + " 🍖 / Inventor pair";
            case "SHAMANIC_DOUBLE_REWARD":
                return "Double 🏆 from\nShamanic ritual";
            case "EXTRA_SHAMAN_STARS":
                int eStars = params.has("extraStars") ? params.get("extraStars").asInt() : 0;
                return "+" + eStars + " ⭐";
            case "EXTRA_RESOURCES_ON_EVENT":
                int eFood = params.has("extraFood") ? params.get("extraFood").asInt() : 0;
                int ePrestige = params.has("extraPrestige") ? params.get("extraPrestige").asInt() : 0;
                String eEvent = params.has("targetEvent") ? params.get("targetEvent").asText() : "Event";
                return "On " + dict.translate(eEvent) + ":\n+" + eFood + " 🍖 & +" + ePrestige + " 🏆";
            case "ENDGAME_BUILDER_PRESTIGE_MULTIPLIER":
                int mult = params.has("multiplier") ? params.get("multiplier").asInt() : 0;
                return "Builder 🏆 x" + mult + "\nat endgame";
            case "FOOD_PER_SET":
                int setFood = params.has("extraFood") ? params.get("extraFood").asInt() : 0;
                return "+" + setFood + " 🍖 / Full Set";
            case "ENDGAME_PRESTIGE_PER_SET":
                int setPrestige = params.has("pointsPerSet") ? params.get("pointsPerSet").asInt() : 0;
                return "+" + setPrestige + " 🏆 / Set at endgame";
            case "ENDGAME_PRESTIGE_PER_CHARACTER":
                int charPrestige = params.has("pointsPerCharacter") ? params.get("pointsPerCharacter").asInt() : 0;
                String charTarget = params.has("targetCharacter") ? params.get("targetCharacter").asText() : "Char";
                return "+" + charPrestige + " 🏆 / " + dict.translate(charTarget) + "\nat endgame";
            case "EXTRA_CARD_ON_ROUND_END":
                return "Draw 1 extra\ncard at end";
            default:
                return "";
        }
    }
}
