package com.contest.parking.data.model;

public class Storico {
    private String id;
    private String utenteID;
    private String postoAutoID;
    private long dataInizio;   //timestamp (System.currentTimeMillis())
    private long dataFine;
    private String targa;
    private double prezzo;

    // Empty constructor for Firestore
    public Storico(){}

    public Storico(String id, String utenteID, String postoAutoID, long dataInizio, long dataFine, String targa, double prezzo) {
        this.id = id;
        this.utenteID = utenteID;
        this.postoAutoID = postoAutoID;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.targa = targa;
        this.prezzo = prezzo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUtenteID() {
        return utenteID;
    }

    public void setUtenteID(String utenteID) {
        this.utenteID = utenteID;
    }

    public String getPostoAutoID() {
        return postoAutoID;
    }

    public void setPostoAutoID(String postoAutoID) {
        this.postoAutoID = postoAutoID;
    }

    public long getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(long dataInizio) {
        this.dataInizio = dataInizio;
    }

    public long getDataFine() {
        return dataFine;
    }

    public void setDataFine(long dataFine) {
        this.dataFine = dataFine;
    }

    public String getTarga() {
        return targa;
    }

    public void setTarga(String targa) {
        this.targa = targa;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }
}
