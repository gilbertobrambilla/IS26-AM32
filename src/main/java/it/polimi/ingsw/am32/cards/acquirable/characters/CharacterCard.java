package it.polimi.ingsw.am32.cards.acquirable.characters;

import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.CardVisitor;
import it.polimi.ingsw.am32.cards.acquirable.AcquirableCard;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.enums.CharacterType;

import java.util.Objects;

public abstract class CharacterCard extends AcquirableCard {
    private final int requiredPlayers;

    public CharacterCard(String id, int era, int basePrestige, int requiredPlayers) {
        super(id, era, basePrestige);

        if (requiredPlayers < 0) {
            throw new IllegalArgumentException("requiredPlayers cannot be negative");
        }

        this.requiredPlayers = requiredPlayers;
    }

    @Override
    public void accept(CardVisitor visitor) {
        Objects.requireNonNull(visitor, "visitor cannot be null");

        visitor.visit(this);
    }

    public int getRequiredPlayers() {
        return this.requiredPlayers;
    }

    public abstract void notifyBuilding(BuildingCard building, Player player);
    public abstract CharacterType getCharacterType();
    public void onAcquire(Player player){}
    public void onGameEnd(Player player){}
}
