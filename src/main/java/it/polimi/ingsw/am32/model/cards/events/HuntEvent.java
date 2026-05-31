package it.polimi.ingsw.am32.model.cards.events;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.EventType;
import it.polimi.ingsw.am32.model.receipts.PlayerStatsChangeReceipt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The hunt event is represented.
 */
public class HuntEvent extends EventCard {
    private final int foodBonus;
    private final int prestigeBonus;

    /**
     * The hunt event is initialized with its specific parameters.
     *
     * @param id the unique identifier of the event
     * @param era the era the event belongs to
     * @param isFinal flag indicating if it is a final event
     * @param foodBonus the amount of food gained
     * @param prestigeBonus the amount of prestige gained
     */
    public HuntEvent(String id, int era, boolean isFinal, int foodBonus, int prestigeBonus) {
        super(id, era, isFinal);

        if (foodBonus < 0) {
            throw new IllegalArgumentException("foodBonus cannot be negative");
        }
        if (prestigeBonus < 0) {
            throw new IllegalArgumentException("prestigeBonus cannot be negative");
        }

        this.foodBonus = foodBonus;
        this.prestigeBonus = prestigeBonus;
    }

    /**
     * The event type is returned.
     *
     * @return the hunt event type
     */
    @Override
    public EventType getEventType() {
        return EventType.HUNT;
    }

    /**
     * The hunt event is resolved.
     *
     * @param gameState the current state of the game
     * @return a list of receipts detailing the changes in player statistics
     */
    @Override
    public List<PlayerStatsChangeReceipt> resolveEvent(GameState gameState) {
        Objects.requireNonNull(gameState, "gameState cannot be null");
        List<PlayerStatsChangeReceipt> receipts = new ArrayList<>();

        for (Player player : gameState.getPlayers()) {
            // stats are recorded before resolution
            int preFood = player.getFood();
            int prePrestige = player.getPrestige();

            player.applyHuntEvent(this.foodBonus, this.prestigeBonus);
            player.triggerHuntEffects(gameState);

            // stats are recorded after resolution
            int postFood = player.getFood();
            int postPrestige = player.getPrestige();

            // receipt is created to notify client of resolution results
            receipts.add(new PlayerStatsChangeReceipt(
                    player.getNickname(),
                    postFood - preFood,
                    postPrestige - prePrestige,
                    postFood,
                    postPrestige
            ));
        }
        return receipts;
    }
}
