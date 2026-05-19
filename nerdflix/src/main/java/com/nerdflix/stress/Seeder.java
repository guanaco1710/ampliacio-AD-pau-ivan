package com.nerdflix.stress;

import com.nerdflix.constants.AppConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Seeder {

    static final int    TOTAL_REGISTRES = 100_000;
    static final String TITOL_PREFIX    = "Serie_";

    private static final int BATCH_SIZE = 1_000;
    private static final String[] GENRES = {
        "Drama", "Comèdia", "Terror", "Ciencia Ficcio",
        "Thriller", "Fantasia", "Documental", "Animació"
    };

    public static void run() throws SQLException {
        if (jaSeeded()) {
            System.out.println("Seeder: ja hi ha " + TOTAL_REGISTRES + " registres, s'omet.");
            return;
        }

        System.out.println("Iniciant seeder: " + TOTAL_REGISTRES + " registres...");
        long inici = System.currentTimeMillis();

        try (Connection conn = DriverManager.getConnection(AppConstants.DB_URL)) {
            conn.setAutoCommit(false);
            insertSeries(conn);
            conn.commit();
        }

        System.out.println("Seeder completat en " + (System.currentTimeMillis() - inici) + " ms");
    }

    private static boolean jaSeeded() throws SQLException {
        try (Connection conn = DriverManager.getConnection(AppConstants.DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery("SELECT COUNT(*) FROM Serie")) {
            return rs.next() && rs.getInt(1) >= TOTAL_REGISTRES;
        }
    }

    private static void insertSeries(Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(AppConstants.SQL_INSERT_SERIE)) {
            for (int i = 1; i <= TOTAL_REGISTRES; i++) {
                stmt.setString(1, TITOL_PREFIX + String.format("%06d", i));
                stmt.setString(2, GENRES[i % GENRES.length]);
                stmt.addBatch();

                if (i % BATCH_SIZE == 0) {
                    stmt.executeBatch();
                }
            }
            stmt.executeBatch();
        }
    }
}
