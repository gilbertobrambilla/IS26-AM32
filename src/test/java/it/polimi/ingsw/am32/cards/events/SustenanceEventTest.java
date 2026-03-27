package it.polimi.ingsw.am32.cards.events;

import it.polimi.ingsw.am32.Deck;
import it.polimi.ingsw.am32.GameState;
import it.polimi.ingsw.am32.Player;
import it.polimi.ingsw.am32.cards.acquirable.characters.ArtistCard;
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

class SustenanceEventTest {

    @Test
    void testGetEventType() {
        SustenanceEvent eventCard = new SustenanceEvent("SUST_01", 1, false, 2, 3);
        assertEquals(EventType.SUSTENANCE, eventCard.getEventType());
    }

    @Test
    void testResolveEvent() {
        SustenanceEvent eventCard = new SustenanceEvent("SUST_01", 1, false, 1, 3);

        //creating a test artist card
        ArtistCard dummyCharacter = new ArtistCard("DUMMY_ARTIST", 1, 0, 2, 0);

        //Creating a player that has no discounts and is able to pay everything
        Player pAlice = new Player("Alice", TotemColor.RED);
        pAlice.addCharacter(dummyCharacter);
        pAlice.addCharacter(dummyCharacter);
        pAlice.addFood(5);

        //Creating a player that is able to pay everything having a discount
        Player pBob = new Player("Bob", TotemColor.BLUE);
        pBob.addCharacter(dummyCharacter);
        pBob.addCharacter(dummyCharacter);
        pBob.addSustenanceDiscount(3);
        pBob.addFood(2);

        //Creating a player that has to lose PP because he cannot pay for everything
        Player pCharlie = new Player("Charlie", TotemColor.WHITE);
        pCharlie.addCharacter(dummyCharacter);
        pCharlie.addCharacter(dummyCharacter);
        pCharlie.addCharacter(dummyCharacter);
        pCharlie.addFood(2);

        //creating test parameters for the gameState
        List<OfferTile> dummyOfferTrack = new ArrayList<>();
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 3, new ArrayList<>(), new ArrayList<>());
        Deck dummyTribeDeck = new Deck();
        Map<Integer, Deck> dummyBuildingDecks = new HashMap<>();

        //Creating a test gameState and adding players to it con il nuovo costruttore
        GameState gameState = new GameState(3, 3, dummyOfferTrack, dummyTurnOrderTile, dummyTribeDeck, dummyBuildingDecks);
        gameState.getPlayers().add(pAlice);
        gameState.getPlayers().add(pBob);
        gameState.getPlayers().add(pCharlie);

        //Solving the event
        eventCard.resolveEvent(gameState);

        //Verifying Alice
        assertEquals(3, pAlice.getFood());
        assertEquals(0, pAlice.getPrestige());

        //Verifying Bob
        assertEquals(2, pBob.getFood());
        assertEquals(0, pBob.getPrestige());

        //Verifying Charlie
        assertEquals(0, pCharlie.getFood());
        assertEquals(-3, pCharlie.getPrestige());
    }

    // --- NUOVI TEST PER LE ECCEZIONI INTRODOTTE ---

    @Test
    void testResolveEvent_NullGameState() {
        SustenanceEvent eventCard = new SustenanceEvent("SUST_01", 1, false, 2, 3);
        assertThrows(NullPointerException.class, () -> eventCard.resolveEvent(null));
    }

    @Test
    void testConstructorExceptions() {
        assertThrows(IllegalArgumentException.class, () ->
                new SustenanceEvent("SUST_01", 1, false, -1, 3)
        );

        assertThrows(IllegalArgumentException.class, () ->
                new SustenanceEvent("SUST_01", 1, false, 2, -1)
        );
    }
}