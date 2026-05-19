package com.nerdflix.dao;

import com.nerdflix.model.Serie;

import java.sql.SQLException;

public interface ISerieDao {

    /**
     * Persisteix una sèrie i totes les seves temporades en una única transacció.
     * Si qualsevol inserció falla, es fa rollback de tota l'operació.
     *
     * @param serie objecte sèrie amb la llista de temporades ja relacionades
     * @throws SQLException si es produeix un error de BD durant la inserció
     */
    void saveCompleteSerie(Serie serie) throws SQLException;
}
