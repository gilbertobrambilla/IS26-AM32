package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.CardVisitor;
import it.polimi.ingsw.am32.model.cards.acquirable.AcquirableCard;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.enums.CharacterType;

import java.util.Objects;

/**
 * Represents an abstract character card in the game.
 */
public abstract class CharacterCard extends AcquirableCard {
    private final int requiredPlayers;

    /**
     * Initializes the character card with an identifier, an era, base prestige, and required players.
     *
     * @param id the unique identifier of the card
     * @param era the era the card belongs to
     * @param basePrestige the base prestige value of the card
     * @param requiredPlayers the minimum number of players required for this card
     */
    public CharacterCard(String id, int era, int basePrestige, int requiredPlayers) {
        super(id, era, basePrestige);

        if (requiredPlayers < 0) {
            throw new IllegalArgumentException("requiredPlayers cannot be negative");
        }

        this.requiredPlayers = requiredPlayers;
    }

    /**
     * Accepts a visitor to perform operations on the card.
     *
     * @param visitor the visitor to accept
     */
    @Override
    public void accept(CardVisitor visitor) {
        Objects.requireNonNull(visitor, "visitor cannot be null");

        visitor.visit(this);
    }

    /**
     * Notifies a building card that this character has been added.
     *
     * @param building the building card to notify
     * @param player the player who owns the cards
     */
    public abstract void notifyBuilding(BuildingCard building, Player player);

    /**
     * Gets the character type of the card.
     *
     * @return the character type
     */
    public abstract CharacterType getCharacterType();

    /**
     * Applies the effects of acquiring this card.
     *
     * @param player the player acquiring the card
     */
    public void onAcquire(Player player) {
    }

    /**
     * Applies the endgame effects of this card.
     *
     * @param player the player owning the card
     */
    public void onGameEnd(Player player) {
    }

    /**
     * Gets the required number of players for this card.
     *
     * @return the required number of players
     */
    public int getRequiredPlayers() {
        return this.requiredPlayers;
    }
}
