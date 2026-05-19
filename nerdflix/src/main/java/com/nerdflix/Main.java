package com.nerdflix;

import com.nerdflix.bigdata.BigDataMerge;
import com.nerdflix.model.Serie;
import com.nerdflix.model.Temporada;
import com.nerdflix.service.SerieService;
import com.nerdflix.stress.Seeder;
import com.nerdflix.stress.Benchmark;

public class Main {
    public static void main(String[] args) {

        // TICKET #1 — BIG DATA MERGE
        // BigDataMerge.run();

        // TICKET #3 — STRESS TEST
        try {
            Seeder.run();
            Benchmark.run();
        } catch (Exception e) {
            System.err.println("Error stress test: " + e.getMessage());
        }

        Serie serie = new Serie("House of Cards", "Thriller Politic");

        Temporada temporada1 = new Temporada(1, 2013);
        Temporada temporada2 = new Temporada(2, 2014);
        Temporada temporada3 = new Temporada(3, 2015);

        serie.addTemporada(temporada1);
        serie.addTemporada(temporada2);
        serie.addTemporada(temporada3);

        temporada1.setSerie(serie);
        temporada2.setSerie(serie);
        temporada3.setSerie(serie);

        // SerieService.saveWithJDBC(serie);
        // SerieService.saveWithHibernate(serie);
        // SerieService.queryWithHQL();
        SerieService.queryWithCriteria();
    }
}
