package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.endgame;

import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCardEffect;
import it.polimi.ingsw.am32.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.cards.events.EventCard;
import it.polimi.ingsw.am32.enums.CharacterType;
import it.polimi.ingsw.am32.tiles.OfferTile;

import java.util.Objects;

public class PrestigePerCharacterEffect implements BuildingCardEffect {
    private CharacterType targetType;
    private int pointsPerCharacter;

    public PrestigePerCharacterEffect(CharacterType targetType, int pointsPerCharacter) {
        Objects.requireNonNull(targetType, "targetType cannot be null");
        if(pointsPerCharacter < 0) {
            throw new IllegalArgumentException("pointsPerCharacter cannot be negative");
        }

        this.targetType = targetType;
        this.pointsPerCharacter = pointsPerCharacter;
    }

    @Override
    public void onGameEnd(Player player, GameState gameState) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        int count = 0;
        // switch used to count specific character types dynamically
        switch (this.targetType) {
            case ARTIST: count = player.getArtistCount(); break;
            case BUILDER: count = player.getBuilderCount(); break;
            case GATHERER: count = player.getGathererCount(); break;
            case HUNTER: count = player.getHunterCount(); break;
            case INVENTOR: count = player.getInventorCount(); break;
            case SHAMAN: count = player.getShamanCount(); break;
        }
        player.addPrestige(count * this.pointsPerCharacter);
    }

    @Override public void onAcquire(Player player, GameState gameState) {}
    @Override public void onEvent(EventCard event, Player player, GameState gameState) {}
    @Override public void onRoundEnd(Player player, GameState gameState) {}
    @Override public void onTotemPlaced(OfferTile offerTile, Player player, GameState gameState) {}
    @Override public void onCharacterAdded(Player player, CharacterCard newCard) {}
}
