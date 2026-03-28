package it.polimi.ingsw.am32;

import it.polimi.ingsw.am32.cards.Card;
import it.polimi.ingsw.am32.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.cards.events.EventCard;
import it.polimi.ingsw.am32.dao.*;
import it.polimi.ingsw.am32.factories.*;
import it.polimi.ingsw.am32.tiles.OfferTile;
import it.polimi.ingsw.am32.tiles.TurnOrderTile;

import java.sql.Connection;
import java.util.*;

public class GameCreator {
    private final int gameId;
    private final Connection connection;

    public GameCreator(int gameId, Connection connection) {
        if (gameId < 0) {
            throw new IllegalArgumentException("gameId cannot be negative");
        }
        Objects.requireNonNull(connection, "connection cannot be null");

        this.gameId = gameId;
        this.connection = connection;
    }

    public GameState createGame(int numOfPlayers) {
        if (numOfPlayers < 2 || numOfPlayers > 5) {
            throw new IllegalArgumentException("numOfPlayers must be between 2 and 5");
        }

        // decks are generated
        Deck<Card> tribeDeck = generateTribeDeck(numOfPlayers);
        Map<Integer, Deck<BuildingCard>> buildingDecks = generateBuildingDecks(numOfPlayers);

        // tiles are generated
        List<OfferTile> offerTrack = generateOfferTrack(numOfPlayers);
        TurnOrderTile turnOrderTile = generateTurnOrderTile(numOfPlayers);

        GameState gameState = new GameState(gameId, numOfPlayers, offerTrack, turnOrderTile, tribeDeck, buildingDecks);

        return gameState;
    }

    private Deck<Card> generateTribeDeck(int numOfPlayers) {
        CharacterCardDAO charDao = new CharacterCardDAO(connection);
        EventCardDAO eventDao = new EventCardDAO(connection);

        List<CharacterCard> characters = new CharacterCardFactory().createCards(charDao.fetchAll(numOfPlayers));
        List<EventCard> events = new EventCardFactory().createCards(eventDao.fetchAll());

        List<Card> finalEvents = new ArrayList<>();
        List<Card> era1 = new ArrayList<>();
        List<Card> era2 = new ArrayList<>();
        List<Card> era3 = new ArrayList<>();

        // events are sorted isolating final ones
        for (EventCard e : events) {
            if (e.isFinal()) {
                finalEvents.add(e);
            } else {
                if (e.getEra() == 1) era1.add(e);
                else if (e.getEra() == 2) era2.add(e);
                else if (e.getEra() == 3) era3.add(e);
            }
        }

        // characters are sorted
        for (CharacterCard c : characters) {
            if (c.getEra() == 1) era1.add(c);
            else if (c.getEra() == 2) era2.add(c);
            else if (c.getEra() == 3) era3.add(c);
        }

        // cards are shuffled independently per era
        Collections.shuffle(finalEvents);
        Collections.shuffle(era1);
        Collections.shuffle(era2);
        Collections.shuffle(era3);

        // final deck is built knowing .draw() fetches from end of list
        Deck<Card> tribeDeck = new Deck<>();
        finalEvents.forEach(tribeDeck::addCard);
        era3.forEach(tribeDeck::addCard);
        era2.forEach(tribeDeck::addCard);
        era1.forEach(tribeDeck::addCard);

        return tribeDeck;
    }

    private Map<Integer, Deck<BuildingCard>> generateBuildingDecks(int numOfPlayers) {
        BuildingCardDAO bldDao = new BuildingCardDAO(connection);
        List<BuildingCard> allBuildings = new BuildingCardFactory().createCards(bldDao.fetchAll());

        List<BuildingCard> era1 = new ArrayList<>();
        List<BuildingCard> era2 = new ArrayList<>();
        List<BuildingCard> era3 = new ArrayList<>();

        for (BuildingCard b : allBuildings) {
            if (b.getEra() == 1) era1.add(b);
            else if (b.getEra() == 2) era2.add(b);
            else if (b.getEra() == 3) era3.add(b);
        }

        Collections.shuffle(era1);
        Collections.shuffle(era2);
        Collections.shuffle(era3);

        // building is based on number of players
        int era1Count = 0, era2Count = 0, era3Count = 0;
        switch (numOfPlayers) {
            case 2 -> { era1Count = 1; era2Count = 2; era3Count = 3; }
            case 3 -> { era1Count = 2; era2Count = 2; era3Count = 4; }
            case 4 -> { era1Count = 2; era2Count = 3; era3Count = 4; }
            case 5 -> { era1Count = 3; era2Count = 2; era3Count = 5; }
        }

        Map<Integer, Deck<BuildingCard>> buildingDecks = new HashMap<>();
        buildingDecks.put(1, createDeckFromSubList(era1, era1Count));
        buildingDecks.put(2, createDeckFromSubList(era2, era2Count));
        buildingDecks.put(3, createDeckFromSubList(era3, era3Count));

        return buildingDecks;
    }

    private Deck<BuildingCard> createDeckFromSubList(List<BuildingCard> cards, int count) {
        Deck<BuildingCard> deck = new Deck<>();
        List<BuildingCard> subList = cards.subList(0, count);
        subList.forEach(deck::addCard);
        return deck;
    }

    private List<OfferTile> generateOfferTrack(int numOfPlayers) {
        OfferTileDAO dao = new OfferTileDAO(connection);
        List<OfferTile> offerTrack = new OfferTileFactory().createTiles(dao.fetchAll(numOfPlayers));

        // tiles are sorted by id to form the correct track
        offerTrack.sort(Comparator.comparing(OfferTile::getId));
        return offerTrack;
    }

    private TurnOrderTile generateTurnOrderTile(int numOfPlayers) {
        TurnOrderTileDAO dao = new TurnOrderTileDAO(connection);
        return new TurnOrderTileFactory().createTile(dao.fetch(numOfPlayers));
    }
}
