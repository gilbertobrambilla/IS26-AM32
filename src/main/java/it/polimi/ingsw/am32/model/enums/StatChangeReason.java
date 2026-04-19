package it.polimi.ingsw.am32.model.enums;

/**
 * enumerates the possible reasons for a change in a player's statistics
 */
public enum StatChangeReason {
    /**
     * cost paid for acquiring buildings during the standard action phase
     */
    ACTION_COST,

    /**
     * passive bonuses triggered by building effects
     */
    BUILDING_PASSIVE,

    /**
     * bonuses or penalties applied when placing the totem on the turn order tile
     */
    TURN_ORDER_BONUS,

    /**
     * cost paid for acquiring an additional card during the extra acquisition phase
     */
    EXTRA_ACQUISITION_COST,

    /**
     * stat changes caused by the cave paintings event
     */
    CAVE_PAINTINGS,

    /**
     * stat changes caused by the hunt event
     */
    HUNT,

    /**
     * stat changes caused by the shamanic ritual event
     */
    SHAMANIC_RITUAL,

    /**
     * stat changes caused by the sustenance event
     */
    SUSTENANCE
}
