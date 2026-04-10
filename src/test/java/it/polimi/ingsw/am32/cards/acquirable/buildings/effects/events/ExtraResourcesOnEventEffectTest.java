package it.polimi.ingsw.am32.cards.acquirable.buildings.effects.events;

import it.polimi.ingsw.am32.Deck;
import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.events.CavePaintingsEvent;
import it.polimi.ingsw.am32.cards.events.EventCard;
import it.polimi.ingsw.am32.cards.events.HuntEvent;
import it.polimi.ingsw.am32.enums.CharacterType;
import it.polimi.ingsw.am32.enums.EventType;
import it.polimi.ingsw.am32.enums.TotemColor;
import it.polimi.ingsw.am32.tiles.TurnOrderTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ExtraResourcesOnEventEffectTest {

    private Player player;
    private GameState gameState;
    private ExtraResourcesOnEventEffect effect;
    private EventCard targetEventCard;

    private final EventType TARGET_EVENT = EventType.CAVE_PAINTINGS;
    private final CharacterType TARGET_CHARACTER = CharacterType.ARTIST;
    private final int EXTRA_FOOD = 2;
    private final int EXTRA_PRESTIGE = 1;

    @BeforeEach
    void setUp() {
        // real player instance is created
        player = new Player("Alice", TotemColor.RED);

        // real game state is instantiated
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 2, new ArrayList<>(), new ArrayList<>());
        gameState = new GameState(2, 2, new ArrayList<>(), dummyTurnOrderTile, new Deck<>(), new HashMap<>());

        // real event card matching the target event type is created
        targetEventCard = new CavePaintingsEvent("event_01", 1, false, 1, 1, 0);

        // extra resources on event effect is instantiated with valid parameters
        effect = new ExtraResourcesOnEventEffect(TARGET_EVENT, TARGET_CHARACTER, EXTRA_FOOD, EXTRA_PRESTIGE);
    }

    @Test
    void constructorShouldThrowExceptionIfTargetEventIsNull() {
        // null pointer exception is expected when target event is null
        assertThrows(NullPointerException.class, () -> new ExtraResourcesOnEventEffect(null, TARGET_CHARACTER, EXTRA_FOOD, EXTRA_PRESTIGE));
    }

    @Test
    void constructorShouldThrowExceptionIfTargetCharacterIsNull() {
        // null pointer exception is expected when target character is null
        assertThrows(NullPointerException.class, () -> new ExtraResourcesOnEventEffect(TARGET_EVENT, null, EXTRA_FOOD, EXTRA_PRESTIGE));
    }

    @Test
    void constructorShouldThrowExceptionIfExtraFoodIsNegative() {
        // illegal argument exception is expected when extra food is negative
        assertThrows(IllegalArgumentException.class, () -> new ExtraResourcesOnEventEffect(TARGET_EVENT, TARGET_CHARACTER, -1, EXTRA_PRESTIGE));
    }

    @Test
    void constructorShouldThrowExceptionIfExtraPrestigeIsNegative() {
        // illegal argument exception is expected when extra prestige is negative
        assertThrows(IllegalArgumentException.class, () -> new ExtraResourcesOnEventEffect(TARGET_EVENT, TARGET_CHARACTER, EXTRA_FOOD, -1));
    }

    @Test
    void onEventShouldThrowExceptionIfEventIsNull() {
        // null pointer exception is expected when event is null
        assertThrows(NullPointerException.class, () -> effect.onEvent(null, player, gameState));
    }

    @Test
    void onEventShouldThrowExceptionIfPlayerIsNull() {
        // null pointer exception is expected when player is null
        assertThrows(NullPointerException.class, () -> effect.onEvent(targetEventCard, null, gameState));
    }

    @Test
    void onEventShouldThrowExceptionIfGameStateIsNull() {
        // null pointer exception is expected when game state is null
        assertThrows(NullPointerException.class, () -> effect.onEvent(targetEventCard, player, null));
    }

    @Test
    void onEventShouldAddZeroResourcesIfEventDoesNotMatch() {
        // target characters are added to the player to ensure failure is due to event mismatch
        player.addArtist();

        // an event of a different type is triggered
        EventCard mismatchEvent = new HuntEvent("event_02", 1, false, 1, 1);
        effect.onEvent(mismatchEvent, player, gameState);

        // zero resources are expected when the triggered event does not match the target event
        assertEquals(0, player.getFood());
        assertEquals(0, player.getPrestige());
    }

    @Test
    void onEventShouldAddZeroResourcesIfPlayerHasNoTargetCharacters() {
        // effect is triggered with the correct event but the player lacks the target character
        effect.onEvent(targetEventCard, player, gameState);

        // zero resources are expected when the player has no characters of the target type
        assertEquals(0, player.getFood());
        assertEquals(0, player.getPrestige());
    }

    @Test
    void onEventShouldAddCorrectResourcesForMultipleTargetCharacters() {
        // multiple target characters are added to the player
        player.addArtist();
        player.addArtist();
        player.addArtist();

        // effect is triggered with the matching event
        effect.onEvent(targetEventCard, player, gameState);

        // correct amount of food and prestige is expected based on the target character count
        assertEquals(6, player.getFood());
        assertEquals(3, player.getPrestige());
    }

    @Test
    void onEventShouldAddZeroResourcesIfBonusParametersAreZero() {
        // effect is instantiated with zero bonus parameters
        ExtraResourcesOnEventEffect zeroEffect = new ExtraResourcesOnEventEffect(TARGET_EVENT, TARGET_CHARACTER, 0, 0);

        // target character is added to the player
        player.addArtist();

        // effect is triggered with the matching event
        zeroEffect.onEvent(targetEventCard, player, gameState);

        // zero resources are added when bonus parameters are configured to zero
        assertEquals(0, player.getFood());
        assertEquals(0, player.getPrestige());
    }

    @Test
    void onEventShouldCoverAllCharacterTypes() {
        // one character of each type is added to the player
        player.addArtist();
        player.addHunter();
        player.addBuilder();
        player.addGatherer();
        player.addInventor();
        player.addShaman();

        // artist effect is tested to verify correct resource increment
        ExtraResourcesOnEventEffect artistEffect = new ExtraResourcesOnEventEffect(TARGET_EVENT, CharacterType.ARTIST, 1, 1);
        artistEffect.onEvent(targetEventCard, player, gameState);
        assertEquals(1, player.getFood());
        assertEquals(1, player.getPrestige());

        // hunter effect is tested to verify correct resource increment
        ExtraResourcesOnEventEffect hunterEffect = new ExtraResourcesOnEventEffect(TARGET_EVENT, CharacterType.HUNTER, 1, 1);
        hunterEffect.onEvent(targetEventCard, player, gameState);
        assertEquals(2, player.getFood());
        assertEquals(2, player.getPrestige());

        // builder effect is tested to verify resources are ignored
        ExtraResourcesOnEventEffect builderEffect = new ExtraResourcesOnEventEffect(TARGET_EVENT, CharacterType.BUILDER, 1, 1);
        builderEffect.onEvent(targetEventCard, player, gameState);
        assertEquals(2, player.getFood());
        assertEquals(2, player.getPrestige());

        // gatherer effect is tested to verify resources are ignored
        ExtraResourcesOnEventEffect gathererEffect = new ExtraResourcesOnEventEffect(TARGET_EVENT, CharacterType.GATHERER, 1, 1);
        gathererEffect.onEvent(targetEventCard, player, gameState);
        assertEquals(2, player.getFood());
        assertEquals(2, player.getPrestige());

        // inventor effect is tested to verify resources are ignored
        ExtraResourcesOnEventEffect inventorEffect = new ExtraResourcesOnEventEffect(TARGET_EVENT, CharacterType.INVENTOR, 1, 1);
        inventorEffect.onEvent(targetEventCard, player, gameState);
        assertEquals(2, player.getFood());
        assertEquals(2, player.getPrestige());

        // shaman effect is tested to verify resources are ignored
        ExtraResourcesOnEventEffect shamanEffect = new ExtraResourcesOnEventEffect(TARGET_EVENT, CharacterType.SHAMAN, 1, 1);
        shamanEffect.onEvent(targetEventCard, player, gameState);
        assertEquals(2, player.getFood());
        assertEquals(2, player.getPrestige());
    }

    @Test
    void emptyMethodsShouldNotAlterPlayerState() {
        // initial state is recorded
        int initialFood = player.getFood();
        int initialPrestige = player.getPrestige();

        // empty interface methods are invoked
        effect.onAcquire(player, gameState);
        effect.onRoundEnd(player, gameState);
        effect.onGameEnd(player, gameState);
        effect.onTotemPlaced(null, player, gameState);
        effect.onCharacterAdded(player, null);

        // player state is verified to remain completely unchanged
        assertEquals(initialFood, player.getFood());
        assertEquals(initialPrestige, player.getPrestige());
    }
}
