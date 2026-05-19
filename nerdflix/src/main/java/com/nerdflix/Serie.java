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
