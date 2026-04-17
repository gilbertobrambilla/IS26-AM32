package it.polimi.ingsw.am32.model.dao;

import it.polimi.ingsw.am32.dao.EventCardDAO;
import it.polimi.ingsw.am32.dto.setup.EventCardDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventCardDAOTest {

    private Connection testConnection;
    private EventCardDAO dao;

    @BeforeEach
    void setUp() throws SQLException {
        // create H2 in-memory database connection
        testConnection = DriverManager.getConnection("jdbc:h2:mem:testdb_event;DB_CLOSE_DELAY=-1", "sa", "");

        // set up the table and mock data based on EventCardDAO columns
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("CREATE TABLE event_cards (" +
                    "id VARCHAR(255) PRIMARY KEY, " +
                    "era INT, " +
                    "event_type VARCHAR(255), " +
                    "event_params VARCHAR(255))");

            // insert mock data
            stmt.execute("INSERT INTO event_cards VALUES ('event_01', 1, 'CAVE_PAINTINGS', '{\"cave_prestige_bonus\": 1}')");
            stmt.execute("INSERT INTO event_cards VALUES ('event_02', 2, 'HUNT', '{\"hunt_food_bonus\": 1}')");
            stmt.execute("INSERT INTO event_cards VALUES ('event_03', 3, 'SHAMANIC_RITUAL', '{}')");
        }

        // initialize the DAO
        dao = new EventCardDAO(testConnection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // clean up the DB: destroy the table at the end of each test
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS event_cards");
        }
        testConnection.close();
    }

    @Test
    void fetchAllShouldReturnAllCards() {
        // covers the fetchAll() method and verifies the mapping of the ResultSet to DTOs
        List<EventCardDTO> cards = dao.fetchAll();

        assertEquals(3, cards.size());

        // verify specific mapping for one card
        EventCardDTO event01 = cards.stream()
                .filter(c -> c.getId().equals("event_01"))
                .findFirst()
                .orElse(null);

        assertNotNull(event01);
        assertEquals(1, event01.getEra());
        assertEquals("CAVE_PAINTINGS", event01.getEventType());
        assertEquals("{\"cave_prestige_bonus\": 1}", event01.getEventParams());
    }

    @Test
    void fetchAllShouldThrowRuntimeExceptionOnDatabaseError() throws SQLException {
        // covers the 'catch (SQLException e)' block in fetchAll()

        // simulate a catastrophic database failure by dropping the table
        try (Statement stmt = testConnection.createStatement()) {
            stmt.execute("DROP TABLE event_cards");
        }

        // now the fetchAll() SQL query will crash
        RuntimeException exception = assertThrows(RuntimeException.class, () -> dao.fetchAll());

        assertTrue(exception.getMessage().contains("error: the database cannot be read"));
        assertNotNull(exception.getCause());
    }
}
