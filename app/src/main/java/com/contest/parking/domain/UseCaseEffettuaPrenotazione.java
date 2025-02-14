package com.contest.parking.domain;

import com.contest.parking.data.model.Range;
import com.contest.parking.presentation.utils.Validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class UseCaseEffettuaPrenotazione {
    private UseCasePrenotaPosto useCasePrenotaPosto;
    private List<Range> dateOccupate;

    // Interfaccia per restituire il risultato al chiamante
    public interface Callback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public UseCaseEffettuaPrenotazione(UseCasePrenotaPosto useCasePrenotaPosto, List<Range> dateOccupate) {
        this.useCasePrenotaPosto = useCasePrenotaPosto;
        this.dateOccupate = dateOccupate;
    }

    /**
     * Esegue la prenotazione.
     *
     * @param dataInizioStr data di inizio in formato "dd/MM/yyyy"
     * @param dataFineStr   data di fine in formato "dd/MM/yyyy"
     * @param prezzoStr     prezzo base per giorno, in formato numerico
     * @param targa         targa del veicolo (formato AA000AA)
     * @param spotId        id dello spot
     * @param utenteId      id dell'utente
     * @param callback      callback per il risultato
     */
    public void execute(String dataInizioStr, String dataFineStr, String prezzoStr, String targa,
                        String spotId, String utenteId, Callback callback) {

        // Validazione degli input: controlla che le date siano state inserite
        if (dataInizioStr.isEmpty() || dataFineStr.isEmpty()) {
            callback.onFailure("Seleziona un intervallo di giorni");
            return;
        }

        // Parsing del prezzo base
        /*
        double prezzo;
        try {
            prezzo = Double.parseDouble(prezzoStr.trim());
        } catch (NumberFormatException e) {
            callback.onFailure("Prezzo non valido");
            return;
        }
        */

        double prezzo = Double.parseDouble(prezzoStr.trim());

        // Conversione delle date da "dd/MM/yyyy" a millisecondi (a mezzanotte)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        long dataInizioMs, dataFineMs;
        try {
            dataInizioMs = sdf.parse(dataInizioStr).getTime();
            dataFineMs = sdf.parse(dataFineStr).getTime();
        } catch (ParseException e) {
            callback.onFailure("Formato data non valido");
            return;
        }

        // Controlla che la data di fine non sia precedente a quella di inizio
        if (dataFineMs < dataInizioMs) {
            callback.onFailure("Data fine precedente alla data inizio!");
            return;
        }

        // Prenotazione giornaliera: la data di fine copre l'intero giorno
        long dataFineGiornoIntero = dataFineMs + 86400000L - 1; // (24*60*60*1000) - 1

        // Verifica che l'intervallo non si sovrapponga a prenotazioni esistenti
        if (isSovrapposto(dataInizioMs, dataFineGiornoIntero)) {
            callback.onFailure("Le date selezionate si sovrappongono a un'altra prenotazione!");
            return;
        }

        // Controllo del formato della targa (AA000AA)
        if (!Validator.isValidTarga(targa)) {
            callback.onFailure("Formato targa non valido");
            return;
        }

        // Calcola il numero di giorni (inclusivi) e il prezzo totale
        long giorni = ((dataFineGiornoIntero - dataInizioMs) / 86400000L) + 1;
        prezzo = prezzo * giorni;

        // Esegue la prenotazione nel DB
        useCasePrenotaPosto.prenotaPosto(spotId, utenteId, targa,
                prezzo, dataInizioMs, dataFineGiornoIntero, new UseCasePrenotaPosto.OnPrenotaPostoCompleteListener() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure("Errore: " + e.getMessage());
                    }
                });
    }

    /**
     * Verifica se l'intervallo [start, end] si sovrappone a uno qualunque dei range occupati.
     *
     * @param start inizio dell'intervallo in ms
     * @param end   fine dell'intervallo in ms
     * @return true se c'è sovrapposizione, false altrimenti
     */
    private boolean isSovrapposto(long start, long end) {
        Range range = new Range(start, end);
        for (Range r : dateOccupate) {
            // Assumiamo che il metodo overlaps restituisca un booleano
            if (r.overlaps(range)) {
                return true;
            }
        }
        return false;
    }
}
