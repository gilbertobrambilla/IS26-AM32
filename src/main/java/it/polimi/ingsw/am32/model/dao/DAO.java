package it.polimi.ingsw.am32.model.dao;

import java.util.List;

public interface DAO<T> {
    List<T> fetchAll();
}
