package com.nerdflix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GestorJDBC {

    public Connection conectar() throws SQLException {
        String url = "jdbc:sqlite:bbdd/nerdflix.db";
        return DriverManager.getConnection(url);

    }

    public void guardarSerieCompleta(Serie serie) throws SQLException {
        Connection conn = null;
        PreparedStatement psSerie = null;
        PreparedStatement psTemp = null;
        ResultSet rsKeys = null;

        try {
            conn = this.conectar();
            conn.setAutoCommit(false); // 1. INICIAR TRANSACCIÓ MANUAL

            // --- PAS A: INSERTAR LA SÈRIE ---

            String sqlSerie = "INSERT INTO Serie (titol, genere) VALUES (?, ?)";

            // Necessitem RETURN_GENERATED_KEYS per saber quin ID li ha donat la BD

            psSerie = conn.prepareStatement(sqlSerie, Statement.RETURN_GENERATED_KEYS);

            // Aquí "buidem" el POJO en la sentència SQL
            psSerie.setString(1, serie.getTitol());
            psSerie.setString(2, serie.getGenere());
            psSerie.executeUpdate();

            // Recuperar l'ID generat (ex: ID=1 per a "Stranger Things")
            rsKeys = psSerie.getGeneratedKeys();
            int serieId = -1;
            if (rsKeys.next()) {
                serieId = rsKeys.getInt(1);
            }

            // --- PAS B: INSERTAR LES TEMPORADES ---
            String sqlTemp = "INSERT INTO Temporada (numero, any_estrena, serie_id) VALUES (?, ?, ?)";
            psTemp = conn.prepareStatement(sqlTemp);

            for (Temporada t : serie.getTemporades()) {
                // psTemp - SQL
                // t - temporada actual
                psTemp.setInt(1, t.getNumero());
                psTemp.setInt(2, t.getAnyEstrena());
                psTemp.setInt(3, serieId); // AQUI POSEM LA FK MANUALMENT!
                psTemp.executeUpdate();
            }

            conn.commit(); // 2. CONFIRMAR SI TOT HA ANAT BÉ
            System.out.println("Èxit: Sèrie guardada amb JDBC!");

        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback(); // DESFER SI HI HA ERROR
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            // TANCAR RECURSOS MANUALMENT (IMPRESCINDIBLE!!)
            //    +rs (ResultSet): Manté obert un cursor per a les dades.
            //    +ps (PreparedStatement): Reserva espai per a SQL i el seu
            //                            pla d'execució.
            //    +conn (Connection): Canal entre programa i base de dades.
            try {
                if (rsKeys != null) {
                    rsKeys.close();
                }
                if (psSerie != null) {
                    psSerie.close();
                }
                if (psTemp != null) {
                    psTemp.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}