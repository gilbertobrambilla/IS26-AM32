package it.polimi.ingsw.am32.model.dao;

import it.polimi.ingsw.am32.dao.CharacterCardDAO;
import it.polimi.ingsw.am32.dto.setup.CharacterCardDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CharacterCardDAOTest {

    private Connection testConnection;
    private CharacterCardDAO dao;

    @BeforeEach
    void setUp() throws SQLException {
        // create H2 in-memory database connection
        testConnection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");

        // set up the table and mock data for the test
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("CREATE TABLE character_cards (" +
                    "id VARCHAR(255) PRIMARY KEY, " +
                    "era INT, " +
                    "base_prestige INT, " +
                    "min_players INT, " +
                    "character_type VARCHAR(255), " +
                    "character_params VARCHAR(255))");

            // insert a couple of dummy character cards
            stmt.execute("INSERT INTO character_cards VALUES ('char_01', 1, 0, 2, 'SHAMAN', '{}')");
            stmt.execute("INSERT INTO character_cards VALUES ('char_02', 2, 2, 4, 'BUILDER', '{}')");
            stmt.execute("INSERT INTO character_cards VALUES ('char_03', 1, 1, 3, 'HUNTER', '{}')");
        }

        // initialize the DAO with the test connection
        dao = new CharacterCardDAO(testConnection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // clean up the DB: destroy the table at the end of each test
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS character_cards");
        }
        testConnection.close();
    }

    @Test
    void fetchAllShouldReturnAllCards() {
        // covers the first fetchAll() method
        List<CharacterCardDTO> cards = dao.fetchAll();

        assertEquals(3, cards.size());
    }

    @Test
    void fetchAllWithPlayersShouldFilterCorrectly() {
        // covers the branch of 'if (numOfPlayers != null)' is true in executeFetch()
        List<CharacterCardDTO> cards = dao.fetchAll(3);

        assertEquals(2, cards.size());

        // exact test of the filter
        boolean hasChar01 = cards.stream().anyMatch(c -> c.getId().equals("char_01"));
        boolean hasChar03 = cards.stream().anyMatch(c -> c.getId().equals("char_03"));
        assertTrue(hasChar01 && hasChar03);
    }

    @Test
    void fetchAllWithInvalidPlayersShouldThrowException() {
        // covers the branches of the 'if (players < 2 || players > 5)' block
        assertThrows(IllegalArgumentException.class, () -> dao.fetchAll(1));
        assertThrows(IllegalArgumentException.class, () -> dao.fetchAll(6));
    }

    @Test
    void executeFetchShouldThrowRuntimeExceptionOnDatabaseError() throws SQLException {
        // covers 'catch (SQLException e)' block in executeFetch()

        // simulate a catastrophic database failure by dropping the table
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DROP TABLE character_cards");
        }

        // now the fetchAll() SQL query will crash
        RuntimeException exception = assertThrows(RuntimeException.class, () -> dao.fetchAll());

        assertTrue(exception.getMessage().contains("error: the database cannot be read"));
        assertNotNull(exception.getCause());
    }
}
