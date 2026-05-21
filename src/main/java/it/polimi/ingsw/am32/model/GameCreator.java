package it.polimi.ingsw.am32.model;

import it.polimi.ingsw.am32.dto.setup.BuildingCardDTO;
import it.polimi.ingsw.am32.dto.setup.CharacterCardDTO;
import it.polimi.ingsw.am32.dto.setup.EventCardDTO;
import it.polimi.ingsw.am32.dto.setup.OfferTileDTO;
import it.polimi.ingsw.am32.dto.setup.TurnOrderTileDTO;
import it.polimi.ingsw.am32.model.cards.Card;
import it.polimi.ingsw.am32.model.cards.acquirable.buildings.BuildingCard;
import it.polimi.ingsw.am32.model.cards.acquirable.characters.CharacterCard;
import it.polimi.ingsw.am32.model.cards.events.EventCard;
import it.polimi.ingsw.am32.model.enums.LobbyErrorReason;
import it.polimi.ingsw.am32.model.exceptions.InvalidLobbyActionException;
import it.polimi.ingsw.am32.model.factories.*;
import it.polimi.ingsw.am32.model.tiles.OfferTile;
import it.polimi.ingsw.am32.model.tiles.TurnOrderTile;
import it.polimi.ingsw.am32.utils.JsonAssetLoader;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Game creator is implemented to instantiate the initial state of the game.
 */
public class GameCreator {
    private final int gameId;

    /**
     * Game creator is instantiated.
     *
     * @param gameId the game identifier
     */
    public GameCreator(int gameId) {
        // game id is validated
        if (gameId < 0) {
            // exception is thrown if game id is negative
            throw new IllegalArgumentException("gameId cannot be negative");
        }

        // field is assigned
        this.gameId = gameId;
    }

    /**
     * Game state is created based on the number of players.
     *
     * @param numOfPlayers the number of players in the game
     * @return the generated game state
     */
    public GameState createGame(int numOfPlayers) {
        // player count is validated
        if (numOfPlayers < 2 || numOfPlayers > 5) {
            // exception is thrown if player count is out of bounds
            throw new InvalidLobbyActionException(
                    LobbyErrorReason.INVALID_PLAYER_COUNT,
                    "numOfPlayers must be between 2 and 5"
            );
        }

        // decks are generated
        Deck<Card> tribeDeck = generateTribeDeck(numOfPlayers);
        Map<Integer, Deck<BuildingCard>> buildingDecks = generateBuildingDecks(numOfPlayers);

        // tiles are generated
        List<OfferTile> offerTrack = generateOfferTrack(numOfPlayers);
        TurnOrderTile turnOrderTile = generateTurnOrderTile(numOfPlayers);

        // game state is instantiated and returned
        return new GameState(this.gameId, numOfPlayers, offerTrack, turnOrderTile, tribeDeck, buildingDecks);
    }

    /**
     * Tribe deck is generated and shuffled.
     *
     * @param numOfPlayers the number of players
     * @return the generated tribe deck
     */
    private Deck<Card> generateTribeDeck(int numOfPlayers) {
        // shared loader instance is retrieved
        JsonAssetLoader loader = JsonAssetLoader.getInstance();

        // character dto are fetched and filtered by minimum players
        List<CharacterCardDTO> charDto = loader.getCharacterCards().stream()
                .filter(c -> c.getMinPlayers() <= numOfPlayers)
                .collect(Collectors.toList());

        // event dto are fetched
        List<EventCardDTO> eventDto = loader.getEventCards();

        // character cards are created
        List<CharacterCard> characters = new CharacterCardFactory().createCards(charDto);

        // event cards are created by the factory
        List<EventCard> events = new EventCardFactory().createCards(eventDto);

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

        // final deck is built knowing draw method fetches from end of list
        Deck<Card> tribeDeck = new Deck<>();
        finalEvents.forEach(tribeDeck::addCard);
        era3.forEach(tribeDeck::addCard);
        era2.forEach(tribeDeck::addCard);
        era1.forEach(tribeDeck::addCard);

        // deck is returned
        return tribeDeck;
    }

    /**
     * Building decks are generated for each era.
     *
     * @param numOfPlayers the number of players
     * @return a map containing the building decks
     */
    private Map<Integer, Deck<BuildingCard>> generateBuildingDecks(int numOfPlayers) {
        // shared loader instance is retrieved
        JsonAssetLoader loader = JsonAssetLoader.getInstance();

        // building dto are fetched
        List<BuildingCardDTO> buildingDto = loader.getBuildingCards();

        // building cards are created by the factory
        List<BuildingCard> allBuildings = new BuildingCardFactory().createCards(buildingDto);

        List<BuildingCard> era1 = new ArrayList<>();
        List<BuildingCard> era2 = new ArrayList<>();
        List<BuildingCard> era3 = new ArrayList<>();

        // buildings are sorted by era
        for (BuildingCard b : allBuildings) {
            if (b.getEra() == 1) era1.add(b);
            else if (b.getEra() == 2) era2.add(b);
            else if (b.getEra() == 3) era3.add(b);
        }

        // decks are shuffled
        Collections.shuffle(era1);
        Collections.shuffle(era2);
        Collections.shuffle(era3);

        // building count is determined based on number of players
        int era1Count = 0, era2Count = 0, era3Count = 0;
        switch (numOfPlayers) {
            case 2 -> { era1Count = 1; era2Count = 2; era3Count = 3; }
            case 3 -> { era1Count = 2; era2Count = 2; era3Count = 4; }
            case 4 -> { era1Count = 2; era2Count = 3; era3Count = 4; }
            case 5 -> { era1Count = 3; era2Count = 2; era3Count = 5; }
        }

        // deck map is populated
        Map<Integer, Deck<BuildingCard>> buildingDecks = new HashMap<>();
        buildingDecks.put(1, createDeckFromSubList(era1, era1Count));
        buildingDecks.put(2, createDeckFromSubList(era2, era2Count));
        buildingDecks.put(3, createDeckFromSubList(era3, era3Count));

        // deck map is returned
        return buildingDecks;
    }

    /**
     * Sub-deck is created from a list of building cards.
     *
     * @param cards the list of building cards
     * @param count the number of cards to include
     * @return the generated deck
     */
    private Deck<BuildingCard> createDeckFromSubList(List<BuildingCard> cards, int count) {
        // deck is instantiated
        Deck<BuildingCard> deck = new Deck<>();

        // sub-list is extracted defensively
        int targetCount = Math.min(count, cards.size());
        List<BuildingCard> subList = cards.subList(0, targetCount);

        // cards are added to the deck
        subList.forEach(deck::addCard);

        // deck is returned
        return deck;
    }

    /**
     * Offer track is generated.
     *
     * @param numOfPlayers the number of players
     * @return the generated list of offer tiles
     */
    private List<OfferTile> generateOfferTrack(int numOfPlayers) {
        // shared loader instance is retrieved
        JsonAssetLoader loader = JsonAssetLoader.getInstance();

        // offer tile dto are fetched and filtered by minimum players
        List<OfferTileDTO> tileDto = loader.getOfferTiles().stream()
                .filter(t -> t.getMinPlayers() <= numOfPlayers)
                .collect(Collectors.toList());

        // offer tiles are created by the factory
        List<OfferTile> offerTrack = new OfferTileFactory().createTiles(tileDto);

        // tiles are sorted by id to form the correct track
        offerTrack.sort(Comparator.comparing(OfferTile::getId));

        // offer track is returned
        return offerTrack;
    }

    /**
     * Turn order tile is generated.
     *
     * @param numOfPlayers the number of players
     * @return the generated turn order tile
     */
    private TurnOrderTile generateTurnOrderTile(int numOfPlayers) {
        // shared loader instance is retrieved
        JsonAssetLoader loader = JsonAssetLoader.getInstance();

        // turn order tile dto is filtered by exact required players
        TurnOrderTileDTO tileDto = loader.getTurnOrderTiles().stream()
                .filter(t -> t.getRequiredPlayers() == numOfPlayers)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("turn order tile not found for " + numOfPlayers + " players"));

        // turn order tile is created by the factory and returned
        return new TurnOrderTileFactory().createTile(tileDto);
    }
}
