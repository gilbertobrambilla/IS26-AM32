package it.polimi.ingsw.am32.model.dao;

import it.polimi.ingsw.am32.model.dto.setup.TurnOrderTileDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TurnOrderTileDAOTest {

    private Connection testConnection;
    private TurnOrderTileDAO dao;

    @BeforeEach
    void setUp() throws SQLException {
        // create H2 in-memory database connection
        testConnection = DriverManager.getConnection("jdbc:h2:mem:testdb_turn;DB_CLOSE_DELAY=-1", "sa", "");

        // set up the table and mock data based on TurnOrderTileDAO columns
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("CREATE TABLE turn_order_tiles (" +
                    "id VARCHAR(255) PRIMARY KEY, " +
                    "required_players INT, " +
                    "food_modifiers VARCHAR(255), " +
                    "prestige_modifiers VARCHAR(255))");

            // insert mock data
            stmt.execute("INSERT INTO turn_order_tiles VALUES ('tile_2p', 2, '[1,0]', '[0,0]')");
            stmt.execute("INSERT INTO turn_order_tiles VALUES ('tile_3p', 3, '[1,1]', '[0,1]')");
        }

        // initialize the DAO
        dao = new TurnOrderTileDAO(testConnection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // clean up the DB: destroy the table at the end of each test
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS turn_order_tiles");
        }
        testConnection.close();
    }

    @Test
    void fetchAllShouldReturnAllTiles() {
        // covers the fetchAll() method
        List<TurnOrderTileDTO> tiles = dao.fetchAll();
        assertEquals(2, tiles.size());
    }

    @Test
    void fetchShouldReturnSpecificTile() {
        // covers the fetch(int) method and its mapping
        TurnOrderTileDTO tile = dao.fetch(2);

        assertNotNull(tile);
        assertEquals("tile_2p", tile.getId());
        assertEquals(2, tile.getRequiredPlayers());
        assertEquals("[1,0]", tile.getFoodModifiers());
        assertEquals("[0,0]", tile.getPrestigeModifiers());
    }

    @Test
    void fetchWithInvalidPlayersShouldThrowException() {
        // covers the branches of the 'if (numOfPlayers < 2 || numOfPlayers > 5)' block
        assertThrows(IllegalArgumentException.class, () -> dao.fetch(1));
        assertThrows(IllegalArgumentException.class, () -> dao.fetch(6));
    }

    @Test
    void fetchShouldThrowExceptionIfTileNotFound() {
        // covers the scenario where getFirst() throws NoSuchElementException if the list is empty
        assertThrows(java.util.NoSuchElementException.class, () -> dao.fetch(4));
    }

    @Test
    void executeFetchShouldThrowRuntimeExceptionOnDatabaseError() throws SQLException {
        // covers 'catch (SQLException e)' block in executeFetch()

        // simulate a catastrophic database failure by dropping the table
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DROP TABLE turn_order_tiles");
        }

        // now the fetchAll() SQL query will crash
        RuntimeException exception = assertThrows(RuntimeException.class, () -> dao.fetchAll());

        assertTrue(exception.getMessage().contains("error: the database cannot be read"));
        assertNotNull(exception.getCause());
    }

}