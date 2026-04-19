package it.polimi.ingsw.am32.dao;

import it.polimi.ingsw.am32.dto.setup.OfferTileDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OfferTileDAO implements DAO<OfferTileDTO> {
    private final Connection connection;

    public OfferTileDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<OfferTileDTO> fetchAll() {
        String query = "SELECT * FROM offer_tiles";
        return executeFetch(query, null);
    }

    public List<OfferTileDTO> fetchAll(int numOfPlayers) {
        if (numOfPlayers < 2 || numOfPlayers > 5) {
            throw new IllegalArgumentException("numOfPlayers must be between 2 and 5");
        }

        String query = "SELECT * FROM offer_tiles WHERE min_players <= ?";
        return executeFetch(query, numOfPlayers);
    }

    private List<OfferTileDTO> executeFetch(String query, Integer numOfPlayers) {
        List<OfferTileDTO> dtoList = new ArrayList<>();

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
                int minPlayers = rs.getInt("min_players");
                int actDrawsUpper = rs.getInt("act_draws_upper");
                int actDrawsLower = rs.getInt("act_draws_lower");
                int actFoodBonus = rs.getInt("act_food_bonus");

                OfferTileDTO dto = new OfferTileDTO(id, minPlayers, actDrawsUpper, actDrawsLower, actFoodBonus);
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
