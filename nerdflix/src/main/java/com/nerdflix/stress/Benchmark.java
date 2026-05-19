package com.nerdflix.stress;

import com.nerdflix.constants.AppConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Benchmark {

    private static final String TITOL_CERCA     = Seeder.TITOL_PREFIX + String.format("%06d", Seeder.TOTAL_REGISTRES / 2);
    private static final String SQL_CERCA        = "SELECT * FROM Serie WHERE titol = ?";
    private static final String SQL_CREAR_INDEX  = "CREATE INDEX IF NOT EXISTS idx_serie_titol ON Serie(titol)";
    private static final String SQL_ELIMINAR_INDEX = "DROP INDEX IF EXISTS idx_serie_titol";

    public static void run() throws SQLException {
        System.out.println("\n=== BENCHMARK (cerca per: '" + TITOL_CERCA + "') ===");

        try (Connection conn = DriverManager.getConnection(AppConstants.DB_URL)) {
            eliminarIndex(conn);

            long sensIndex = mesurarCerca(conn);
            System.out.println("Sense index: " + sensIndex + " ms");

            crearIndex(conn);

            long ambIndex = mesurarCerca(conn);
            System.out.println("Amb index:   " + ambIndex + " ms");

            imprimirComparativa(sensIndex, ambIndex);
        }
    }

    private static long mesurarCerca(Connection conn) throws SQLException {
        long inici = System.currentTimeMillis();
        try (PreparedStatement stmt = conn.prepareStatement(SQL_CERCA)) {
            stmt.setString(1, TITOL_CERCA);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rs.getString("titol");
            }
        }
        return System.currentTimeMillis() - inici;
    }

    private static void crearIndex(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(SQL_CREAR_INDEX);
            System.out.println("Index creat: idx_serie_titol");
        }
    }

    private static void eliminarIndex(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(SQL_ELIMINAR_INDEX);
        }
    }

    private static void imprimirComparativa(long sensIndex, long ambIndex) {
        long millora = sensIndex - ambIndex;
        System.out.println("Millora:     " + millora + " ms (" +
                (sensIndex > 0 ? (millora * 100 / sensIndex) : 0) + "% més ràpid)");
    }
}
