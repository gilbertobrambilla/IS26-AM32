package it.polimi.ingsw.am32;

import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.enums.*;
import it.polimi.ingsw.am32.tiles.OfferTile;

import java.util.*;

public class Player {
    private final String nickname;
    private final TotemColor totem;
    private int food;
    private int prestige;
    private int artistCount;
    private int builderCount;
    private int buildingDiscount;
    private int gathererCount;
    private int sustenanceDiscount;
    private int hunterCount;
    private int inventorCount;
    private final Set<InventionType> inventions;
    private int shamanCount;
    private int shamanStars;
    private boolean shamanicImmunity;
    private boolean doubleShamanicBonus;
    private final List<CharacterCard> characterList;
    private final List<BuildingCard> buildingList;
    private boolean isConnected;

    public Player(String nickname, TotemColor totem) {
        Objects.requireNonNull(nickname, "nickname cannot be null");
        Objects.requireNonNull(totem, "totem cannot be null");

        this.nickname = nickname;
        this.totem = totem;
        this.food = 0;
        this.prestige = 0;
        this.artistCount = 0;
        this.builderCount = 0;
        this.buildingDiscount = 0;
        this.gathererCount = 0;
        this.sustenanceDiscount = 0;
        this.hunterCount = 0;
        this.inventorCount = 0;
        this.inventions = new HashSet<>();
        this.shamanCount = 0;
        this.shamanStars = 0;
        this.characterList = new ArrayList<>();
        this.buildingList = new ArrayList<>();
        this.isConnected = true;
    }

    public String getNickname() {
        return nickname;
    }

    public TotemColor getTotem() {
        return totem;
    }

    public int getFood() {
        return food;
    }

    public void addFood(int amount) throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }

        this.food += amount;
    }

    public void removeFood(int amount) throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }

        this.food -= amount;
    }

    public boolean hasEnoughFood(int amount) throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }

        return this.food >= amount;
    }

    public int getPrestige() {
        return prestige;
    }

    public void addPrestige(int amount) throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }

        this.prestige += amount;
    }

    public void removePrestige(int amount) throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }

        this.prestige -= amount;
    }

    public int getArtistCount() {
        return artistCount;
    }

    public void addArtist() {
        this.artistCount++;
    }

    public int getBuilderCount() {
        return builderCount;
    }

    public void addBuilder() {
        this.builderCount++;
    }

    public int getBuildingDiscount() {
        return buildingDiscount;
    }

    public void addBuildingDiscount(int amount) throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }

        this.buildingDiscount += amount;
    }

    public int getGathererCount() {
        return gathererCount;
    }

    public void addGatherer() {
        this.gathererCount++;
    }

    public int getSustenanceDiscount() {
        return sustenanceDiscount;
    }

    public void addSustenanceDiscount(int amount) throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }

        this.sustenanceDiscount += amount;
    }

    public int getHunterCount() {
        return hunterCount;
    }

    public void addHunter() {
        this.hunterCount++;
    }

    public int getInventorCount() {
        return inventorCount;
    }

    public void addInventor() {
        this.inventorCount++;
    }

    public int getInventionCount() {
        return inventions.size();
    }

    public void addInvention(InventionType invention) throws IllegalArgumentException {
        Objects.requireNonNull(invention, "invention cannot be null");

        this.inventions.add(invention);
    }

    public int getShamanCount() {
        return shamanCount;
    }

    public void addShaman() {
        this.shamanCount++;
    }

    public int getShamanStars() {
        return shamanStars;
    }

    public void addShamanStars(int amount) throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }

        this.shamanStars += amount;
    }

    public boolean hasShamanicImmunity() {
        return shamanicImmunity;
    }

    public void setShamanicImmunity(boolean status) {
        this.shamanicImmunity = status;
    }

    public boolean hasDoubleShamanicBonus() {
        return doubleShamanicBonus;
    }

    public void setDoubleShamanicBonus(boolean status) {
        this.doubleShamanicBonus = status;
    }

    public int getCompletedSetsCount() {
        // a complete set requires one of each character type
        int min1 = Math.min(artistCount, builderCount);
        int min2 = Math.min(gathererCount, hunterCount);
        int min3 = Math.min(inventorCount, shamanCount);
        return Math.min(min1, Math.min(min2, min3));
    }

    public int getBuildersTotalPrestige() {
        int total = 0;
        for (CharacterCard card : characterList) {
            if (card.getCharacterType() == CharacterType.BUILDER) {
                total += card.getBasePrestige();
            }
        }
        return total;
    }

//    public List<CharacterCard> getCharacterList() {
//        return new ArrayList<>(characterList);
//    }

    public void addCharacter(CharacterCard card) {
        Objects.requireNonNull(card, "card cannot be null");
        this.characterList.add(card);
    }

    public int getCharacterCount() {
        return characterList.size();
    }

//    public List<BuildingCard> getBuildingList() {
//        return new ArrayList<>(buildingList);
//    }

    public void triggerBuildingEffectsOnTotemPlaced(OfferTile targetTile, GameState gameState) {
        Objects.requireNonNull(targetTile, "targetTile cannot be null");
        Objects.requireNonNull(gameState, "gameState cannot be null");

        for (BuildingCard building : this.buildingList) {
            building.getEffect().onTotemPlaced(targetTile, this, gameState);
        }
    }

    public void triggerBuildingEffectsOnRoundEnd(GameState gameState) {
        Objects.requireNonNull(gameState, "gameState cannot be null");

        for (BuildingCard building : this.buildingList) {
            building.getEffect().onRoundEnd(this, gameState);
        }
    }

    public void triggerBuildingEffectsOnGameEnd(GameState gameState) {
        Objects.requireNonNull(gameState, "gameState cannot be null");

        for (BuildingCard building : this.buildingList) {
            building.getEffect().onGameEnd(this, gameState);
        }
    }



    public void addBuilding(BuildingCard card) {
        Objects.requireNonNull(card, "card cannot be null");
        this.buildingList.add(card);
    }

    public void setConnected(boolean status) {
        this.isConnected = status;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
