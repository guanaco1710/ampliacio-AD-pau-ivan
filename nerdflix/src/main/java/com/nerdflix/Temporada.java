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

