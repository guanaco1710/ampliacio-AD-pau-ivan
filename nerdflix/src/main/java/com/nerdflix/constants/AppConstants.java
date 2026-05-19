package com.nerdflix.constants;

public final class AppConstants {

    private AppConstants() {}

    public static final String DB_URL = "jdbc:sqlite:bbdd/nerdflix.db";
    public static final String DATA_DIR = "data";
    public static final int    DEFAULT_SERIE_ID = 1;

    public static final String SQL_INSERT_SERIE =
            "INSERT INTO Serie (titol, genere) VALUES (?, ?)";

    public static final String SQL_INSERT_TEMPORADA =
            "INSERT INTO Temporada (numero, any_estrena, serie_id) VALUES (?, ?, ?)";

    public static final String HQL_TEMPORADES_BY_SERIE =
            "FROM Temporada T WHERE T.serie.id = :serieId ORDER BY T.numero ASC";
}
