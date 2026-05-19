package com.nerdflix;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        // 0. TICKET #1 — BIG DATA MERGE
        bigDataMerge();

        // Preparem dades (compte amb els duplicats!!)

        Serie newSerie = new Serie("House of Cards", "Thriller Politico");

        Temporada newTemporada1 = new Temporada(1, 2013);
        Temporada newTemporada2 = new Temporada(2, 2014);
        Temporada newTemporada3 = new Temporada(3, 2015);

        // RELACIONEM ELS OBJECTES

        // Afegim Temporada a Serie

        newSerie.addTemporada(newTemporada1);
        newSerie.addTemporada(newTemporada2);
        newSerie.addTemporada(newTemporada3);

        // Afegim Serie a Temporada

        newTemporada1.setSerie(newSerie);
        newTemporada2.setSerie(newSerie);
        newTemporada3.setSerie(newSerie);

        // 1. JDBC (Comentat)
        // guardaAmbJDBC (newSerie);

        // 2. HIBERNATE
        // guardaAmbHibernate(newSerie);

        // 3. CONSULTA AMB HQL
        // consultesHQL();

        // 4. CONSULTA AMB CRITERIA
        consultesCriteria();

    }

    private static void bigDataMerge() {
        String dir = "data";
        try {
            GeneradorCSV.generar(dir);
            MergeJoin.executar(
                dir + "/series_metadata.csv",
                dir + "/series_estadistiques.csv",
                dir + "/informe_series.csv",
                dir + "/log_errores.txt"
            );
        } catch (Exception e) {
            System.err.println("Error al Merge Join: " + e.getMessage());
        }
    }

    private static void guardaAmbJDBC(Serie s) {

        // Guardem a la BBDD via JDBC

        GestorJDBC gestor = new GestorJDBC();

        try {
            gestor.guardarSerieCompleta(s);
            System.out.println("¡ TOT CORRECTE amb JDBC !");
        } catch (Exception e) {
            System.err.println("Error amb JDBC: " + e.getMessage());
        }
    }

    private static void guardaAmbHibernate(Serie s) {

        // 1. CONFIGURACIÓ DE HIBERNATE
        SessionFactory factory = new Configuration().configure().buildSessionFactory();

        // Obtenim la conexió

        Session session = factory.openSession();

        try {

            // 2. INTENTEM GUARDAR (amb transacció)

            Transaction tx = session.beginTransaction();

            System.out.println("Guardant la sèrie amb Hibernate...");

            session.persist(s);

            tx.commit();
            System.out.println("¡Serie guardada amb Hibernate! ID generado: " + s.getId());

        } catch (Exception e) {
            System.err.println("Error guardant amb Hibernate: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Tanquem coses
            session.close();
            factory.close();
        }
    }

    private static void consultesHQL() {

        SessionFactory factory = new Configuration().configure().buildSessionFactory();

        // 1. SessionFactory y Session

        Session session = factory.openSession();

        try {

            // 2. FEM LA CONSULTA

            String hql = "FROM Temporada T WHERE T.serie.id = :serieId ORDER BY T.numero ASC";
            Query<Temporada> query = session.createQuery(hql, Temporada.class);

            // Podem demanar a l'usuari aquest valor a consultar.

            query.setParameter("serieId", 1);

            List<Temporada> temporadas = query.list();

            /*
             * ALTRA MANERA
             *
             * List<Temporada> temporadas = session.createQuery(
             * "FROM Temporada T WHERE T.serie.id = 1 ORDER BY T.numero ASC",
             * Temporada.class)
             * .list();
             * 
             */

            for (Temporada temporada : temporadas) {
                System.out.println("Numero: " + temporada.getNumero());
                System.out.println("Any estrena: " + temporada.getAnyEstrena());
            }

        } catch (Exception e) {
            System.err.println("\n🚨 Error amb la consulta HQL: " + e.getMessage());
            System.err.println("----------------------------------------------------\n");
            e.printStackTrace();
        } finally {
            // Tanquem coses
            session.close();
        }

    }

    private static void consultesCriteria() {

        // Sessió (Com sempre)
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        Session session = factory.openSession();

        try {
            // ANEM PER PARTS
            // A. Constructor
                CriteriaBuilder cb = session.getCriteriaBuilder();
            // B. Objecte "Query" de Criteria
                CriteriaQuery<Temporada> cTemp = cb.createQuery(Temporada.class);
            // C. El "Root" (arrel)
                Root<Temporada> root = cTemp.from(Temporada.class);
            // D. Construïm la consulta: SELECT * FROM Temporada WHERE serie.ide = 1 ORDER BY...
                cTemp.select(root)
                      .where(cb.equal(root.get("serie").get("id"), 1))
                      .orderBy(cb.asc(root.get("numero")));

            // D. Bucle per a mostrar resultats

            List<Temporada> resultats = session.createQuery(cTemp).getResultList();

            System.out.println("Temporades trobades (Criteria): " +resultats.size());

            for (Temporada t: resultats){
                System.out.println("Série ID: " + t.getSerie().getId() + " | Temporada: " + t.getNumero());
            }

        } catch (Exception e) {
            System.err.println("\n🚨 Error amb Criteria: " + e.getMessage());
            System.err.println("---------------------------------------------n");
            e.printStackTrace();
        } finally {
            session.close();
            factory.close();
        }
    }
}
