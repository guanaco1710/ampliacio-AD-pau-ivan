package com.nerdflix.bigdata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Merge Join O(n+m) sobre dos fitxers CSV de Nerdflix ordenats per ID_SERIE.
 *
 * Creua el catàleg de metadades (titol, genere) amb les estadístiques
 * d'audiència (visualitzacions, puntuació) per generar l'informe complet.
 *
 * Precondició: ambdós fitxers han d'estar ordenats ascendentment per ID_SERIE.
 * Els registres sense parella es consideren "orfes" i s'escriuen a log_errores.txt.
 */
public class MergeJoin {

    private static final String SEP = ",";

    /**
     * Punt d'entrada principal. Obre els fitxers i orquestra el procés.
     *
     * @param pathMetadata      ruta al fitxer series_metadata.csv (ordenat per ID_SERIE)
     * @param pathEstadistiques ruta al fitxer series_estadistiques.csv (ordenat per ID_SERIE)
     * @param pathResultat      ruta de sortida per a l'informe complet
     * @param pathLog           ruta de sortida per als registres orfes
     * @throws IOException si algun fitxer no es pot llegir o escriure
     */
    public static void executar(String pathMetadata, String pathEstadistiques,
                                String pathResultat, String pathLog) throws IOException {

        try (BufferedReader brMetadata      = new BufferedReader(new FileReader(pathMetadata));
             BufferedReader brEstadistiques = new BufferedReader(new FileReader(pathEstadistiques));
             BufferedWriter bwResultat      = new BufferedWriter(new FileWriter(pathResultat));
             BufferedWriter bwLog           = new BufferedWriter(new FileWriter(pathLog))) {

            saltarCapcaleres(brMetadata, brEstadistiques);
            escriureCapcaleres(bwResultat, bwLog);

            int[] comptadors = mergeFitxers(brMetadata, brEstadistiques, bwResultat, bwLog);

            imprimirResum(comptadors[0], comptadors[1], pathResultat, pathLog);
        }
    }

    private static void saltarCapcaleres(BufferedReader brMetadata,
                                          BufferedReader brEstadistiques) throws IOException {
        brMetadata.readLine();
        brEstadistiques.readLine();
    }

    private static void escriureCapcaleres(BufferedWriter bwResultat,
                                            BufferedWriter bwLog) throws IOException {
        bwResultat.write("ID_SERIE,TITOL,GENERE,VISUALITZACIONS,PUNTUACIO");
        bwResultat.newLine();
        bwLog.write("=== LOG D'ERRORS — REGISTRES ORFES ===");
        bwLog.newLine();
    }

    /**
     * Bucle principal del Merge Join. Avança pels dos fitxers en paral·lel
     * sense carregar-los mai sencers a memòria.
     *
     * @return array de dos enters: [coincidències, orfes]
     */
    private static int[] mergeFitxers(BufferedReader brMetadata,
                                       BufferedReader brEstadistiques,
                                       BufferedWriter bwResultat,
                                       BufferedWriter bwLog) throws IOException {
        int coincidencies = 0;
        int orfes         = 0;

        String lineaMetadata      = brMetadata.readLine();
        String lineaEstadistiques = brEstadistiques.readLine();

        while (lineaMetadata != null && lineaEstadistiques != null) {
            String[] metadata      = lineaMetadata.split(SEP);
            String[] estadistiques = lineaEstadistiques.split(SEP);

            int cmp = extraureClau(metadata).compareTo(extraureClau(estadistiques));

            if (cmp == 0) {
                escriureCoincidencia(metadata, estadistiques, bwResultat);
                coincidencies++;
                lineaMetadata      = brMetadata.readLine();
                lineaEstadistiques = brEstadistiques.readLine();
            } else if (cmp < 0) {
                escriureOrfe("[SENSE-ESTADISTIQUES] ", lineaMetadata, bwLog);
                orfes++;
                lineaMetadata = brMetadata.readLine();
            } else {
                escriureOrfe("[SENSE-METADATA]      ", lineaEstadistiques, bwLog);
                orfes++;
                lineaEstadistiques = brEstadistiques.readLine();
            }
        }

        orfes += buidarRestes(lineaMetadata,      brMetadata,      "[SENSE-ESTADISTIQUES] ", bwLog);
        orfes += buidarRestes(lineaEstadistiques, brEstadistiques, "[SENSE-METADATA]      ", bwLog);

        return new int[]{coincidencies, orfes};
    }

    /**
     * Extreu la clau de join (ID_SERIE) d'una fila CSV.
     *
     * @param camps array de camps d'una línia CSV
     * @return ID_SERIE com a String
     */
    private static String extraureClau(String[] camps) {
        return camps[0];
    }

    /**
     * Combina metadata i estadístiques i escriu la línia a l'informe.
     *
     * @param metadata      camps de la fila de metadades (ID_SERIE, TITOL, GENERE)
     * @param estadistiques camps de la fila d'estadístiques (ID_SERIE, VISUALITZACIONS, PUNTUACIO)
     * @param bwResultat    escriptor del fitxer de resultat
     * @throws IOException si no es pot escriure
     */
    private static void escriureCoincidencia(String[] metadata, String[] estadistiques,
                                              BufferedWriter bwResultat) throws IOException {
        bwResultat.write(metadata[0] + SEP + metadata[1] + SEP + metadata[2] + SEP +
                         estadistiques[1] + SEP + estadistiques[2]);
        bwResultat.newLine();
    }

    /**
     * Escriu un registre orfe al log d'errors amb el seu tipus.
     *
     * @param tipus etiqueta del tipus d'orfe
     * @param linia línia CSV original
     * @param bwLog escriptor del log d'errors
     * @throws IOException si no es pot escriure
     */
    private static void escriureOrfe(String tipus, String linia,
                                      BufferedWriter bwLog) throws IOException {
        bwLog.write(tipus + linia);
        bwLog.newLine();
    }

    /**
     * Buida les línies restants d'un fitxer quan l'altre ja s'ha esgotat.
     *
     * @param primeraLinia línia ja llegida del BufferedReader (pot ser null)
     * @param br           lector del fitxer a buidar
     * @param tipus        etiqueta del tipus d'orfe
     * @param bwLog        escriptor del log d'errors
     * @return nombre de registres orfes afegits
     * @throws IOException si no es pot llegir o escriure
     */
    private static int buidarRestes(String primeraLinia, BufferedReader br, String tipus,
                                     BufferedWriter bwLog) throws IOException {
        int count = 0;
        String linia = primeraLinia;
        while (linia != null) {
            escriureOrfe(tipus, linia, bwLog);
            count++;
            linia = br.readLine();
        }
        return count;
    }

    private static void imprimirResum(int coincidencies, int orfes,
                                       String pathResultat, String pathLog) {
        System.out.println("=== MERGE JOIN COMPLETAT ===");
        System.out.println("Series creuades: " + coincidencies);
        System.out.println("Registres orfes: " + orfes);
        System.out.println("Informe → " + pathResultat);
        System.out.println("Log orfes → " + pathLog);
    }
}
