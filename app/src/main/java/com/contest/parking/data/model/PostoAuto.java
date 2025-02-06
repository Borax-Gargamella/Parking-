package com.contest.parking.data.model;

public class PostoAuto {
    private String id;
    private String categoria;
    private boolean statoOccupato; // true = occupato, false = libero
    private String parcheggioID;

    // Empty constructor for Firestore
    public PostoAuto(){}

    public PostoAuto(String id, String categoria, boolean stato, String parcheggioID) {
        this.id = id;
        this.categoria = categoria;
        this.statoOccupato = stato;
        this.parcheggioID = parcheggioID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isStatoOccupato() {
        return statoOccupato;
    }

    public void setStatoOccupato(boolean statoOccupato) {
        this.statoOccupato = statoOccupato;
    }

    public String getParcheggioID() {
        return parcheggioID;
    }

    public void setParcheggioID(String parcheggioID) {
        this.parcheggioID = parcheggioID;
    }
}
