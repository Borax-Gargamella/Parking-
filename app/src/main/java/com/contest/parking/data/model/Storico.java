package com.contest.parking.data.model;

public class Storico {
    private String id;
    private String utenteId;
    private String postoAutoId;
    private long dataInizio;   //timestamp (System.currentTimeMillis())
    private long dataFine;
    private String targa;
    private double prezzo;
    private boolean pagato;

    // Empty constructor for Firestore
    public Storico(){}

    public Storico(String id, String utenteId, String postoAutoId, long dataInizio, long dataFine, String targa, double prezzo, boolean pagato) {
        this.id = id;
        this.utenteId = utenteId;
        this.postoAutoId = postoAutoId;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.targa = targa;
        this.prezzo = prezzo;
        this.pagato = pagato;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUtenteId() {
        return utenteId;
    }

    public void setUtenteId(String utenteId) {
        this.utenteId = utenteId;
    }

    public String getPostoAutoId() {
        return postoAutoId;
    }

    public void setPostoAutoId(String postoAutoId) {
        this.postoAutoId = postoAutoId;
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

    public boolean isPagato() {
        return pagato;
    }

    public void setPagato(boolean pagato) {
        this.pagato = pagato;
    }
}
