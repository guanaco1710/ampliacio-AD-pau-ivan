package com.nerdflix.service;

import com.nerdflix.constants.AppConstants;
import com.nerdflix.dao.ISerieDao;
import com.nerdflix.dao.impl.GestorJDBC;
import com.nerdflix.model.Serie;
import com.nerdflix.model.Temporada;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class SerieService {

    public static void saveWithJDBC(Serie serie) {
        ISerieDao dao = new GestorJDBC();
        try {
            dao.saveCompleteSerie(serie);
        } catch (Exception e) {
            System.err.println("Error amb JDBC: " + e.getMessage());
        }
    }

    public static void saveWithHibernate(Serie serie) {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        Session session = factory.openSession();
        try {
            Transaction tx = session.beginTransaction();
            session.persist(serie);
            tx.commit();
            System.out.println("Serie guardada amb Hibernate! ID: " + serie.getId());
        } catch (Exception e) {
            System.err.println("Error guardant amb Hibernate: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
            factory.close();
        }
    }

    public static void queryWithHQL() {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        Session session = factory.openSession();
        try {
            Query<Temporada> query = session.createQuery(
                AppConstants.HQL_TEMPORADES_BY_SERIE, Temporada.class);
            query.setParameter("serieId", AppConstants.DEFAULT_SERIE_ID);

            for (Temporada temporada : query.list()) {
                System.out.println("Numero: " + temporada.getNumero()
                        + " | Any: " + temporada.getAnyEstrena());
            }
        } catch (Exception e) {
            System.err.println("Error amb HQL: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static void queryWithCriteria() {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        Session session = factory.openSession();
        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Temporada> criteriaQuery = cb.createQuery(Temporada.class);
            Root<Temporada> root = criteriaQuery.from(Temporada.class);

            criteriaQuery.select(root)
                         .where(cb.equal(root.get("serie").get("id"), AppConstants.DEFAULT_SERIE_ID))
                         .orderBy(cb.asc(root.get("numero")));

            List<Temporada> resultats = session.createQuery(criteriaQuery).getResultList();
            System.out.println("Temporades trobades (Criteria): " + resultats.size());
            for (Temporada temporada : resultats) {
                System.out.println("Serie ID: " + temporada.getSerie().getId()
                        + " | Temporada: " + temporada.getNumero());
            }
        } catch (Exception e) {
            System.err.println("Error amb Criteria: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
            factory.close();
        }
    }
}
