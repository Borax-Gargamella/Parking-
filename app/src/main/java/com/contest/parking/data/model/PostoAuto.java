package com.contest.parking.data.model;

public class PostoAuto {
    private String id;
    private String categoria;
    private String parcheggioId;

    // Empty constructor for Firestore
    public PostoAuto(){}

    public PostoAuto(String id, String categoria, boolean stato, String parcheggioId) {
        this.id = id;
        this.categoria = categoria;
        //this.statoOccupato = stato;
        this.parcheggioId = parcheggioId;
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

    public String getParcheggioId() {
        return parcheggioId;
    }

    public void setParcheggioId(String parcheggioId) {
        this.parcheggioId = parcheggioId;
    }


}
