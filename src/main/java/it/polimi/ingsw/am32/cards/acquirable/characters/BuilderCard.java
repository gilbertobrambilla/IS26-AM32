package it.polimi.ingsw.am32.cards.acquirable.characters;

import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.enums.CharacterType;

import java.util.Objects;

public class BuilderCard extends CharacterCard {
    private final int buildingDiscount;

    public BuilderCard(String id, int era, int basePrestige, int requiredPlayers, int buildingDiscount) {
        super(id, era, basePrestige, requiredPlayers);

        if (buildingDiscount < 0) {
            throw new IllegalArgumentException("buildingDiscount cannot be negative");
        }

        this.buildingDiscount = buildingDiscount;
    }

    @Override
    public CharacterType getCharacterType() {
        return CharacterType.BUILDER;
    }

    @Override
    public void onAcquire(Player player){
        Objects.requireNonNull(player, "player cannot be null");

        // builder stats and base prestige are registered
        player.addBuilder();
        player.addBuildingDiscount(this.buildingDiscount);
        player.addBuilderBasePrestige(this.getBasePrestige());
    }

    @Override
    public void onGameEnd(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        player.addPrestige(this.getBasePrestige());
    }

    @Override
    public void notifyBuilding(BuildingCard building, Player player) {
        // specific building effect method is invoked
        building.onBuilderAdded(player);
    }
}
