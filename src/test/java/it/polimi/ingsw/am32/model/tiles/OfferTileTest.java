package it.polimi.ingsw.am32.model.tiles;

import it.polimi.ingsw.am32.model.Player;
import it.polimi.ingsw.am32.model.exceptions.InvalidGameActionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OfferTileTest {

    private Action dummyAction;
    private OfferTile offerTile;

    @BeforeEach
    void setUp() {
        //creo una action fittizia per i test
        //nota: se action non ha un costruttore vuoto, dovrai adattare questa riga
        dummyAction = new Action(1, 0, 0) {
            @Override
            public boolean isValidSelection(int requestedUpper, int requestedLower, int availableUpper, int availableLower) {
                //restituisco true solo se requestedUpper è uguale a 1 per testare la delega
                return requestedUpper == 1;
            }
        };

        //inizializzo la tessera offerta prima di ogni test
        offerTile = new OfferTile("OT_01", 3, dummyAction);
    }

    @Test
    void constructorShouldSetValuesAndInitiallyBeFree() {
        //act and assert: verifico i valori iniziali tramite i getter
        assertEquals("OT_01", offerTile.getId());
        assertEquals(3, offerTile.getRequiredPlayers());
        assertEquals(dummyAction, offerTile.getAction());
        assertNull(offerTile.getOccupyingPlayer());
        assertTrue(offerTile.isFree());
    }

    @Test
    void constructorShouldThrowExceptionIfActionIsNull() {
        //arrange, act and assert: verifico eccezione per action nulla
        assertThrows(NullPointerException.class, () -> {
            new OfferTile("OT_02", 3, null);
        });
    }

    @Test
    void placePlayerShouldSetOccupyingPlayerAndMakeTileNotFree() {
        //arrange: creo un giocatore
        Player player = new Player("Alice");

        //act: piazzo il giocatore
        offerTile.placePlayer(player);

        //assert: verifico che la tessera sia occupata da alice
        assertFalse(offerTile.isFree());
        assertEquals(player, offerTile.getOccupyingPlayer());
    }

    @Test
    void placePlayerShouldThrowExceptionIfPlayerIsNull() {
        //arrange, act and assert: verifico eccezione per giocatore nullo
        assertThrows(NullPointerException.class, () -> {
            offerTile.placePlayer(null);
        });
    }

    @Test
    void placePlayerShouldThrowExceptionIfTileIsAlreadyOccupied() {
        //arrange: piazzo un primo giocatore
        offerTile.placePlayer(new Player("Alice"));

        //act and assert: piazzarne un altro deve lanciare eccezione
        assertThrows(InvalidGameActionException.class, () -> {
            offerTile.placePlayer(new Player("Bob"));
        });
    }

    @Test
    void removePlayerShouldFreeTheTile() {
        //arrange: piazzo un giocatore
        offerTile.placePlayer(new Player("Charlie"));
        assertFalse(offerTile.isFree());

        //act: rimuovo il giocatore
        offerTile.removePlayer();

        //assert: la tessera deve tornare libera
        assertTrue(offerTile.isFree());
        assertNull(offerTile.getOccupyingPlayer());
    }

    @Test
    void isValidSelectionShouldDelegateToAction() {
        //act: richiamo il metodo con requestedUpper = 1 (che il mio dummy action valuta come true)
        boolean resultTrue = offerTile.isValidSelection(1, 0, 0, 0);

        //act: richiamo il metodo con requestedUpper diverso da 1 (valutato come false)
        boolean resultFalse = offerTile.isValidSelection(2, 0, 0, 0);

        //assert: la risposta deve coincidere con la logica del dummy action
        assertTrue(resultTrue);
        assertFalse(resultFalse);
    }
}
