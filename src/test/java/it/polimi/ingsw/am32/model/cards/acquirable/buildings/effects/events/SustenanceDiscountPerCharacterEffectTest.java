package it.polimi.ingsw.am32.model.cards.acquirable.buildings.effects.events;

import it.polimi.ingsw.am32.model.Deck;
import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.GathererCard;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.HunterCard;
import it.polimi.ingsw.am32.model.enums.CharacterType;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class SustenanceDiscountPerCharacterEffectTest {

    private Player player;
    private GameState gameState;
    private SustenanceDiscountPerCharacterEffect effect;
    private final CharacterType TARGET_CHARACTER = CharacterType.GATHERER;
    private final int DISCOUNT_PER_CHARACTER = 2;

    @BeforeEach
    void setUp() {
        // real player instance is created
        player = new Player("Alice", TotemColor.RED);

        // real game state is instantiated
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 2, new ArrayList<>(), new ArrayList<>());
        gameState = new GameState(2, 2, new ArrayList<>(), dummyTurnOrderTile, new Deck<>(), new HashMap<>());

        // sustenance discount per character effect is instantiated with valid parameters
        effect = new SustenanceDiscountPerCharacterEffect(TARGET_CHARACTER, DISCOUNT_PER_CHARACTER);
    }

    @Test
    void constructorShouldThrowExceptionIfTargetCharacterIsNull() {
        // null pointer exception is expected when target character is null
        assertThrows(NullPointerException.class, () -> new SustenanceDiscountPerCharacterEffect(null, DISCOUNT_PER_CHARACTER));
    }

    @Test
    void constructorShouldThrowExceptionIfDiscountIsNegative() {
        // illegal argument exception is expected when discount per character is negative
        assertThrows(IllegalArgumentException.class, () -> new SustenanceDiscountPerCharacterEffect(TARGET_CHARACTER, -1));
    }

    @Test
    void onAcquireShouldThrowExceptionIfPlayerIsNull() {
        // null pointer exception is expected when player is null
        assertThrows(NullPointerException.class, () -> effect.onAcquire(null, gameState));
    }

    @Test
    void onAcquireShouldThrowExceptionIfGameStateIsNull() {
        // null pointer exception is expected when game state is null
        assertThrows(NullPointerException.class, () -> effect.onAcquire(player, null));
    }

    @Test
    void onCharacterAddedShouldThrowExceptionIfPlayerIsNull() {
        // null pointer exception is expected when player is null
        GathererCard validCard = new GathererCard("gat_01", 1, 0, 0, 1);
        assertThrows(NullPointerException.class, () -> effect.onCharacterAdded(null, validCard));
    }

    @Test
    void onCharacterAddedShouldThrowExceptionIfNewCardIsNull() {
        // null pointer exception is expected when new card is null
        assertThrows(NullPointerException.class, () -> effect.onCharacterAdded(player, null));
    }

    @Test
    void onAcquireShouldCoverAllCharacterTypes() {
        // one character of each type is added to the player
        player.addArtist();
        player.addHunter();
        player.addBuilder();
        player.addGatherer();
        player.addInventor();
        player.addShaman();

        // artist effect is tested to verify correct discount increment
        SustenanceDiscountPerCharacterEffect artistEffect = new SustenanceDiscountPerCharacterEffect(CharacterType.ARTIST, DISCOUNT_PER_CHARACTER);
        artistEffect.onAcquire(player, gameState);
        assertEquals(2, player.getSustenanceDiscount());

        // inventor effect is tested to verify correct discount increment
        SustenanceDiscountPerCharacterEffect inventorEffect = new SustenanceDiscountPerCharacterEffect(CharacterType.INVENTOR, DISCOUNT_PER_CHARACTER);
        inventorEffect.onAcquire(player, gameState);
        assertEquals(4, player.getSustenanceDiscount());

        // gatherer effect is tested to verify correct discount increment
        SustenanceDiscountPerCharacterEffect gathererEffect = new SustenanceDiscountPerCharacterEffect(CharacterType.GATHERER, DISCOUNT_PER_CHARACTER);
        gathererEffect.onAcquire(player, gameState);
        assertEquals(6, player.getSustenanceDiscount());

        // builder effect is tested to verify discount is ignored
        SustenanceDiscountPerCharacterEffect builderEffect = new SustenanceDiscountPerCharacterEffect(CharacterType.BUILDER, DISCOUNT_PER_CHARACTER);
        builderEffect.onAcquire(player, gameState);
        assertEquals(6, player.getSustenanceDiscount());

        // hunter effect is tested to verify discount is ignored
        SustenanceDiscountPerCharacterEffect hunterEffect = new SustenanceDiscountPerCharacterEffect(CharacterType.HUNTER, DISCOUNT_PER_CHARACTER);
        hunterEffect.onAcquire(player, gameState);
        assertEquals(6, player.getSustenanceDiscount());

        // shaman effect is tested to verify discount is ignored
        SustenanceDiscountPerCharacterEffect shamanEffect = new SustenanceDiscountPerCharacterEffect(CharacterType.SHAMAN, DISCOUNT_PER_CHARACTER);
        shamanEffect.onAcquire(player, gameState);
        assertEquals(6, player.getSustenanceDiscount());
    }

    @Test
    void onCharacterAddedShouldIncreaseDiscountIfCardMatchesTarget() {
        // a matching character card is created
        GathererCard matchingCard = new GathererCard("gat_02", 1, 0, 0, 3);

        // effect is triggered by adding the character
        effect.onCharacterAdded(player, matchingCard);

        // correct discount is added dynamically when a matching character card is acquired
        assertEquals(DISCOUNT_PER_CHARACTER, player.getSustenanceDiscount());
    }

    @Test
    void onCharacterAddedShouldNotIncreaseDiscountIfCardDoesNotMatch() {
        // a non-matching character card is created
        HunterCard nonMatchingCard = new HunterCard("hnt_02", 1, 0, 0, false);

        // effect is triggered by adding the character
        effect.onCharacterAdded(player, nonMatchingCard);

        // zero discount is added dynamically when the acquired character card does not match the target
        assertEquals(0, player.getSustenanceDiscount());
    }

    @Test
    void emptyMethodsShouldNotAlterPlayerState() {
        // initial state is recorded
        int initialDiscount = player.getSustenanceDiscount();
        int initialFood = player.getFood();
        int initialPrestige = player.getPrestige();

        // empty interface methods are invoked
        effect.onEvent(null, player, gameState);
        effect.onRoundEnd(player, gameState);
        effect.onGameEnd(player, gameState);
        effect.onTotemPlaced(null, player, gameState);

        // player state is verified to remain completely unchanged
        assertEquals(initialDiscount, player.getSustenanceDiscount());
        assertEquals(initialFood, player.getFood());
        assertEquals(initialPrestige, player.getPrestige());
    }
}
