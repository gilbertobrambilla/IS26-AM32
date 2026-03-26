package it.polimi.ingsw.am32.cards.acquirable.characters;

import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.enums.CharacterType;

import java.util.Objects;

public class ArtistCard extends CharacterCard {
    private final int prestigePerPair;

    public ArtistCard(String id, int era, int basePrestige, int requiredPlayers, int prestigePerPair) {
        super(id, era, basePrestige, requiredPlayers);

        if (prestigePerPair < 0) {
            throw new IllegalArgumentException("prestigePerPair cannot be negative");
        }

        this.prestigePerPair = prestigePerPair;
    }

    @Override
    public CharacterType getCharacterType() {
        return CharacterType.ARTIST;
    }

    @Override
    public void onAcquire(Player player){
        Objects.requireNonNull(player, "player cannot be null");

        player.addArtist();
    }
}
