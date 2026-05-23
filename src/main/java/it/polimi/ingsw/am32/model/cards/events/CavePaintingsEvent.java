package it.polimi.ingsw.am32.model.cards.events;

import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.EventType;
import it.polimi.ingsw.am32.model.enums.StatChangeReason;
import it.polimi.ingsw.am32.model.receipts.PlayerStatsChangeReceipt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the cave paintings event.
 */
public class CavePaintingsEvent extends EventCard {
    private final int prestigeBonus;
    private final int bonusThreshold;
    private final int prestigePenalty;

    /**
     * Initializes the cave paintings event with its specific parameters.
     *
     * @param id the unique identifier of the event
     * @param era the era the event belongs to
     * @param isFinal flag indicating if it is a final event
     * @param prestigeBonus the prestige gained if the threshold is met
     * @param bonusThreshold the threshold required to gain the bonus
     * @param prestigePenalty the prestige lost if the threshold is not met
     */
    public CavePaintingsEvent(String id, int era, boolean isFinal, int prestigeBonus, int bonusThreshold, int prestigePenalty) {
        super(id, era, isFinal);

        if (prestigeBonus < 0) {
            throw new IllegalArgumentException("prestigeBonus cannot be negative");
        }
        if (bonusThreshold < 0) {
            throw new IllegalArgumentException("bonusThreshold cannot be negative");
        }
        if (prestigePenalty < 0) {
            throw new IllegalArgumentException("prestigePenalty cannot be negative");
        }

        this.prestigeBonus = prestigeBonus;
        this.bonusThreshold = bonusThreshold;
        this.prestigePenalty = prestigePenalty;
    }

    /**
     * Provides the event type.
     *
     * @return the cave paintings event type
     */
    @Override
    public EventType getEventType() {
        return EventType.CAVE_PAINTINGS;
    }

    /**
     * Resolves the cave paintings event.
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

            player.applyCavePaintingsEvent(this.prestigeBonus, this.bonusThreshold, this.prestigePenalty);
            player.triggerCavePaintingsEffects(gameState);

            // stats are recorded after resolution
            int postFood = player.getFood();
            int postPrestige = player.getPrestige();

            // receipt is created to notify client of resolution results
            receipts.add(new PlayerStatsChangeReceipt(
                    player.getNickname(),
                    postFood - preFood,
                    postPrestige - prePrestige,
                    postFood,
                    postPrestige,
                    StatChangeReason.CAVE_PAINTINGS.toString()
            ));
        }
        return receipts;
    }
}
