package com.contest.parking.presentation.utils.wrapper;

public class PrenotazioneValidatedData {
    private final long dataInizioMs;
    private final long dataFineGiornoIntero;
    private final double prezzoTotale;
    private final long giorni;

    public PrenotazioneValidatedData(long dataInizioMs, long dataFineGiornoIntero, double prezzoTotale, long giorni) {
        this.dataInizioMs = dataInizioMs;
        this.dataFineGiornoIntero = dataFineGiornoIntero;
        this.prezzoTotale = prezzoTotale;
        this.giorni = giorni;
    }

    public long getDataInizioMs() {
        return dataInizioMs;
    }

    public long getDataFineGiornoIntero() {
        return dataFineGiornoIntero;
    }

    public double getPrezzoTotale() {
        return prezzoTotale;
    }

    public long getGiorni() {
        return giorni;
    }
}

