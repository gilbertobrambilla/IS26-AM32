package it.polimi.ingsw.am32.model.dao;

import it.polimi.ingsw.am32.model.dto.setup.BuildingCardDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BuildingCardDAO implements DAO<BuildingCardDTO> {
    private final Connection connection;

    public BuildingCardDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<BuildingCardDTO> fetchAll() {
        List<BuildingCardDTO> dtoList = new ArrayList<>();

        String query = "SELECT * FROM building_cards";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement(query);
            rs = stmt.executeQuery();

            // the result set is iterated row by row
            while (rs.next()) {
                String id = rs.getString("id");
                int era = rs.getInt("era");
                int basePrestige = rs.getInt("base_prestige");
                int cost = rs.getInt("cost");
                String effectCode = rs.getString("effect_code");
                String effectDetails = rs.getString("effect_params");

                BuildingCardDTO dto = new BuildingCardDTO(id, era, basePrestige, cost, effectCode, effectDetails);
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
