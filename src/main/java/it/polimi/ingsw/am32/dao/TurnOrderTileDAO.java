package it.polimi.ingsw.am32.dao;

import it.polimi.ingsw.am32.dto.setup.TurnOrderTileDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TurnOrderTileDAO implements DAO<TurnOrderTileDTO> {
    private final Connection connection;

    public TurnOrderTileDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<TurnOrderTileDTO> fetchAll() {
        String sql = "SELECT * FROM turn_order_tiles";
        return executeFetch(sql, null);
    }

    public TurnOrderTileDTO fetch(int numOfPlayers) {
        if (numOfPlayers < 2 || numOfPlayers > 5) {
            throw new IllegalArgumentException("numOfPlayers must be between 2 and 5");
        }

        String sql = "SELECT * FROM turn_order_tiles WHERE required_players = ?";
        return executeFetch(sql, numOfPlayers).getFirst();
    }

    private List<TurnOrderTileDTO> executeFetch(String query, Integer numOfPlayers) {
        List<TurnOrderTileDTO> dtoList = new ArrayList<>();

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement(query);
            if (numOfPlayers != null) {
                stmt.setInt(1, numOfPlayers);
            }

            rs = stmt.executeQuery();

            // the result set is iterated row by row
            while (rs.next()) {
                String id = rs.getString("id");
                int requiredPlayers = rs.getInt("required_players");
                String foodModifiers = rs.getString("food_modifiers");
                String prestigeModifiers = rs.getString("prestige_modifiers");

                TurnOrderTileDTO dto = new TurnOrderTileDTO(id, requiredPlayers, foodModifiers, prestigeModifiers);
                dtoList.add(dto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("error: the database cannot be read", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return dtoList;
    }
}
