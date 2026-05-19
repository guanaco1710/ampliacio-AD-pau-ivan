package com.nerdflix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GeneradorCSV {

    public static void generar(String dirDades) throws IOException {
        new File(dirDades).mkdirs();
        generarSeriesMetadata(dirDades + "/series_metadata.csv");
        generarSeriesEstadistiques(dirDades + "/series_estadistiques.csv");
        System.out.println("Fitxers CSV generats a: " + dirDades);
    }

    private static void generarSeriesMetadata(String path) throws IOException {
        // Sorted ascending by ID_SERIE — required for Merge Join
        String[][] dades = {
            {"S001", "Breaking Bad",       "Drama"},
            {"S002", "Dark",               "Ciencia Ficcio"},
            {"S003", "House of Cards",     "Thriller Politic"},
            {"S005", "Peaky Blinders",     "Drama"},
            {"S006", "Stranger Things",    "Terror"},
            {"S008", "The Crown",          "Drama Historic"},
            {"S009", "The Witcher",        "Fantasia"},
        };

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            bw.write("ID_SERIE,TITOL,GENERE");
            bw.newLine();
            for (String[] fila : dades) {
                bw.write(String.join(",", fila));
                bw.newLine();
            }
        }
    }

    private static void generarSeriesEstadistiques(String path) throws IOException {
        // Sorted ascending by ID_SERIE — required for Merge Join
        // Orphans marked intentionally to test log_errores.txt output
        String[][] dades = {
            {"S001", "4820000", "9.5"},
            {"S002", "1200000", "8.8"},
            {"S004", "350000",  "7.2"}, // orfe: S004 no existeix a metadata
            {"S005", "2100000", "8.9"},
            {"S006", "9500000", "8.7"},
            {"S007", "180000",  "6.5"}, // orfe: S007 no existeix a metadata
            {"S009", "3300000", "8.2"},
        };

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            bw.write("ID_SERIE,VISUALITZACIONS,PUNTUACIO");
            bw.newLine();
            for (String[] fila : dades) {
                bw.write(String.join(",", fila));
                bw.newLine();
            }
        }
    }
}
