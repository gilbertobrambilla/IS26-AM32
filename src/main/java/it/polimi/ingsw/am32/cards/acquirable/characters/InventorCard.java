package it.polimi.ingsw.am32.cards.acquirable.characters;

import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.enums.CharacterType;
import it.polimi.ingsw.am32.enums.InventionType;

import java.util.Objects;

public class InventorCard extends CharacterCard {
    private final InventionType invention;

    public InventorCard(String id, int era, int basePrestige, int requiredPlayers, InventionType invention) {
        super(id, era, basePrestige, requiredPlayers);

        Objects.requireNonNull(invention, "invention cannot be null");

        this.invention = invention;
    }

    @Override
    public CharacterType getCharacterType() {
        return CharacterType.INVENTOR;
    }

    @Override
    public void onAcquire(Player player){
        Objects.requireNonNull(player, "player cannot be null");

        player.addInventor();
        player.addInvention(this.invention);
    }

    @Override
    public void onGameEnd(Player player) {
        Objects.requireNonNull(player, "player cannot be null");

        player.scoreInventor();
    }

    @Override
    public void notifyBuilding(BuildingCard building, Player player) {
        // specific building effect method is invoked
        building.onInventorAdded(player);
    }
}
