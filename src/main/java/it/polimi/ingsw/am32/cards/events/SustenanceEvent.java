package it.polimi.ingsw.am32.cards.events;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.enums.EventType;

import java.util.Objects;

public class SustenanceEvent extends EventCard {
    private int foodCost;
    private int prestigePenalty;

    public SustenanceEvent(String id, int era, boolean isFinal, int foodCost, int prestigePenalty) {
        super(id, era, isFinal);

        if (foodCost < 0) {
            throw new IllegalArgumentException("foodCost cannot be negative");
        }
        if (prestigePenalty < 0) {
            throw new IllegalArgumentException("prestigePenalty cannot be negative");
        }

        this.foodCost = foodCost;
        this.prestigePenalty = prestigePenalty;
    }

    @Override
    public EventType getEventType() {
        return EventType.SUSTENANCE;
    }

    @Override
    public void resolveEvent(GameState gameState) {
        Objects.requireNonNull(gameState, "gameState cannot be null");

        for (Player player : gameState.getPlayers()) {
            int totalCharacters = player.getCharacterCount();
            int grossCost = totalCharacters * this.foodCost;
            int discount = player.getSustenanceDiscount();

            // calculate net cost ensuring it does not drop below zero
            int netCost = Math.max(0, grossCost - discount);
            int currentFood = player.getFood();

            if (currentFood >= netCost) {
                player.removeFood(netCost);
            } else {
                // player pays all available food, penalty applied for each unfed character
                player.removeFood(currentFood);
                int unfedCharacters = (netCost - currentFood) / this.foodCost;
                player.removePrestige(unfedCharacters * this.prestigePenalty);
            }
        }
    }
}
