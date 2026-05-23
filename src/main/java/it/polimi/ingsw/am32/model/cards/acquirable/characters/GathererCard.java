package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.enums.CharacterType;

import java.util.Objects;

/**
 * Represents a gatherer card in the game.
 */
public class GathererCard extends CharacterCard {
    private final int sustenanceDiscount;

    /**
     * Initializes the gatherer card with its attributes.
     *
     * @param id the unique identifier of the card
     * @param era the era the card belongs to
     * @param basePrestige the base prestige value of the card
     * @param requiredPlayers the minimum number of players required
     * @param sustenanceDiscount the discount applied during sustenance events
     */
    public GathererCard(String id, int era, int basePrestige, int requiredPlayers, int sustenanceDiscount) {
        super(id, era, basePrestige, requiredPlayers);

        if (sustenanceDiscount < 0) {
            throw new IllegalArgumentException("sustenanceDiscount cannot be negative");
        }

        this.sustenanceDiscount = sustenanceDiscount;
    }

    /**
     * Notifies a building card that a gatherer has been added.
     *
     * @param building the building card to notify
     * @param player the player who owns the cards
     */
    @Override
    public void notifyBuilding(BuildingCard building, Player player) {
        // specific building effect method is invoked
        building.onGathererAdded(player);
    }

    /**
     * Gets the character type of the card.
     *
     * @return the character type
     */
    @Override
    public CharacterType getCharacterType() {
        return CharacterType.GATHERER;
    }

    /**
     * Applies the effects of acquiring this card.
     *
     * @param player the player acquiring the card
     */
    @Override
    public void onAcquire(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        player.addGatherer();
        player.addSustenanceDiscount(this.sustenanceDiscount);
    }
}
