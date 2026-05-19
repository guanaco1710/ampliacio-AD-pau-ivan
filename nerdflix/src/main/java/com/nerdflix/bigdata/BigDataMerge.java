package com.nerdflix.bigdata;

import com.nerdflix.constants.AppConstants;

public class BigDataMerge {

    public static void run() {
        try {
            GeneradorCSV.generar(AppConstants.DATA_DIR);
            MergeJoin.executar(
                AppConstants.DATA_DIR + "/series_metadata.csv",
                AppConstants.DATA_DIR + "/series_estadistiques.csv",
                AppConstants.DATA_DIR + "/informe_series.csv",
                AppConstants.DATA_DIR + "/log_errores.txt"
            );
        } catch (Exception e) {
            System.err.println("Error al Merge Join: " + e.getMessage());
        }
    }
}
