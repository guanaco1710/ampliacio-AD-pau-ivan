package com.nerdflix.dao.impl;

import com.nerdflix.constants.AppConstants;
import com.nerdflix.dao.ISerieDao;
import com.nerdflix.model.Serie;
import com.nerdflix.model.Temporada;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class GestorJDBC implements ISerieDao {

    /**
     * Obre i retorna una connexió a la base de dades SQLite.
     *
     * @return connexió activa a la BD
     * @throws SQLException si no es pot establir la connexió
     */
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(AppConstants.DB_URL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCompleteSerie(Serie serie) throws SQLException {
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);
            try {
                int serieId = insertSerie(conn, serie);
                insertTemporades(conn, serie.getTemporades(), serieId);
                conn.commit();
                System.out.println("Èxit: Sèrie guardada amb JDBC!");
            } catch (SQLException e) {
                rollbackSafely(conn);
                throw e;
            }
        }
    }

    private int insertSerie(Connection conn, Serie serie) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                AppConstants.SQL_INSERT_SERIE, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, serie.getTitol());
            stmt.setString(2, serie.getGenere());
            stmt.executeUpdate();
            return extractGeneratedId(stmt);
        }
    }

    private int extractGeneratedId(PreparedStatement stmt) throws SQLException {
        try (ResultSet rsKeys = stmt.getGeneratedKeys()) {
            if (rsKeys.next()) return rsKeys.getInt(1);
            throw new SQLException("No s'ha pogut obtenir l'ID generat");
        }
    }

    private void insertTemporades(Connection conn, List<Temporada> temporades,
                                   int serieId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(AppConstants.SQL_INSERT_TEMPORADA)) {
            for (Temporada temporada : temporades) {
                insertTemporada(stmt, temporada, serieId);
            }
        }
    }

    private void insertTemporada(PreparedStatement stmt, Temporada temporada,
                                  int serieId) throws SQLException {
        stmt.setInt(1, temporada.getNumero());
        stmt.setInt(2, temporada.getAnyEstrena());
        stmt.setInt(3, serieId);
        stmt.executeUpdate();
    }

    private void rollbackSafely(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
