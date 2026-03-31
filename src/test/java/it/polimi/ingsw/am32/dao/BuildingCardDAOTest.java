package it.polimi.ingsw.am32.dao;

import it.polimi.ingsw.am32.dto.setup.BuildingCardDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuildingCardDAOTest {

    private Connection testConnection;
    private BuildingCardDAO dao;

    @BeforeEach
    void setUp() throws SQLException {
        // create H2 in-memory database connection
        testConnection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");

        // set up the table and mock data for the test
        try (Statement stmt = testConnection.createStatement()) {
            // creating the table with the exact columns expected by BuildingCardDAO
            stmt.execute("CREATE TABLE building_cards (" +
                    "id VARCHAR(255) PRIMARY KEY, " +
                    "era INT, " +
                    "base_prestige INT, " +
                    "cost INT, " +
                    "effect_code VARCHAR(255), " +
                    "effect_params VARCHAR(255))");

            // insert a couple of dummy building cards
            stmt.execute("INSERT INTO building_cards VALUES ('bld_01', 1, 2, 3, 'EFFECT_A', '{}')");
            stmt.execute("INSERT INTO building_cards VALUES ('bld_02', 2, 5, 4, 'EFFECT_B', '{}')");
        }

        // initialize the DAO with the test connection
        dao = new BuildingCardDAO(testConnection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // clean up the DB: destroy the table at the end of each test
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS building_cards");
        }
        testConnection.close();
    }

    @Test
    void fetchAllShouldReturnAllCards() {
        // execute the real query "SELECT * FROM building_cards"
        List<BuildingCardDTO> cards = dao.fetchAll();

        assertEquals(2, cards.size());

        // verify that the mapping from database columns to DTO fields is correct
        BuildingCardDTO firstCard = cards.stream()
                .filter(c -> c.getId().equals("bld_01"))
                .findFirst()
                .orElse(null);

        assertNotNull(firstCard);
        assertEquals(1, firstCard.getEra());
        assertEquals(2, firstCard.getBasePrestige());
        assertEquals(3, firstCard.getCost());
        assertEquals("EFFECT_A", firstCard.getEffectCode());
        assertEquals("{}", firstCard.getEffectParams());
    }

    @Test
    void fetchAllShouldThrowRuntimeExceptionOnDatabaseError() throws SQLException {
        // simulate a catastrophic database failure by dropping the table
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DROP TABLE building_cards");
        }

        // now the fetchAll() SQL query will crash
        RuntimeException exception = assertThrows(RuntimeException.class, () -> dao.fetchAll());

        assertTrue(exception.getMessage().contains("error: the database cannot be read"));
        assertNotNull(exception.getCause());
    }
}