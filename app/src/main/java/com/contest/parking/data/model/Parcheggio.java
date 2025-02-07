package com.contest.parking.data.model;

public class Parcheggio {
    private String id;
    private String nome;
    private int postiTot;
    private double prezzo;
    private String luogoId;

    // Empty constructor for Firestore
    public Parcheggio(){}

    public Parcheggio(String id, String nome, int postiTot, double prezzo, String luogoID) {
        this.id = id;
        this.nome = nome;
        this.postiTot = postiTot;
        this.prezzo = prezzo;
        this.luogoId = luogoID;
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

    public int getPostiTot() {
        return postiTot;
    }

    public void setPostiTot(int postiTot) {
        this.postiTot = postiTot;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public String getLuogoId() {
        return luogoId;
    }

    public void setLuogoId(String luogoId) {
        this.luogoId = luogoId;
    }
}
