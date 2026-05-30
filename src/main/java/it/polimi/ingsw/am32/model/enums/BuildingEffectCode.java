package it.polimi.ingsw.am32.model.enums;

/**
 * Lists the possible effects provided by building cards.
 */
public enum BuildingEffectCode {
    /**
     * Multiplies the endgame prestige points for builders.
     */
    ENDGAME_BUILDER_PRESTIGE_MULTIPLIER,
    /**
     * Grants endgame prestige points per specific character type.
     */
    ENDGAME_PRESTIGE_PER_CHARACTER,
    /**
     * Grants endgame prestige points per completed set of different characters.
     */
    ENDGAME_PRESTIGE_PER_SET,
    /**
     * Allows taking an extra card at the end of the round.
     */
    EXTRA_CARD_ON_ROUND_END,
    /**
     * Grants extra food when placing the totem on a food-yielding turn order space.
     */
    EXTRA_FOOD_ON_TOTEM,
    /**
     * Grants extra resources during specific events like hunt or cave paintings.
     */
    EXTRA_RESOURCES_ON_EVENT,
    /**
     * Grants extra shaman stars during the shamanic ritual event.
     */
    EXTRA_SHAMAN_STARS,
    /**
     * Grants food for each pair of inventors with the same icon.
     */
    FOOD_PER_INVENTOR_PAIR,
    /**
     * Grants food per completed set of different characters.
     */
    FOOD_PER_SET,
    /**
     * Represents a building with no special effect.
     */
    NO_EFFECT,
    /**
     * Grants immunity to prestige penalties during the shamanic ritual event.
     */
    SHAMANIC_PENALTY_IMMUNITY,
    /**
     * Grants double prestige points during the shamanic ritual event if holding the majority.
     */
    SHAMANIC_DOUBLE_REWARD,
    /**
     * Grants a sustenance discount per specific character type during the sustenance event.
     */
    SUSTENANCE_DISCOUNT_PER_CHARACTER
}
