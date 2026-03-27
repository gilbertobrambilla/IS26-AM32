package it.polimi.ingsw.am32.cards.events;

import it.polimi.ingsw.am32.Deck;
import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.enums.EventType;
import it.polimi.ingsw.am32.enums.TotemColor;
import it.polimi.ingsw.am32.tiles.OfferTile;
import it.polimi.ingsw.am32.tiles.TurnOrderTile;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CavePaintingsEventTest {

    @Test
    void testGetEventType() {
        CavePaintingsEvent eventCard = new CavePaintingsEvent("CAVE_01", 1, false, 2, 1, 3);
        assertEquals(EventType.CAVE_PAINTINGS, eventCard.getEventType());
    }

    @Test
    void testResolveEvent() {
        CavePaintingsEvent eventCard = new CavePaintingsEvent("CAVE_01", 1, false, 2, 2, 2);

        //creating 2 test players
        Player player1 = new Player("Alice", TotemColor.RED);
        Player player2 = new Player("Bob", TotemColor.BLUE);

        //assigning artistCard to players
        player1.addArtist(); //Alice has only on artist so she will have to pay 2PP

        player2.addArtist();
        player2.addArtist(); //Bob has 2 artist so he will receive 4PP(2x2)

        //creating a test gamestate
        List<OfferTile> dummyOfferTrack = new ArrayList<>();
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 2, new ArrayList<>(), new ArrayList<>());
        Deck dummyTribeDeck = new Deck();
        Map<Integer, Deck> dummyBuildingDecks = new HashMap<>();
        GameState gameState = new GameState(1, 2, dummyOfferTrack, dummyTurnOrderTile, dummyTribeDeck, dummyBuildingDecks);

        //adding players to gamestate
        gameState.getPlayers().add(player1);
        gameState.getPlayers().add(player2);

        //solving event
        eventCard.resolveEvent(gameState);

        //checking results
        assertEquals(-2, player1.getPrestige()); // Alice: 0-3=-3
        assertEquals(4, player2.getPrestige());  // Bob: 0+4=4
    }

    //testing the exceptions
    @Test
    void testResolveEvent_NullGameState() {
        CavePaintingsEvent eventCard = new CavePaintingsEvent("CAVE_01", 1, false, 2, 1, 3);

        assertThrows(NullPointerException.class, () -> eventCard.resolveEvent(null));
    }

    @Test
    void testConstructorExceptions() {
        //checking for negative values in prestigeBonus
        assertThrows(IllegalArgumentException.class, () ->
                new CavePaintingsEvent("CAVE_01", 1, false, -1, 1, 3)
        );

        //checking for negative values in bonusThreshold
        assertThrows(IllegalArgumentException.class, () ->
                new CavePaintingsEvent("CAVE_01", 1, false, 2, -1, 3)
        );

        //checking for negative values in prestigePenalty
        assertThrows(IllegalArgumentException.class, () ->
                new CavePaintingsEvent("CAVE_01", 1, false, 2, 1, -1)
        );
    }
}