package it.polimi.ingsw.am32.model.cards.acquirable.characters;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.enums.CharacterType;

import java.util.Objects;

public class HunterCard extends CharacterCard {
    private final boolean hasMeat;

    public HunterCard(String id, int era, int basePrestige, int requiredPlayers, boolean hasMeat){
        super(id, era, basePrestige, requiredPlayers);
        this.hasMeat = hasMeat;
    }

    @Override
    public CharacterType getCharacterType() {
        return CharacterType.HUNTER;
    }

    @Override
    public void onAcquire(Player player){
        Objects.requireNonNull(player, "player cannot be null");

        player.addHunter(this.hasMeat);
    }

    @Override
    public void notifyBuilding(BuildingCard building, Player player) {
        // specific building effect method is invoked
        building.onHunterAdded(player);
    }
}
