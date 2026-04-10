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

class ShamanicRitualEventTest {

    @Test
    void testGetEventType() {
        ShamanicRitualEvent eventCard = new ShamanicRitualEvent("SHAMAN_01", 1, false, 5, 3);
        assertEquals(EventType.SHAMANIC_RITUAL, eventCard.getEventType());
    }

    @Test
    void testResolveEvent_NormalAndAbilities() {
        //Creating test eventCard
        ShamanicRitualEvent eventCard = new ShamanicRitualEvent("SHAMAN_01", 1, false, 5, 3);

        //Creating the player with the least number of stars
        Player pMinNormal = new Player("Alice", TotemColor.RED);

        //Creating player with a building that has shamanic event immunity and the least number of stars
        Player pMinImmune = new Player("Bob", TotemColor.BLUE);
        pMinImmune.setShamanicImmunity(true);

        //Creating a player that has a middle number of stars
        Player pMid = new Player("Charlie", TotemColor.WHITE);
        pMid.addShamanStars(2);

        //Creating 2 players that have the maximum number of stars
        Player pMaxNormal = new Player("Diana", TotemColor.YELLOW);
        pMaxNormal.addShamanStars(5);

        //The player has both the maximum number of stars and the building that doubles the PP received for this event
        Player pMaxDouble = new Player("Eve", TotemColor.BLACK);
        pMaxDouble.addShamanStars(5);
        pMaxDouble.setDoubleShamanicBonus(true);

        //creating fake parameteres for the gameState
        List<OfferTile> dummyOfferTrack = new ArrayList<>();
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 5, new ArrayList<>(), new ArrayList<>());
        Deck dummyTribeDeck = new Deck();
        Map<Integer, Deck> dummyBuildingDecks = new HashMap<>();

        //Creating the GameState and adding every player
        GameState gameState = new GameState(1, 5, dummyOfferTrack, dummyTurnOrderTile, dummyTribeDeck, dummyBuildingDecks);
        gameState.getPlayers().add(pMinNormal);
        gameState.getPlayers().add(pMinImmune);
        gameState.getPlayers().add(pMid);
        gameState.getPlayers().add(pMaxNormal);
        gameState.getPlayers().add(pMaxDouble);

        //Solving event
        eventCard.resolveEvent(gameState);

        //Verifying
        assertEquals(-3, pMinNormal.getPrestige());
        assertEquals(0, pMinImmune.getPrestige());
        assertEquals(0, pMid.getPrestige());
        assertEquals(5, pMaxNormal.getPrestige());
        assertEquals(10, pMaxDouble.getPrestige());
    }

    @Test
    void testResolveEvent_TotalTie() {
        //Testing the case where every player has the same amount of stars and no building
        ShamanicRitualEvent eventCard = new ShamanicRitualEvent("SHAMAN_01", 1, false, 5, 3);

        Player p1 = new Player("Alice", TotemColor.RED);
        Player p2 = new Player("Bob", TotemColor.BLUE);

        //creating fake parameters for the gameState
        List<OfferTile> dummyOfferTrack = new ArrayList<>();
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 2, new ArrayList<>(), new ArrayList<>());
        Deck dummyTribeDeck = new Deck();
        Map<Integer, Deck> dummyBuildingDecks = new HashMap<>();

        //Creating a test GameState
        GameState gameState = new GameState(2, 2, dummyOfferTrack, dummyTurnOrderTile, dummyTribeDeck, dummyBuildingDecks);
        gameState.getPlayers().add(p1);
        gameState.getPlayers().add(p2);

        //Solving event
        eventCard.resolveEvent(gameState);

        //Verifying
        assertEquals(2, p1.getPrestige());
        assertEquals(2, p2.getPrestige());
    }

    @Test
    void testResolveEvent_TotalTieWithAbilities() {
        //Creating test eventCard
        ShamanicRitualEvent eventCard = new ShamanicRitualEvent("SHAMAN_01", 1, false, 5, 3);

        //Creating a player that has immunity but doesn't have double points for the win
        Player pAlice = new Player("Alice", TotemColor.RED);
        pAlice.setShamanicImmunity(true);

        //Creating a player that has double points for the win but doesn't have immunity
        Player pBob = new Player("Bob", TotemColor.BLUE);
        pBob.setDoubleShamanicBonus(true);

        //creating fake parameters for the gameState
        List<OfferTile> dummyOfferTrack = new ArrayList<>();
        TurnOrderTile dummyTurnOrderTile = new TurnOrderTile("DUMMY", 2, new ArrayList<>(), new ArrayList<>());
        Deck dummyTribeDeck = new Deck();
        Map<Integer, Deck> dummyBuildingDecks = new HashMap<>();

        //Creating gameState and adding players
        GameState gameState = new GameState(2, 2, dummyOfferTrack, dummyTurnOrderTile, dummyTribeDeck, dummyBuildingDecks);
        gameState.getPlayers().add(pAlice);
        gameState.getPlayers().add(pBob);

        //Solving event
        eventCard.resolveEvent(gameState);

        //Verifying event
        assertEquals(5, pAlice.getPrestige());
        assertEquals(7, pBob.getPrestige());
    }

    //testing the exceptions

    @Test
    void testResolveEvent_NullGameState() {
        ShamanicRitualEvent eventCard = new ShamanicRitualEvent("SHAMAN_01", 1, false, 5, 3);
        assertThrows(NullPointerException.class, () -> eventCard.resolveEvent(null));
    }

    @Test
    void testConstructorExceptions() {
        //checking for negative values in prestigeBonus
        assertThrows(IllegalArgumentException.class, () ->
                new ShamanicRitualEvent("SHAMAN_01", 1, false, -1, 3)
        );

        //checking for negative values in prestigePenalty
        assertThrows(IllegalArgumentException.class, () ->
                new ShamanicRitualEvent("SHAMAN_01", 1, false, 5, -1)
        );
    }
}