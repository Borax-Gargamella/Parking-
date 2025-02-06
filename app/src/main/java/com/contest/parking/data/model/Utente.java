package com.contest.parking.data.model;

public class Utente {
    private  String id;
    private String nome;
    private String cognome;
    private String targa;
    private String email;

    // Empty constructor for Firestore
    public Utente(){}

    public Utente(String id, String nome, String cognome, String targa, String email) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.targa = targa;
        this.email = email;
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

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getTarga() {
        return targa;
    }

    public void setTarga(String targa) {
        this.targa = targa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
