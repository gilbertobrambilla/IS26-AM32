package it.polimi.ingsw.am32.model.dao;

import it.polimi.ingsw.am32.dao.OfferTileDAO;
import it.polimi.ingsw.am32.dto.setup.OfferTileDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OfferTileDAOTest {

    private Connection testConnection;
    private OfferTileDAO dao;

    @BeforeEach
    void setUp() throws SQLException {
        // create H2 in-memory database connection
        testConnection = DriverManager.getConnection("jdbc:h2:mem:testdb_offer;DB_CLOSE_DELAY=-1", "sa", "");

        // set up the table and mock data for the test
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("CREATE TABLE offer_tiles (" +
                    "id VARCHAR(255) PRIMARY KEY, " +
                    "min_players INT, " +
                    "act_draws_upper INT, " +
                    "act_draws_lower INT, " +
                    "act_food_bonus INT)");

            // inserting tiles with different player requirements
            stmt.execute("INSERT INTO offer_tiles VALUES ('tile_01', 2, 1, 0, 0)"); // min 2 players
            stmt.execute("INSERT INTO offer_tiles VALUES ('tile_02', 4, 0, 1, 1)"); // min 4 players
            stmt.execute("INSERT INTO offer_tiles VALUES ('tile_03', 3, 1, 1, 0)"); // min 3 players
        }

        // initialize the DAO with the test connection
        dao = new OfferTileDAO(testConnection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // clean up the DB: destroy the table at the end of each test
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS offer_tiles");
        }
        testConnection.close();
    }

    @Test
    void fetchAllShouldReturnAllTiles() {
        // execute the query to get all tiles
        List<OfferTileDTO> tiles = dao.fetchAll();

        assertEquals(3, tiles.size());
    }

    @Test
    void fetchAllWithPlayersShouldFilterCorrectly() {
        // execute the query filtering for 3 players
        List<OfferTileDTO> tiles = dao.fetchAll(3);

        assertEquals(2, tiles.size());

        // verify specific mapping for one tile
        OfferTileDTO tile01 = tiles.stream()
                .filter(t -> t.getId().equals("tile_01"))
                .findFirst()
                .orElse(null);

        assertNotNull(tile01);
        assertEquals(2, tile01.getMinPlayers());
        assertEquals(1, tile01.getActDrawsUpper());
        assertEquals(0, tile01.getActDrawsLower());
        assertEquals(0, tile01.getActFoodBonus());
    }

    @Test
    void fetchAllWithInvalidPlayersShouldThrowException() {
        // check bounds for numOfPlayers (2-5)
        assertThrows(IllegalArgumentException.class, () -> dao.fetchAll(1));
        assertThrows(IllegalArgumentException.class, () -> dao.fetchAll(6));
    }

    @Test
    void executeFetchShouldThrowRuntimeExceptionOnDatabaseError() throws SQLException {
        // simulate a database failure by dropping the table
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DROP TABLE offer_tiles");
        }

        RuntimeException exception = assertThrows(RuntimeException.class, () -> dao.fetchAll());
        assertTrue(exception.getMessage().contains("error: the database cannot be read"));
    }
}
