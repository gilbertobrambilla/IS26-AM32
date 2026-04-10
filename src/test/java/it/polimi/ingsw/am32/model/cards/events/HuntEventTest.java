package it.polimi.ingsw.am32.model.cards.events;

import it.polimi.ingsw.am32.model.Deck;
import it.polimi.ingsw.am32.model.GameState;
import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.enums.EventType;
import it.polimi.ingsw.am32.model.enums.TotemColor;
import it.polimi.ingsw.am32.model.tiles.OfferTile;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HuntEventTest {

    @Test
    void testGetEventType() {
        HuntEvent eventCard = new HuntEvent("HUNT_01", 1, false, 1, 1);
        assertEquals(EventType.HUNT, eventCard.getEventType());
    }

    @Test
    void testResolveEvent() {
        HuntEvent eventCard = new HuntEvent("HUNT_01", 1, false, 1, 1);

        //Creating 2 test players
        Player player1 = new Player("Alice", TotemColor.RED);
        Player player2 = new Player("Bob", TotemColor.BLUE);

        //Assigning 0 HunterCard to Alice and 2 HunterCard to Bob
        player2.addHunter();
        player2.addHunter();

        //creating fake parameters for the gamestate
        List<OfferTile> dummyOfferTrack = new ArrayList<>();
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 2, new ArrayList<>(), new ArrayList<>());
        Deck dummyTribeDeck = new Deck();
        Map<Integer, Deck> dummyBuildingDecks = new HashMap<>();

        //creating a test gameState
        GameState gameState = new GameState(2, 2, dummyOfferTrack, dummyTurnOrderTile, dummyTribeDeck, dummyBuildingDecks);

        //Adding players to the gameState
        gameState.getPlayers().add(player1);
        gameState.getPlayers().add(player2);

        //Solving event
        eventCard.resolveEvent(gameState);

        //Verifying that Alice gets nothing since she's got 0 HunterCard
        assertEquals(0, player1.getFood());
        assertEquals(0, player1.getPrestige());

        //Verifying that Bob gets 2 foods and 2 PP since he's got 2 HunterCard
        assertEquals(2, player2.getFood());
        assertEquals(2, player2.getPrestige());
    }

    //testing the exceptions
    @Test
    void testResolveEvent_NullGameState() {
        HuntEvent eventCard = new HuntEvent("HUNT_01", 1, false, 1, 1);
        assertThrows(NullPointerException.class, () -> eventCard.resolveEvent(null));
    }

    @Test
    void testConstructorExceptions() {
        //checking for negative values in foodBonus
        assertThrows(IllegalArgumentException.class, () ->
                new HuntEvent("HUNT_01", 1, false, -1, 1)
        );

        //checking for negative values in prestigeBonus
        assertThrows(IllegalArgumentException.class, () ->
                new HuntEvent("HUNT_01", 1, false, 1, -1)
        );
    }
}