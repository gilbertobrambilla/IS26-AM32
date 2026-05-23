package it.polimi.ingsw.am32.model.enums;

/**
 * Lists the possible reasons for a change in a player's statistics.
 */
public enum StatChangeReason {
    /**
     * Indicates the cost paid for acquiring buildings during the standard action phase.
     */
    ACTION_COST,
    /**
     * Indicates passive bonuses triggered by building effects.
     */
    BUILDING_PASSIVE,
    /**
     * Indicates bonuses or penalties applied when placing the totem on the turn order tile.
     */
    TURN_ORDER_BONUS,
    /**
     * Indicates the cost paid for acquiring an additional card during the extra acquisition phase.
     */
    EXTRA_ACQUISITION_COST,
    /**
     * Indicates stat changes caused by the cave paintings event.
     */
    CAVE_PAINTINGS,
    /**
     * Indicates stat changes caused by the hunt event.
     */
    HUNT,
    /**
     * Indicates stat changes caused by the shamanic ritual event.
     */
    SHAMANIC_RITUAL,
    /**
     * Indicates stat changes caused by the sustenance event.
     */
    SUSTENANCE
}
