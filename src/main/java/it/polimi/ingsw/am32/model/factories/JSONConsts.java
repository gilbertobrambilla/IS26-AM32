package it.polimi.ingsw.am32.model.factories;

/**
 * Utility class containing constants for JSON field names used in factories.
 */
public class JSONConsts {

    // building cards parameters
    public static final String BUILD_MULTIPLIER = "multiplier";
    public static final String BUILD_TARGET_CHAR = "targetCharacter";
    public static final String BUILD_PTS_PER_CHAR = "pointsPerCharacter";
    public static final String BUILD_PTS_PER_SET = "pointsPerSet";
    public static final String BUILD_DISC_PER_CHAR = "discountPerCharacter";
    public static final String BUILD_TARGET_EVENT = "targetEvent";
    public static final String BUILD_EXTRA_FOOD = "extraFood";
    public static final String BUILD_EXTRA_PRESTIGE = "extraPrestige";
    public static final String BUILD_EXTRA_STARS = "extraStars";

    // character cards parameters
    public static final String CHAR_ART_PRESTIGE = "prestigePerPair";
    public static final String CHAR_BLD_DISCOUNT = "buildingDiscount";
    public static final String CHAR_GAT_DISCOUNT = "sustenanceDiscount";
    public static final String CHAR_HNT_HAS_MEAT = "hasMeat";
    public static final String CHAR_INV_TYPE = "invention";
    public static final String CHAR_SHA_STARS = "shamanStars";

    // event cards parameters
    public static final String EVENT_IS_FINAL = "isFinal";

    public static final String EVENT_CAVE_PRESTIGE_BONUS = "prestigeBonus";
    public static final String EVENT_CAVE_BONUS_THRESH = "bonusThreshold";
    public static final String EVENT_CAVE_PRESTIGE_PENALTY = "prestigePenalty";

    public static final String EVENT_HUNT_FOOD_BONUS = "foodBonus";
    public static final String EVENT_HUNT_PRESTIGE_BONUS = "prestigeBonus";

    public static final String EVENT_SHAM_PRESTIGE_BONUS = "prestigeBonus";
    public static final String EVENT_SHAM_PRESTIGE_PENALTY = "prestigePenalty";

    public static final String EVENT_SUST_FOOD_COST = "foodCost";
    public static final String EVENT_SUST_PRESTIGE_PENALTY = "prestigePenalty";
}
