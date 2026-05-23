package it.polimi.ingsw.am32.model;

import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import it.polimi.ingsw.am32.model.enums.GameErrorReason;
import it.polimi.ingsw.am32.model.enums.InventionType;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import it.polimi.ingsw.am32.model.exceptions.InvalidGameActionException;

import java.util.*;

/**
 * Represents a player in the game.
 */
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

    private boolean shamanicImmunity;
    private boolean doubleShamanicBonus;
    private boolean isConnected;

    private final List<CharacterCard> characterList;
    private final List<BuildingCard> buildingList;

    /**
     * Initializes the player with the given nickname.
     *
     * @param nickname the nickname of the player
     */
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

    /**
     * Compares this player with another player based on prestige and food.
     *
     * @param other the other player to compare to
     * @return the comparison result
     */
    @Override
    public int compareTo(Player other) {
        Objects.requireNonNull(other, "other player cannot be null");

        // prestige is compared first
        int prestigeComparison = Integer.compare(this.prestige, other.prestige);
        if (prestigeComparison != 0) {
            return prestigeComparison;
        }

        // food is used as a tie-breaker
        return Integer.compare(this.food, other.food);
    }

    /**
     * Sets the player's totem color.
     *
     * @param totem the totem color
     */
    public void setTotem(TotemColor totem) {
        Objects.requireNonNull(totem, "totem cannot be null");

        if (this.totem != null) {
            throw new IllegalArgumentException("totem already set");
        }
        this.totem = totem;
    }

    /**
     * Gets the nickname of the player.
     *
     * @return the player nickname
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Gets the totem color of the player.
     *
     * @return the player totem
     */
    public TotemColor getTotem() {
        return this.totem;
    }

    /**
     * Gets the current amount of food.
     *
     * @return the food amount
     */
    public int getFood() {
        return this.food;
    }

    /**
     * Gets the current amount of prestige.
     *
     * @return the prestige amount
     */
    public int getPrestige() {
        return this.prestige;
    }

    /**
     * Gets the current number of shaman stars.
     *
     * @return the shaman stars amount
     */
    public int getShamanStars() {
        return this.shamanStars;
    }

    /**
     * Checks if the player is currently connected.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return this.isConnected;
    }

    /**
     * Gets the list of buildings acquired by the player.
     *
     * @return the list of buildings
     */
    public List<BuildingCard> getBuildingList() {
        return this.buildingList;
    }

    /**
     * Gets the list of characters acquired by the player.
     *
     * @return the list of characters
     */
    public List<CharacterCard> getCharacterList() {
        return this.characterList;
    }

    /**
     * Adds the specified amount of food to the player.
     *
     * @param amount the amount of food to add
     */
    public void addFood(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        this.food += amount;
    }

    /**
     * Removes the specified amount of food from the player.
     *
     * @param amount the amount of food to remove
     */
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

    /**
     * Checks if the player has at least the specified amount of food.
     *
     * @param amount the amount of food to check
     * @return true if the player has enough food, false otherwise
     */
    public boolean hasEnoughFood(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        return this.food >= amount;
    }

    /**
     * Adds the specified amount of prestige to the player.
     *
     * @param amount the amount of prestige to add
     */
    public void addPrestige(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        this.prestige += amount;
    }

    /**
     * Removes the specified amount of prestige from the player.
     *
     * @param amount the amount of prestige to remove
     */
    public void removePrestige(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        this.prestige -= amount;
    }

    /**
     * Sets the player's connection status.
     *
     * @param status the connection status
     */
    public void setConnected(boolean status) {
        this.isConnected = status;
    }

    /**
     * Acquires a building card and applies its effects.
     *
     * @param card the building card to acquire
     * @param gameState the current state of the game
     */
    public void acquireBuilding(BuildingCard card, GameState gameState) {
        Objects.requireNonNull(card, "card cannot be null");
        Objects.requireNonNull(gameState, "game state cannot be null");

        this.buildingList.add(card);
        // building acquisition effects are applied through delegation to preserve encapsulation
        card.applyAcquireEffect(this, gameState);
    }

    /**
     * Checks if the player can afford an additional building card.
     *
     * @param alreadySelected the list of buildings already selected
     * @param additionalBuilding the extra building to evaluate
     * @return true if the player has enough food, false otherwise
     */
    public boolean canAffordAdditionalBuilding(List<BuildingCard> alreadySelected, BuildingCard additionalBuilding) {
        int totalCost = 0;
        for (BuildingCard b : alreadySelected) {
            totalCost += b.getDiscountedCost(this.buildingDiscount);
        }
        int additionalCost = additionalBuilding.getDiscountedCost(this.buildingDiscount);

        return (totalCost + additionalCost) <= this.food;
    }

    /**
     * Acquires a character card and applies its effects.
     *
     * @param card the character card to acquire
     */
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

    /**
     * Processes the payment for the selected buildings.
     *
     * @param buildings the list of buildings to pay for
     */
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

    /**
     * Triggers effects when the player is placed on the turn order tile.
     *
     * @param gainedFood true if food was gained from the placement, false otherwise
     */
    public void onPlacedOnTurnOrderTile(boolean gainedFood) {
        // totem placement event is dispatched to all active building effects
        for (BuildingCard building : this.buildingList) {
            building.applyTurnOrderPlaced(this, gainedFood);
        }
    }

    /**
     * Triggers round end effects for all owned buildings.
     *
     * @param gameState the current state of the game
     */
    public void triggerBuildingEffectsOnRoundEnd(GameState gameState) {
        Objects.requireNonNull(gameState, "game state cannot be null");

        for (BuildingCard building : this.buildingList) {
            building.applyRoundEnd(this, gameState);
        }
    }

    /**
     * Triggers cave paintings effects for all owned buildings.
     *
     * @param gameState the current state of the game
     */
    public void triggerCavePaintingsEffects(GameState gameState) {
        Objects.requireNonNull(gameState, "game state cannot be null");

        // cave paintings event is dispatched to all active building effects
        for (BuildingCard card : this.buildingList) {
            card.applyCavePaintingsEvent(this, gameState);
        }
    }

    /**
     * Triggers hunt effects for all owned buildings.
     *
     * @param gameState the current state of the game
     */
    public void triggerHuntEffects(GameState gameState) {
        Objects.requireNonNull(gameState, "game state cannot be null");

        // hunt event is dispatched to all active building effects
        for (BuildingCard card : this.buildingList) {
            card.applyHuntEvent(this, gameState);
        }
    }

    /**
     * Triggers shamanic ritual effects for all owned buildings.
     *
     * @param gameState the current state of the game
     */
    public void triggerShamanicRitualEffects(GameState gameState) {
        Objects.requireNonNull(gameState, "game state cannot be null");

        // shamanic ritual event is dispatched to all active building effects
        for (BuildingCard card : this.buildingList) {
            card.applyShamanicRitualEvent(this, gameState);
        }
    }

    /**
     * Triggers sustenance effects for all owned buildings.
     *
     * @param gameState the current state of the game
     */
    public void triggerSustenanceEffects(GameState gameState) {
        Objects.requireNonNull(gameState, "game state cannot be null");

        // sustenance event is dispatched to all active building effects
        for (BuildingCard card : this.buildingList) {
            card.applySustenanceEvent(this, gameState);
        }
    }

    /**
     * Applies the outcome of a cave paintings event.
     *
     * @param prestigeBonus the prestige bonus per artist
     * @param threshold the required number of artists
     * @param penalty the penalty if the threshold is not met
     */
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

    /**
     * Applies the outcome of a hunt event.
     *
     * @param foodPerHunter the food bonus per hunter
     * @param prestigePerHunter the prestige bonus per hunter
     */
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

    /**
     * Applies the outcome of a shamanic ritual event.
     *
     * @param isWinner true if the player won the ritual
     * @param isLoser true if the player lost the ritual
     * @param prestigeBonus the prestige bonus for winning
     * @param prestigePenalty the prestige penalty for losing
     */
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

    /**
     * Applies the outcome of a sustenance event.
     *
     * @param grossCostPerCharacter the food cost per character
     * @param prestigePenalty the prestige penalty for each unfed character
     */
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
            int unfedCharacters = grossCostPerCharacter == 0 ? 0 : (netCost - this.food) / grossCostPerCharacter;
            this.removeFood(this.food);
            this.removePrestige(unfedCharacters * prestigePenalty);
        }
    }

    /**
     * Processes turn order modifiers for the player.
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

    /**
     * Increments the count of artist characters.
     */
    public void addArtist() {
        int current = this.characterCounts.get(CharacterType.ARTIST);
        this.characterCounts.put(CharacterType.ARTIST, current + 1);
    }

    /**
     * Increments the count of builder characters.
     */
    public void addBuilder() {
        int current = this.characterCounts.get(CharacterType.BUILDER);
        this.characterCounts.put(CharacterType.BUILDER, current + 1);
    }

    /**
     * Increments the count of gatherer characters.
     */
    public void addGatherer() {
        int current = this.characterCounts.get(CharacterType.GATHERER);
        this.characterCounts.put(CharacterType.GATHERER, current + 1);
    }

    /**
     * Increments the count of hunter characters.
     *
     * @param hasMeat true if the hunter provides meat, false otherwise
     */
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

    /**
     * Increments the count of inventor characters.
     */
    public void addInventor() {
        int current = this.characterCounts.get(CharacterType.INVENTOR);
        this.characterCounts.put(CharacterType.INVENTOR, current + 1);
    }

    /**
     * Increments the count of shaman characters.
     */
    public void addShaman() {
        int current = this.characterCounts.get(CharacterType.SHAMAN);
        this.characterCounts.put(CharacterType.SHAMAN, current + 1);
    }

    /**
     * Increases the building discount by the given amount.
     *
     * @param amount the discount amount
     */
    public void addBuildingDiscount(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("discount cannot be negative");
        }
        this.buildingDiscount += amount;
    }

    /**
     * Increases the total base prestige provided by builders.
     *
     * @param prestige the prestige amount
     */
    public void addBuilderBasePrestige(int prestige) {
        if (prestige < 0) {
            throw new IllegalArgumentException("prestige cannot be negative");
        }
        this.buildersTotalBasePrestige += prestige;
    }

    /**
     * Increases the sustenance discount by the given amount.
     *
     * @param amount the discount amount
     */
    public void addSustenanceDiscount(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("discount cannot be negative");
        }
        this.sustenanceDiscount += amount;
    }

    /**
     * Adds an invention to the player's collection.
     *
     * @param invention the invention type
     */
    public void addInvention(InventionType invention) {
        Objects.requireNonNull(invention, "invention cannot be null");
        this.inventionsCounts.put(invention, this.inventionsCounts.getOrDefault(invention, 0) + 1);
    }

    /**
     * Increases the shaman stars by the given amount.
     *
     * @param amount the star amount
     */
    public void addShamanStars(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("stars cannot be negative");
        }
        this.shamanStars += amount;
    }

    /**
     * Sets the shamanic immunity status.
     *
     * @param status the immunity status
     */
    public void setShamanicImmunity(boolean status) {
        this.shamanicImmunity = status;
    }

    /**
     * Sets the double shamanic bonus status.
     *
     * @param status the bonus status
     */
    public void setDoubleShamanicBonus(boolean status) {
        this.doubleShamanicBonus = status;
    }

    /**
     * Grants food for a newly completed character set.
     *
     * @param foodAmount the amount of food to grant
     */
    public void grantFoodForNewSet(int foodAmount) {
        if (foodAmount < 0) {
            throw new IllegalArgumentException("food amount cannot be negative");
        }
        // extra food is added for the newly completed set
        this.addFood(foodAmount);
    }

    /**
     * Grants food for a newly completed inventor pair.
     *
     * @param foodAmount the amount of food to grant
     */
    public void grantFoodForNewInventorPair(int foodAmount) {
        if (foodAmount < 0) {
            throw new IllegalArgumentException("food amount cannot be negative");
        }
        // extra food is added for the newly completed inventor pair
        this.addFood(foodAmount);
    }

    /**
     * Adds a sustenance discount based on the number of a specific character type.
     *
     * @param type the character type
     * @param discountPerCharacter the discount per character
     */
    public void addSustenanceDiscountPerCharacterType(CharacterType type, int discountPerCharacter) {
        Objects.requireNonNull(type, "type cannot be null");
        if (discountPerCharacter < 0) {
            throw new IllegalArgumentException("discountPerCharacter cannot be negative");
        }

        int count = this.getCharacterCountByType(type);
        this.addSustenanceDiscount(count * discountPerCharacter);
    }

    /**
     * Adds resources based on the number of a specific character type.
     *
     * @param type the character type
     * @param foodAmount the food amount per character
     * @param prestigeAmount the prestige amount per character
     */
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

    /**
     * Computes the final prestige for the player at the end of the game.
     *
     * @param gameState the current state of the game
     */
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

    /**
     * Scores prestige for an artist pair.
     *
     * @param prestigePerPair the prestige points per completed pair
     */
    public void scoreArtist(int prestigePerPair) {
        this.scoredArtistsCounter++;

        // prestige is added for each completed pair
        if (this.scoredArtistsCounter % 2 == 0) {
            this.addPrestige(prestigePerPair);
        }
    }

    /**
     * Scores prestige based on unique inventions collected.
     */
    public void scoreInventor() {
        // prestige is added based on the total number of unique inventions collected
        this.addPrestige(this.inventionsCounts.size());
    }

    /**
     * Adds endgame prestige for each completed set of characters.
     *
     * @param pointsPerSet the prestige points per set
     */
    public void addEndGamePrestigePerSet(int pointsPerSet) {
        if (pointsPerSet < 0) {
            throw new IllegalArgumentException("pointsPerSet cannot be negative");
        }

        int sets = this.calculateCompletedSets();
        this.addPrestige(sets * pointsPerSet);
    }

    /**
     * Adds endgame prestige based on the count of a specific character type.
     *
     * @param type the character type
     * @param pointsPerCharacter the prestige points per character
     */
    public void addEndGamePrestigePerCharacterType(CharacterType type, int pointsPerCharacter) {
        Objects.requireNonNull(type, "type cannot be null");
        if (pointsPerCharacter < 0) {
            throw new IllegalArgumentException("pointsPerCharacter cannot be negative");
        }

        int count = this.getCharacterCountByType(type);
        this.addPrestige(count * pointsPerCharacter);
    }

    /**
     * Adds an endgame prestige multiplier based on the total base prestige of builders.
     *
     * @param multiplier the multiplier value
     */
    public void addEndGameBuilderPrestigeMultiplier(int multiplier) {
        if (multiplier < 1) {
            throw new IllegalArgumentException("multiplier cannot be less than 1");
        }

        // extra prestige is calculated using the tracked builders base prestige
        int extraPrestige = this.buildersTotalBasePrestige * (multiplier - 1);
        this.addPrestige(extraPrestige);
    }

    /**
     * Checks if this player is equal to another object based on the nickname.
     *
     * @param o the object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return nickname.equals(player.nickname);
    }

    /**
     * Generates a hash code for the player based on the nickname.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(nickname);
    }

    /**
     * Gets the count of characters of the specified type.
     *
     * @param type the character type
     * @return the number of characters
     */
    private int getCharacterCountByType(CharacterType type) {
        return this.characterCounts.get(type);
    }

    /**
     * Calculates the number of completed character sets.
     *
     * @return the number of completed sets
     */
    private int calculateCompletedSets() {
        int min1 = Math.min(this.characterCounts.get(CharacterType.ARTIST),
                this.characterCounts.get(CharacterType.BUILDER));
        int min2 = Math.min(this.characterCounts.get(CharacterType.GATHERER),
                this.characterCounts.get(CharacterType.HUNTER));
        int min3 = Math.min(this.characterCounts.get(CharacterType.INVENTOR),
                this.characterCounts.get(CharacterType.SHAMAN));

        return Math.min(min1, Math.min(min2, min3));
    }

    /**
     * Calculates the number of completed inventor pairs.
     *
     * @return the number of inventor pairs
     */
    private int calculateInventorPairs() {
        int totalPairs = 0;
        // pairs for each invention type are summed
        for (int count : this.inventionsCounts.values()) {
            totalPairs += (count / 2);
        }
        return totalPairs;
    }
}
