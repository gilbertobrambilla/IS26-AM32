package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.enums.CharacterType;

import java.util.Objects;

/**
 * Represents an artist card in the game.
 */
public class ArtistCard extends CharacterCard {
    private final int prestigePerPair;

    /**
     * Initializes the artist card with its attributes.
     *
     * @param id the unique identifier of the card
     * @param era the era the card belongs to
     * @param basePrestige the base prestige value of the card
     * @param requiredPlayers the minimum number of players required
     * @param prestigePerPair the prestige points awarded per artist pair
     */
    public ArtistCard(String id, int era, int basePrestige, int requiredPlayers, int prestigePerPair) {
        super(id, era, basePrestige, requiredPlayers);

        if (prestigePerPair < 0) {
            throw new IllegalArgumentException("prestigePerPair cannot be negative");
        }

        this.prestigePerPair = prestigePerPair;
    }

    /**
     * Notifies a building card that an artist has been added.
     *
     * @param building the building card to notify
     * @param player the player who owns the cards
     */
    @Override
    public void notifyBuilding(BuildingCard building, Player player) {
        // specific building effect method is invoked
        building.onArtistAdded(player);
    }

    /**
     * Gets the character type of the card.
     *
     * @return the character type
     */
    @Override
    public CharacterType getCharacterType() {
        return CharacterType.ARTIST;
    }

    /**
     * Applies the effects of acquiring this card.
     *
     * @param player the player acquiring the card
     */
    @Override
    public void onAcquire(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        player.addArtist();
    }

    /**
     * Applies the endgame effects of this card.
     *
     * @param player the player owning the card
     */
    @Override
    public void onGameEnd(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        player.scoreArtist(this.prestigePerPair);
    }
}
