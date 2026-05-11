package it.polimi.ingsw.am32.model;

import it.polimi.ingsw.am32.model.cards.Card;
import it.polimi.ingsw.am32.model.cards.CardVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    //dummy class to instantiate cards for the deck
    private static class DummyCard extends Card {
        public DummyCard(String id, int era) {
            super(id, era);
        }

        @Override
        public void accept(CardVisitor visitor) {
            //empty implementation for testing purposes
        }
    }

    private Deck<DummyCard> deck;

    @BeforeEach
    void setUp() {
        //initialize an empty deck before each test
        deck = new Deck<>();
    }

    @Test
    void constructorShouldCreateEmptyDeck() {
        //assert: the newly created deck must be empty and have size 0
        assertTrue(deck.isEmpty());
        assertEquals(0, deck.getSize());
    }

    @Test
    void addCardShouldIncreaseSizeAndMakeDeckNotEmpty() {
        //arrange: create a dummy card
        DummyCard card = new DummyCard("C_01", 1);

        //act: add the card to the deck
        deck.addCard(card);

        //assert: size should be 1 and deck should not be empty
        assertFalse(deck.isEmpty());
        assertEquals(1, deck.getSize());
    }

    @Test
    void addCardShouldThrowExceptionIfCardIsNull() {
        //arrange, act and assert: adding a null card throws exception
        assertThrows(NullPointerException.class, () -> {
            deck.addCard(null);
        });
    }

    @Test
    void drawShouldReturnLastAddedCardAndDecreaseSize() {
        //arrange: add two cards to the deck
        DummyCard card1 = new DummyCard("C_01", 1);
        DummyCard card2 = new DummyCard("C_02", 2);
        deck.addCard(card1);
        deck.addCard(card2);

        //act: draw a card
        DummyCard drawnCard = deck.draw();

        //assert: it should be the last added card (lifo) and size should decrease to 1
        assertEquals(card2, drawnCard);
        assertEquals(1, deck.getSize());
    }

    @Test
    void drawShouldThrowExceptionIfDeckIsEmpty() {
        //arrange, act and assert: drawing from empty deck throws exception
        assertThrows(IllegalStateException.class, () -> {
            deck.draw();
        });
    }

    @Test
    void getFirstCardEraShouldReturnEraOfLastAddedCardWithoutRemovingIt() {
        //arrange: add two cards with different eras
        deck.addCard(new DummyCard("C_01", 1));
        deck.addCard(new DummyCard("C_02", 3));

        //act: get the era of the first card to be drawn (the one at the end of the list)
        int era = deck.getFirstCardEra();

        //assert: it should match the era of the last added card
        assertEquals(3, era);

        //assert: getting the era should not remove the card (size is still 2)
        assertEquals(2, deck.getSize());
    }

    @Test
    void getFirstCardEraShouldThrowExceptionIfDeckIsEmpty() {
        //arrange, act and assert: getting era from empty deck throws exception
        assertThrows(IllegalStateException.class, () -> {
            deck.getFirstCardEra();
        });
    }
}