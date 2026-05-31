package it.polimi.ingsw.am32.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.am32.log.LogLevel;
import it.polimi.ingsw.am32.log.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;

    private final String url;
    private final String user;
    private final String password;

    private DatabaseConnection() {
        ObjectMapper mapper = new ObjectMapper();
        InputStream input = null;

        try {
            input = getClass().getClassLoader().getResourceAsStream("database.json");
            if (input == null) {
                throw new RuntimeException("database.json file is not found");
            }

            JsonNode rootNode = mapper.readTree(input);

            this.url = rootNode.get("url").asText();
            this.user = rootNode.get("user").asText();
            this.password = rootNode.get("password").asText();

        } catch (IOException e) {
            throw new RuntimeException("database configuration cannot be read", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    LogManager.getInstance().log(LogLevel.WARNING, "[DATABASE] Database configuration stream could not be closed.", e);
                }
            }
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        // instance is created if it does not exist
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        // new connection is established
        try {
            return DriverManager.getConnection(this.url, this.user, this.password);
        } catch (SQLException e) {
            throw new RuntimeException("database connection cannot be established", e);
        }
    }
}
