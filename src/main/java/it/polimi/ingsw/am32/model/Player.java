package it.polimi.ingsw.am32.model;

import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import it.polimi.ingsw.am32.model.enums.GameErrorReason;
import it.polimi.ingsw.am32.model.enums.InventionType;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import it.polimi.ingsw.am32.model.exceptions.InvalidGameActionException;

import java.util.*;

public class Player implements Comparable<Player> {
    private final String nickname;
    private TotemColor totem;
    private int food;
    private int prestige;

    // counters are kept private to ensure strict encapsulation
    private final Map<CharacterType, Integer> characterCounts;
    private int scoredArtistsCounter;
    private int buildingDiscount;
    private int buildersTotalBasePrestige;
    private int sustenanceDiscount;
    private final Map<InventionType, Integer> inventionsCounts;
    private int shamanStars;

    // status flags
    private boolean shamanicImmunity;
    private boolean doubleShamanicBonus;
    private boolean isConnected;

    private final List<CharacterCard> characterList;
    private final List<BuildingCard> buildingList;

    public Player(String nickname) {
        Objects.requireNonNull(nickname, "nickname cannot be null");

        this.nickname = nickname;
        this.totem = null;
        this.food = 0;
        this.prestige = 0;

        this.characterCounts = new EnumMap<>(CharacterType.class);
        for (CharacterType type : CharacterType.values()) {
            this.characterCounts.put(type, 0);
        }

        this.scoredArtistsCounter = 0;
        this.buildingDiscount = 0;
        this.buildersTotalBasePrestige = 0;
        this.sustenanceDiscount = 0;
        this.inventionsCounts = new HashMap<>();
        this.shamanStars = 0;

        this.shamanicImmunity = false;
        this.doubleShamanicBonus = false;
        this.isConnected = true;

        this.characterList = new ArrayList<>();
        this.buildingList = new ArrayList<>();
    }

    public void setTotem(TotemColor totem) {
        Objects.requireNonNull(totem, "totem cannot be null");

        if (this.totem != null) {
            throw new IllegalArgumentException("totem already set");
        }
        this.totem = totem;
    }

    // comparable implementation

    @Override
    public int compareTo(Player other) {
        Objects.requireNonNull(other, "other player cannot be null");

        // prestige is compared first
        int prestigeComparison = Integer.compare(this.prestige, other.prestige);
        if (prestigeComparison != 0) {
            return prestigeComparison;
        }

        // food is used as tie-breaker
        return Integer.compare(this.food, other.food);
    }



    // primitive getters (state observation)

    public String getNickname() {
        return this.nickname;
    }

    public TotemColor getTotem() {
        return this.totem;
    }

    public int getFood() {
        return this.food;
    }

    public int getPrestige() {
        return this.prestige;
    }

    public int getShamanStars() {
        return this.shamanStars;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public List<BuildingCard> getBuildingList() {
        return buildingList;
    }

    public List<CharacterCard> getCharacterList() {
        return characterList;
    }

    // basic resource management

    public void addFood(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        this.food += amount;
    }

    public void removeFood(int amount) {
        if (amount < 0) {
            // exception is thrown if amount is negative
            throw new IllegalArgumentException("amount cannot be negative");
        }
        if (this.food < amount) {
            // exception is thrown if food is not enough
            throw new InvalidGameActionException(
                    GameErrorReason.NOT_ENOUGH_FOOD,
                    "not enough food to perform the removal"
            );
        }
        this.food -= amount;
    }

    public boolean hasEnoughFood(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        return this.food >= amount;
    }

    public void addPrestige(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        this.prestige += amount;
    }

    public void removePrestige(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        this.prestige -= amount;
    }

    public void setConnected(boolean status) {
        this.isConnected = status;
    }



    // character and building transactions

    public void acquireBuilding(BuildingCard card, GameState gameState) {
        Objects.requireNonNull(card, "card cannot be null");
        Objects.requireNonNull(gameState, "game state cannot be null");

        this.buildingList.add(card);
        // building acquisition effects are applied through delegation to preserve encapsulation
        card.applyAcquireEffect(this, gameState);
    }

    public void acquireCharacter(CharacterCard card) {
        Objects.requireNonNull(card, "card cannot be null");

        // state before acquisition is captured
        int setsBefore = this.calculateCompletedSets();
        int pairsBefore = this.calculateInventorPairs();

        this.characterList.add(card);
        // specific character acquisition effects are resolved via polymorphism
        card.onAcquire(this);

        // state after acquisition is evaluated
        boolean isNewSet = this.calculateCompletedSets() > setsBefore;
        boolean isNewPair = this.calculateInventorPairs() > pairsBefore;

        for (BuildingCard building : this.buildingList) {
            // double dispatch is used to safely notify buildings of the specific character type added
            card.notifyBuilding(building, this);

            if (isNewSet) {
                building.applySetCompleted(this);
            }
            if (isNewPair) {
                building.applyInventorPairCompleted(this);
            }
        }
    }

    public void payForBuildings(List<BuildingCard> buildings) {
        Objects.requireNonNull(buildings, "buildings cannot be null");

        int totalCost = 0;
        for (BuildingCard building : buildings) {
            // discounted cost is calculated internally
            totalCost += building.getDiscountedCost(this.buildingDiscount);
        }

        // food availability is verified
        if (this.food < totalCost) {
            throw new InvalidGameActionException(
                    GameErrorReason.NOT_ENOUGH_FOOD,
                    "not enough food to buy the selected buildings"
            );
        }

        // food is deducted
        this.food -= totalCost;
    }



    // phase triggers

    public void onPlacedOnTurnOrderTile(boolean gainedFood) {
        // totem placement event is dispatched to all active building effects
        for (BuildingCard building : this.buildingList) {
            building.applyTurnOrderPlaced(this, gainedFood);
        }
    }

    public void triggerBuildingEffectsOnRoundEnd(GameState gameState) {
        Objects.requireNonNull(gameState, "game state cannot be null");

        for (BuildingCard building : this.buildingList) {
            building.applyRoundEnd(this, gameState);
        }
    }

    // event triggers

    public void triggerCavePaintingsEffects(GameState gameState) {
        Objects.requireNonNull(gameState, "game state cannot be null");

        // cave paintings event is dispatched to all active building effects
        for (BuildingCard card : this.buildingList) {
            card.applyCavePaintingsEvent(this, gameState);
        }
    }

    public void triggerHuntEffects(GameState gameState) {
        Objects.requireNonNull(gameState, "game state cannot be null");

        // hunt event is dispatched to all active building effects
        for (BuildingCard card : this.buildingList) {
            card.applyHuntEvent(this, gameState);
        }
    }

    public void triggerShamanicRitualEffects(GameState gameState) {
        Objects.requireNonNull(gameState, "game state cannot be null");

        // shamanic ritual event is dispatched to all active building effects
        for (BuildingCard card : this.buildingList) {
            card.applyShamanicRitualEvent(this, gameState);
        }
    }

    public void triggerSustenanceEffects(GameState gameState) {
        Objects.requireNonNull(gameState, "game state cannot be null");

        // sustenance event is dispatched to all active building effects
        for (BuildingCard card : this.buildingList) {
            card.applySustenanceEvent(this, gameState);
        }
    }



    // event reactions

    public void applyCavePaintingsEvent(int prestigeBonus, int threshold, int penalty) {
        if (prestigeBonus < 0 || threshold < 0 || penalty < 0) {
            throw new IllegalArgumentException("event parameters cannot be negative");
        }

        int currentArtists = this.characterCounts.get(CharacterType.ARTIST);

        if (currentArtists >= threshold) {
            this.addPrestige(currentArtists * prestigeBonus);
        } else {
            this.removePrestige(penalty);
        }
    }

    public void applyHuntEvent(int foodPerHunter, int prestigePerHunter) {
        if (foodPerHunter < 0 || prestigePerHunter < 0) {
            throw new IllegalArgumentException("bonus multipliers cannot be negative");
        }

        int currentHunters = this.characterCounts.get(CharacterType.HUNTER);

        if (currentHunters > 0) {
            this.addFood(currentHunters * foodPerHunter);
            this.addPrestige(currentHunters * prestigePerHunter);
        }
    }

    public void applyShamanicRitualResult(boolean isWinner, boolean isLoser, int prestigeBonus, int prestigePenalty) {
        if (prestigeBonus < 0 || prestigePenalty < 0) {
            throw new IllegalArgumentException("ritual parameters cannot be negative");
        }

        if (isWinner) {
            // status flags are evaluated internally
            int finalBonus = this.doubleShamanicBonus ? (prestigeBonus * 2) : prestigeBonus;
            this.addPrestige(finalBonus);
        }
        if (isLoser && !this.shamanicImmunity) {
            this.removePrestige(prestigePenalty);
        }
    }

    public void applySustenanceEvent(int grossCostPerCharacter, int prestigePenalty) {
        if (grossCostPerCharacter < 0 || prestigePenalty < 0) {
            throw new IllegalArgumentException("sustenance parameters cannot be negative");
        }

        int totalCharacters = this.characterList.size();
        int grossCost = totalCharacters * grossCostPerCharacter;

        // net cost is calculated internally ensuring it never drops below zero
        int netCost = Math.max(0, grossCost - this.sustenanceDiscount);

        if (this.food >= netCost) {
            this.removeFood(netCost);
        } else {
            // unfed characters penalty is applied
            int unfedCharacters = (netCost - this.food) / grossCostPerCharacter;
            this.removeFood(this.food);
            this.removePrestige(unfedCharacters * prestigePenalty);
        }
    }

    /**
     * Turn order modifiers are processed for the player.
     *
     * @param foodMod the food modifier to apply
     * @param prestigeMod the prestige modifier to apply
     */
    public void processTurnOrderModifiers(int foodMod, int prestigeMod) {
        if (foodMod > 0) {
            // positive food modifier is added
            this.addFood(foodMod);
        } else if (foodMod < 0) {
            int cost = Math.abs(foodMod);
            if (this.hasEnoughFood(cost)) {
                // food cost is paid
                this.removeFood(cost);
            } else if (prestigeMod < 0) {
                // prestige penalty is applied if food is not enough
                this.removePrestige(Math.abs(prestigeMod));
            }
        }

        if (prestigeMod > 0) {
            // positive prestige modifier is added
            this.addPrestige(prestigeMod);
        }
    }



    // state modifiers

    public void addArtist() {
        int current = this.characterCounts.get(CharacterType.ARTIST);
        this.characterCounts.put(CharacterType.ARTIST, current + 1);
    }

    public void addBuilder() {
        int current = this.characterCounts.get(CharacterType.BUILDER);
        this.characterCounts.put(CharacterType.BUILDER, current + 1);
    }

    public void addGatherer() {
        int current = this.characterCounts.get(CharacterType.GATHERER);
        this.characterCounts.put(CharacterType.GATHERER, current + 1);
    }

    public void addHunter(boolean hasMeat) {
        // hunter count is incremented
        int current = this.characterCounts.get(CharacterType.HUNTER);
        int newCount = current + 1;
        this.characterCounts.put(CharacterType.HUNTER, newCount);

        // food is added based on the total number of hunters if the new hunter has meat
        if (hasMeat) {
            this.addFood(newCount);
        }
    }

    public void addInventor() {
        int current = this.characterCounts.get(CharacterType.INVENTOR);
        this.characterCounts.put(CharacterType.INVENTOR, current + 1);
    }

    public void addShaman() {
        int current = this.characterCounts.get(CharacterType.SHAMAN);
        this.characterCounts.put(CharacterType.SHAMAN, current + 1);
    }

    public void addBuildingDiscount(int amount) {
        if (amount < 0) throw new IllegalArgumentException("discount cannot be negative");
        this.buildingDiscount += amount;
    }

    public void addBuilderBasePrestige(int prestige) {
        if (prestige < 0) {
            throw new IllegalArgumentException("prestige cannot be negative");
        }

        this.buildersTotalBasePrestige += prestige;
    }

    public void addSustenanceDiscount(int amount) {
        if (amount < 0) throw new IllegalArgumentException("discount cannot be negative");
        this.sustenanceDiscount += amount;
    }

    public void addInvention(InventionType invention) {
        Objects.requireNonNull(invention, "invention cannot be null");
        this.inventionsCounts.put(invention, this.inventionsCounts.getOrDefault(invention, 0) + 1);
    }

    public void addShamanStars(int amount) {
        if (amount < 0) throw new IllegalArgumentException("stars cannot be negative");
        this.shamanStars += amount;
    }

    public void setShamanicImmunity(boolean status) {
        this.shamanicImmunity = status;
    }

    public void setDoubleShamanicBonus(boolean status) {
        this.doubleShamanicBonus = status;
    }

    public void grantFoodForNewSet(int foodAmount) {
        if (foodAmount < 0) {
            throw new IllegalArgumentException("food amount cannot be negative");
        }
        // extra food is added for the newly completed set
        this.addFood(foodAmount);
    }

    public void grantFoodForNewInventorPair(int foodAmount) {
        if (foodAmount < 0) {
            throw new IllegalArgumentException("food amount cannot be negative");
        }
        // extra food is added for the newly completed inventor pair
        this.addFood(foodAmount);
    }

    public void addSustenanceDiscountPerCharacterType(CharacterType type, int discountPerCharacter) {
        Objects.requireNonNull(type, "type cannot be null");
        if (discountPerCharacter < 0) {
            throw new IllegalArgumentException("discountPerCharacter cannot be negative");
        }

        int count = this.getCharacterCountByType(type);
        this.addSustenanceDiscount(count * discountPerCharacter);
    }

    public void addResourcesPerCharacterType(CharacterType type, int foodAmount, int prestigeAmount) {
        Objects.requireNonNull(type, "type cannot be null");
        if (foodAmount < 0) {
            throw new IllegalArgumentException("foodAmount cannot be negative");
        }
        if (prestigeAmount < 0) {
            throw new IllegalArgumentException("prestigeAmount cannot be negative");
        }

        int count = this.getCharacterCountByType(type);
        this.addFood(count * foodAmount);
        this.addPrestige(count * prestigeAmount);
    }



    // endgame scoring

    public void computeFinalPrestige(GameState gameState) {
        Objects.requireNonNull(gameState, "game state cannot be null");

        // building endgame effects and base prestige are triggered
        for (BuildingCard building : this.buildingList) {
            building.onGameEnd(this, gameState);
        }

        // character endgame effects are triggered
        for (CharacterCard card : this.characterList) {
            card.onGameEnd(this);
        }
    }

    public void scoreArtist(int prestigePerPair) {
        this.scoredArtistsCounter++;

        // prestige is added for each completed pair
        if (this.scoredArtistsCounter % 2 == 0) {
            this.addPrestige(prestigePerPair);
        }
    }

    public void scoreInventor() {
        // prestige is added based on the total number of unique inventions collected
        this.addPrestige(this.inventionsCounts.size());
    }

    public void addEndGamePrestigePerSet(int pointsPerSet) {
        if (pointsPerSet < 0) {
            throw new IllegalArgumentException("pointsPerSet cannot be negative");
        }

        int sets = this.calculateCompletedSets();
        this.addPrestige(sets * pointsPerSet);
    }

    public void addEndGamePrestigePerCharacterType(CharacterType type, int pointsPerCharacter) {
        Objects.requireNonNull(type, "type cannot be null");
        if (pointsPerCharacter < 0) {
            throw new IllegalArgumentException("pointsPerCharacter cannot be negative");
        }

        int count = this.getCharacterCountByType(type);
        this.addPrestige(count * pointsPerCharacter);
    }

    public void addEndGameBuilderPrestigeMultiplier(int multiplier) {
        if (multiplier < 1) {
            throw new IllegalArgumentException("multiplier cannot be less than 1");
        }

        // extra prestige is calculated using the tracked builders base prestige
        int extraPrestige = this.buildersTotalBasePrestige * (multiplier - 1);
        this.addPrestige(extraPrestige);
    }



    // utility

    private int getCharacterCountByType(CharacterType type) {
        return this.characterCounts.get(type);
    }

    private int calculateCompletedSets() {
        int min1 = Math.min(this.characterCounts.get(CharacterType.ARTIST),
                            this.characterCounts.get(CharacterType.BUILDER));
        int min2 = Math.min(this.characterCounts.get(CharacterType.GATHERER),
                            this.characterCounts.get(CharacterType.HUNTER));
        int min3 = Math.min(this.characterCounts.get(CharacterType.INVENTOR),
                            this.characterCounts.get(CharacterType.SHAMAN));

        return Math.min(min1, Math.min(min2, min3));
    }

    private int calculateInventorPairs() {
        int totalPairs = 0;
        // pairs for each invention type are summed
        for (int count : this.inventionsCounts.values()) {
            totalPairs += (count / 2);
        }
        return totalPairs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return nickname.equals(player.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname);
    }
}
