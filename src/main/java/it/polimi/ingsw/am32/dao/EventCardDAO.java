package it.polimi.ingsw.am32.dao;

import it.polimi.ingsw.am32.dto.setup.EventCardDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventCardDAO implements DAO<EventCardDTO> {
    private final Connection connection;

    public EventCardDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<EventCardDTO> fetchAll() {
        List<EventCardDTO> dtoList = new ArrayList<>();

        String query = "SELECT * FROM event_cards";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement(query);
            rs = stmt.executeQuery();

            // the result set is iterated row by row
            while (rs.next()) {
                String id = rs.getString("id");
                int era = rs.getInt("era");
                String eventType = rs.getString("event_type");
                String eventDetails = rs.getString("event_params");

                EventCardDTO dto = new EventCardDTO(id, era, eventType, eventDetails);
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
