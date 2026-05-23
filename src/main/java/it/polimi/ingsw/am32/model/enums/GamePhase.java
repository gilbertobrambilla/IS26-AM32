package it.polimi.ingsw.am32.model.enums;

/**
 * Lists the different phases within a single game round.
 */
public enum GamePhase {
    /**
     * The phase where players place their totems on offer tiles.
     */
    PLACE_TOTEMS,
    /**
     * The phase where players resolve the actions of their chosen offer tiles.
     */
    RESOLVE_ACTIONS,
    /**
     * The optional phase where a specific building effect allows an extra card acquisition.
     */
    EXTRA_ACQUISITION,
    /**
     * The phase where the events present in the lower row are resolved.
     */
    RESOLVE_EVENTS,
    /**
     * The cleanup phase at the end of the round.
     */
    END_ROUND
}
