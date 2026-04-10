package it.polimi.ingsw.am32.model.dao;

import it.polimi.ingsw.am32.model.dto.setup.CharacterCardDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CharacterCardDAO implements DAO<CharacterCardDTO> {
    private final Connection connection;

    public CharacterCardDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<CharacterCardDTO> fetchAll() {
        String sql = "SELECT * FROM character_cards";
        return executeFetch(sql, null);
    }

    public List<CharacterCardDTO> fetchAll(int numOfPlayers) {
        if (numOfPlayers < 2 || numOfPlayers > 5) {
            throw new IllegalArgumentException("numOfPlayers must be between 2 and 5");
        }

        String sql = "SELECT * FROM character_cards WHERE min_players <= ?";
        return executeFetch(sql, numOfPlayers);
    }

    private List<CharacterCardDTO> executeFetch(String query, Integer numOfPlayers) {
        List<CharacterCardDTO> dtoList = new ArrayList<>();

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
                int era = rs.getInt("era");
                int basePrestige = rs.getInt("base_prestige");
                int minPlayers = rs.getInt("min_players");
                String characterType = rs.getString("character_type");
                String characterParams = rs.getString("character_params");

                CharacterCardDTO dto = new CharacterCardDTO(id, era, basePrestige, minPlayers, characterType, characterParams);
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
