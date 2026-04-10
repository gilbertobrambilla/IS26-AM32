package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.enums.CharacterType;

import java.util.Objects;

public class GathererCard extends CharacterCard {
    private final int sustenanceDiscount;

    public GathererCard(String id, int era, int basePrestige, int requiredPlayers, int sustenanceDiscount) {
        super(id, era, basePrestige, requiredPlayers);

        if (sustenanceDiscount < 0) {
            throw new IllegalArgumentException("sustenanceDiscount cannot be negative");
        }

        this.sustenanceDiscount = sustenanceDiscount;
    }

    @Override
    public CharacterType getCharacterType() {
        return CharacterType.GATHERER;
    }

    @Override
    public void onAcquire(Player player){
        Objects.requireNonNull(player, "player cannot be null");

        player.addGatherer();
        player.addSustenanceDiscount(this.sustenanceDiscount);
    }

    @Override
    public void notifyBuilding(BuildingCard building, Player player) {
        // specific building effect method is invoked
        building.onGathererAdded(player);
    }
}
