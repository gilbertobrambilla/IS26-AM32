package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.enums.CharacterType;

import java.util.Objects;

/**
 * Represents a shaman card in the game.
 */
public class ShamanCard extends CharacterCard {
    private final int shamanStars;

    /**
     * Initializes the shaman card with its attributes.
     *
     * @param id the unique identifier of the card
     * @param era the era the card belongs to
     * @param basePrestige the base prestige value of the card
     * @param requiredPlayers the minimum number of players required
     * @param shamanStars the number of stars provided by the shaman
     */
    public ShamanCard(String id, int era, int basePrestige, int requiredPlayers, int shamanStars) {
        super(id, era, basePrestige, requiredPlayers);

        if (shamanStars < 1 || shamanStars > 3) {
            throw new IllegalArgumentException("shamanStars must be between 1 and 3");
        }

        this.shamanStars = shamanStars;
    }

    /**
     * Notifies a building card that a shaman has been added.
     *
     * @param building the building card to notify
     * @param player the player who owns the cards
     */
    @Override
    public void notifyBuilding(BuildingCard building, Player player) {
        // specific building effect method is invoked
        building.onShamanAdded(player);
    }

    /**
     * Gets the character type of the card.
     *
     * @return the character type
     */
    @Override
    public CharacterType getCharacterType() {
        return CharacterType.SHAMAN;
    }

    /**
     * Applies the effects of acquiring this card.
     *
     * @param player the player acquiring the card
     */
    @Override
    public void onAcquire(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        player.addShaman();
        player.addShamanStars(this.shamanStars);
    }
}
