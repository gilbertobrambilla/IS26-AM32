package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import it.polimi.ingsw.am32.model.enums.InventionType;

import java.util.Objects;

/**
 * Represents an inventor card in the game.
 */
public class InventorCard extends CharacterCard {
    private final InventionType invention;

    /**
     * Initializes the inventor card with its attributes.
     *
     * @param id the unique identifier of the card
     * @param era the era the card belongs to
     * @param basePrestige the base prestige value of the card
     * @param requiredPlayers the minimum number of players required
     * @param invention the type of invention provided by the card
     */
    public InventorCard(String id, int era, int basePrestige, int requiredPlayers, InventionType invention) {
        super(id, era, basePrestige, requiredPlayers);

        Objects.requireNonNull(invention, "invention cannot be null");

        this.invention = invention;
    }

    /**
     * Notifies a building card that an inventor has been added.
     *
     * @param building the building card to notify
     * @param player the player who owns the cards
     */
    @Override
    public void notifyBuilding(BuildingCard building, Player player) {
        // specific building effect method is invoked
        building.onInventorAdded(player);
    }

    /**
     * Gets the character type of the card.
     *
     * @return the character type
     */
    @Override
    public CharacterType getCharacterType() {
        return CharacterType.INVENTOR;
    }

    /**
     * Applies the effects of acquiring this card.
     *
     * @param player the player acquiring the card
     */
    @Override
    public void onAcquire(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        player.addInventor();
        player.addInvention(this.invention);
    }

    /**
     * Applies the endgame effects of this card.
     *
     * @param player the player owning the card
     */
    @Override
    public void onGameEnd(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        player.scoreInventor();
    }
}
