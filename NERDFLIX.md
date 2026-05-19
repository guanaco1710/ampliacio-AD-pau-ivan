# PROJECTE AMB JDBC I HIBERNATE (XML)

## Main.java

```java
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

```

## Serie.java

```java
package com.nerdflix;

import java.util.ArrayList;
import java.util.List;

public class Serie {  
    private int id;  
    private String titol;  
    private String genere;  
      
    // PREPARACIÓ PER AL FUTUR: La llista de fills  
    // Encara que en JDBC pur a vegades no s'usa, és vital tindre-la ja definida  
    // per a quan arribem a Hibernate (Fases 2 i 3).  
    private List<Temporada> temporades = new ArrayList<>();
    public Serie() {} // IMPRESCINDIBLE pq. Hibernate el necessita.

    public Serie(String titol, String genere) {  
        this.titol = titol;  
        this.genere = genere;  
    }  
      
    // Mètode helper útil per a afegir temporades fàcilment  
    public void addTemporada(Temporada t) {  
        this.temporades.add(t);  
    }  
    // Getters i Setters (...) 
    public String getTitol() {
        return titol;
    }

    public String getGenere() {
        return genere;   
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public void setTemporades(List<Temporada> temporades) {
        this.temporades = temporades;
    }

    public int getId() {
        return id;
    }

    public List<Temporada> getTemporades() {
        return temporades;
    }

}

```

## Temporada.java

```java
package com.nerdflix;

public class Temporada {  
    private int id;  
    private int numero;  
    private int anyEstrena;  
    // En fases avançades necessitarem la referència inversa (objecte Serie),   
    // però per a JDBC usem només l'ID per simplificar.  
    private Serie serie;
    public Temporada() {} // IMPRESCINDIBLE (Hibernate)

    public Temporada(int numero, int any) {   
        this.numero = numero;   
        this.anyEstrena = any;   
    }  
    // Getters i Setters (...)
    public int getNumero() {
        return numero;
    }

    public int getAnyEstrena() {
        return anyEstrena;
    }

    public int getId() {
        return id;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void setAnyEstrena(int anyEstrena) {
        this.anyEstrena = anyEstrena;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

}


```

## GestorJDBC.java

```java
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
```

## hibernate.cfg.xml

```xml
<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">

<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">org.sqlite.JDBC</property>
        <property name="hibernate.connection.url">jdbc:sqlite:bbdd/nerdflix.db</property>
        
        <property name="hibernate.dialect">org.hibernate.community.dialect.SQLiteDialect</property>
        
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">false</property>

        <!-- Mapeigs XML -->
        <mapping resource="Serie.hbm.xml"/>
        <mapping resource="Temporada.hbm.xml"/>
        </session-factory>
</hibernate-configuration>
```

## Serie.hbm.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-mapping PUBLIC 
    "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
    "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">

<hibernate-mapping package="com.nerdflix">
    <class name="Serie" table="Serie">
        <id name="id" column="id">
            <generator class="native"/>
        </id>
        <property name="titol" column="titol"/>
        <property name="genere" column="genere"/>
        
        <bag name="temporades" cascade="all">
            <key column="serie_id"/>
            <one-to-many class="Temporada"/>
        </bag>
    </class>
</hibernate-mapping>
```

## Temporada.hbm.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-mapping PUBLIC 
    "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
    "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">

<hibernate-mapping package="com.nerdflix">
    <class name="Temporada" table="Temporada">
        <id name="id" column="id">
            <generator class="native"/>
        </id>
        <property name="numero" column="numero"/>
        <property name="anyEstrena" column="any_estrena"/>
        
        <many-to-one name="serie" class="Serie" column="serie_id" not-null="true"/>
    </class>
</hibernate-mapping>
```