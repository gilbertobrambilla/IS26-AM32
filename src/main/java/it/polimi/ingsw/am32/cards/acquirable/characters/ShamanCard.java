package it.polimi.ingsw.am32.cards.acquirable.characters;

import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.enums.CharacterType;

import java.util.Objects;

public class ShamanCard extends CharacterCard {
    private final int shamanStars;

    public ShamanCard(String id, int era, int basePrestige, int requiredPlayers, int shamanStars) {
        super(id, era, basePrestige, requiredPlayers);

        if (shamanStars < 1 || shamanStars > 3) {
            throw new IllegalArgumentException("shamanStars must be between 1 and 3");
        }

        this.shamanStars = shamanStars;
    }

    @Override
    public CharacterType getCharacterType() {
        return CharacterType.SHAMAN;
    }

    @Override
    public void onAcquire(Player player){
        Objects.requireNonNull(player, "player cannot be null");

        player.addShaman();
        player.addShamanStars(this.shamanStars);
    }

    @Override
    public void notifyBuilding(BuildingCard building, Player player) {
        // specific building effect method is invoked
        building.onShamanAdded(player);
    }
}
