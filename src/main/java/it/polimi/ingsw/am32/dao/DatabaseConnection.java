package it.polimi.ingsw.am32.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private String url;
    private String user;
    private String password;

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

            connect();

        } catch (IOException e) {
            throw new RuntimeException("database configuration cannot be read", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
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

    private void connect() {
        try {
            this.connection = DriverManager.getConnection(this.url, this.user, this.password);
        } catch (SQLException e) {
            throw new RuntimeException("database connection cannot be established", e);
        }
    }

    public synchronized Connection getConnection() {
        // connection is checked for validity
        try {
            if (this.connection == null || this.connection.isClosed()) {
                // connection is re-established if closed
                connect();
            }
        } catch (SQLException e) {
            throw new RuntimeException("connection status cannot be verified", e);
        }

        return this.connection;
    }

    public synchronized void closeConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
