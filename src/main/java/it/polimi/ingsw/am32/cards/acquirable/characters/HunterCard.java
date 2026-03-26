package it.polimi.ingsw.am32.cards.acquirable.characters;

import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.enums.CharacterType;

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

        player.addHunter();

        // grant 1 food for each hunter currently in the tribe if this hunter has meat
        if (this.hasMeat) {
            player.addFood(player.getHunterCount());
        }
    }
}
