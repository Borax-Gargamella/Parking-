package com.contest.parking.domain;

import com.contest.parking.data.model.Range;
import com.contest.parking.presentation.utils.Validator;
import com.contest.parking.presentation.utils.wrapper.PrenotazioneValidatedData;
import java.util.List;

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

        PrenotazioneValidatedData validatedData;
        try {
            validatedData = Validator.validatePrenotazioneInputs(
                    dataInizioStr,
                    dataFineStr,
                    prezzoStr,
                    targa,
                    dateOccupate
            );
        } catch (IllegalArgumentException ex) {
            callback.onFailure(ex.getMessage());
            return;
        }

        // Esegue la prenotazione nel DB con i dati validati
        useCasePrenotaPosto.prenotaPosto(
                spotId,
                utenteId,
                targa,
                validatedData.getPrezzoTotale(),
                validatedData.getDataInizioMs(),
                validatedData.getDataFineGiornoIntero(),
                new UseCasePrenotaPosto.OnPrenotaPostoCompleteListener() {
                    @Override
                    public void onSuccess() {
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure("Errore: " + e.getMessage());
                    }
                }
        );
    }
}
