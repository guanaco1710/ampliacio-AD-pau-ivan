package com.nerdflix.model;

import java.util.ArrayList;
import java.util.List;

public class Serie {
    private int id;
    private String titol;
    private String genere;
    private List<Temporada> temporades = new ArrayList<>();

    public Serie() {}

    public Serie(String titol, String genere) {
        this.titol = titol;
        this.genere = genere;
    }

    public void addTemporada(Temporada temporada) {
        this.temporades.add(temporada);
    }

    public int getId() { return id; }
    public String getTitol() { return titol; }
    public String getGenere() { return genere; }
    public List<Temporada> getTemporades() { return temporades; }

    public void setId(int id) { this.id = id; }
    public void setTitol(String titol) { this.titol = titol; }
    public void setGenere(String genere) { this.genere = genere; }
    public void setTemporades(List<Temporada> temporades) { this.temporades = temporades; }
}
