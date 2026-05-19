package com.nerdflix.model;

public class Temporada {
    private int id;
    private int numero;
    private int anyEstrena;
    private Serie serie;

    public Temporada() {}

    public Temporada(int numero, int anyEstrena) {
        this.numero = numero;
        this.anyEstrena = anyEstrena;
    }

    public int getId() { return id; }
    public int getNumero() { return numero; }
    public int getAnyEstrena() { return anyEstrena; }
    public Serie getSerie() { return serie; }

    public void setId(int id) { this.id = id; }
    public void setNumero(int numero) { this.numero = numero; }
    public void setAnyEstrena(int anyEstrena) { this.anyEstrena = anyEstrena; }
    public void setSerie(Serie serie) { this.serie = serie; }
}
