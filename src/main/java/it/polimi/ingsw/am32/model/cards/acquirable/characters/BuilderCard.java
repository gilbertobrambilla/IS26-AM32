package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.enums.CharacterType;

import java.util.Objects;

/**
 * Represents a builder card in the game.
 */
public class BuilderCard extends CharacterCard {
    private final int buildingDiscount;

    /**
     * Initializes the builder card with its attributes.
     *
     * @param id the unique identifier of the card
     * @param era the era the card belongs to
     * @param basePrestige the base prestige value of the card
     * @param requiredPlayers the minimum number of players required
     * @param buildingDiscount the discount applied to building costs
     */
    public BuilderCard(String id, int era, int basePrestige, int requiredPlayers, int buildingDiscount) {
        super(id, era, basePrestige, requiredPlayers);

        if (buildingDiscount < 0) {
            throw new IllegalArgumentException("buildingDiscount cannot be negative");
        }

        this.buildingDiscount = buildingDiscount;
    }

    /**
     * Notifies a building card that a builder has been added.
     *
     * @param building the building card to notify
     * @param player the player who owns the cards
     */
    @Override
    public void notifyBuilding(BuildingCard building, Player player) {
        // specific building effect method is invoked
        building.onBuilderAdded(player);
    }

    /**
     * Gets the character type of the card.
     *
     * @return the character type
     */
    @Override
    public CharacterType getCharacterType() {
        return CharacterType.BUILDER;
    }

    /**
     * Applies the effects of acquiring this card.
     *
     * @param player the player acquiring the card
     */
    @Override
    public void onAcquire(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        // builder stats and base prestige are registered
        player.addBuilder();
        player.addBuildingDiscount(this.buildingDiscount);
        player.addBuilderBasePrestige(this.getBasePrestige());
    }

    /**
     * Applies the endgame effects of this card.
     *
     * @param player the player owning the card
     */
    @Override
    public void onGameEnd(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        player.addPrestige(this.getBasePrestige());
    }
}
