package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.enums.CharacterType;

import java.util.Objects;

/**
 * Represents a hunter card in the game.
 */
public class HunterCard extends CharacterCard {
    private final boolean hasMeat;

    /**
     * Initializes the hunter card with its attributes.
     *
     * @param id the unique identifier of the card
     * @param era the era the card belongs to
     * @param basePrestige the base prestige value of the card
     * @param requiredPlayers the minimum number of players required
     * @param hasMeat true if the hunter has meat, false otherwise
     */
    public HunterCard(String id, int era, int basePrestige, int requiredPlayers, boolean hasMeat) {
        super(id, era, basePrestige, requiredPlayers);
        this.hasMeat = hasMeat;
    }

    /**
     * Notifies a building card that a hunter has been added.
     *
     * @param building the building card to notify
     * @param player the player who owns the cards
     */
    @Override
    public void notifyBuilding(BuildingCard building, Player player) {
        // specific building effect method is invoked
        building.onHunterAdded(player);
    }

    /**
     * Gets the character type of the card.
     *
     * @return the character type
     */
    @Override
    public CharacterType getCharacterType() {
        return CharacterType.HUNTER;
    }

    /**
     * Applies the effects of acquiring this card.
     *
     * @param player the player acquiring the card
     */
    @Override
    public void onAcquire(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        player.addHunter(this.hasMeat);
    }
}
