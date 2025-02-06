package com.contest.parking.data.model;

public class Luogo {
    private String id;
    private String nome;
    private String indirizzo;

    // Empty constructor for Firestore
    public Luogo(){}

    public Luogo(String id, String nome, String indirizzo) {
        this.id = id;
        this.nome = nome;
        this.indirizzo = indirizzo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }
}
