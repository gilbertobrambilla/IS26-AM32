package it.polimi.ingsw.am32.model.dao;

import it.polimi.ingsw.am32.dao.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // resetting the Singleton instance via Reflection to ensure test isolation.
        Field instanceField = DatabaseConnection.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    @AfterEach
    void tearDown() {
        // cleanup: ensure the connection is closed after each test
        try {
            DatabaseConnection db = DatabaseConnection.getInstance();
            db.closeConnection();
        } catch (RuntimeException e) {
            // Ignore exception if the DB was unreachable during the test
        }
    }

    @Test
    void getInstanceShouldReturnSingletonInstance() {
        // covers getInstance() method and verifies the Singleton pattern
        DatabaseConnection instance1 = DatabaseConnection.getInstance();
        DatabaseConnection instance2 = DatabaseConnection.getInstance();

        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }

    @Test
    void getConnectionShouldReturnOpenConnection() throws SQLException {
        // covers getConnection() method
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();

        assertNotNull(conn);
        assertFalse(conn.isClosed());
    }

    @Test
    void closeConnectionShouldProperlyCloseIt() throws SQLException {
        // covers closeConnection() method
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection conn = db.getConnection();

        assertFalse(conn.isClosed());

        db.closeConnection();

        assertTrue(conn.isClosed());
    }

    @Test
    void getConnectionShouldReconnectIfClosed() throws SQLException {
        // covers the branch 'if (this.connection == null || this.connection.isClosed())'
        DatabaseConnection db = DatabaseConnection.getInstance();
        Connection firstConn = db.getConnection();

        db.closeConnection();
        assertTrue(firstConn.isClosed());

        // request a connection again
        Connection secondConn = db.getConnection();

        assertNotNull(secondConn);
        assertFalse(secondConn.isClosed());
        assertNotSame(firstConn, secondConn);
    }

    @Test
    void getConnectionShouldThrowExceptionOnInvalidDatabaseUrl() throws Exception {
        // covers the 'catch (SQLException e)' block inside the connect() method
        DatabaseConnection db = DatabaseConnection.getInstance();

        // close the valid connection that was just created
        db.closeConnection();

        // use Reflection to inject a fake URL and break the database configuration
        Field urlField = DatabaseConnection.class.getDeclaredField("url");
        urlField.setAccessible(true);
        urlField.set(db, "jdbc:h2:mem:fakedb;INVALID_PARAM=ERROR"); // an URL that will cause H2 to fail

        // when requesting the connection, it will try to reconnect using the broken URL
        RuntimeException exception = assertThrows(RuntimeException.class, () -> db.getConnection());

        // verify that the exact custom exception message is thrown
        assertTrue(exception.getMessage().contains("database connection cannot be established"));
    }

}
