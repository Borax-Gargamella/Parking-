package com.contest.parking.domain;

import com.contest.parking.data.model.Storico;
import com.contest.parking.data.repository.PostoAutoRepository;
import com.contest.parking.data.repository.StoricoRepository;

public class UseCasePrenotaPosto {

    private PostoAutoRepository postoAutoRepository;
    private StoricoRepository storicoRepository;

    public UseCasePrenotaPosto(PostoAutoRepository postoAutoRepository, StoricoRepository storicoRepository) {
        this.postoAutoRepository = postoAutoRepository;
        this.storicoRepository = storicoRepository;
    }

    // Interfaccia callback per notificare il risultato della prenotazione
    public interface OnPrenotaPostoCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    /**
     * Prenota un posto auto:
     * - Aggiorna lo stato del posto auto (statoOccupato = true).
     * - Crea un record nello storico con i dettagli della prenotazione.
     * - In caso di errore nel salvataggio dello storico, ripristina lo stato del posto auto.
     *
     * @param postoId   L'ID del posto auto.
     * @param utenteId  L'ID dell'utente loggato.
     * @param targa     La targa (presa dall'utente loggato).
     * @param prezzo    Il prezzo della prenotazione.
     * @param dataInizio Il timestamp della data di inizio prenotazione.
     * @param dataFine   Il timestamp della data di fine prenotazione.
     * @param listener  Il callback per il risultato.
     */
    public void prenotaPosto(String postoId,
                             String utenteId,
                             String targa,
                             double prezzo,
                             long dataInizio,
                             long dataFine,
                             OnPrenotaPostoCompleteListener listener) {
        // Aggiorna lo stato del posto auto a "occupato"
        postoAutoRepository.getPostoAutoById(postoId)
                .addOnSuccessListener(aVoid -> {
                    // Crea il record dello storico
                    Storico s = new Storico();
                    s.setId(""); // L'ID può essere generato automaticamente dal repository/Firestore
                    s.setUtenteId(utenteId);
                    s.setPostoAutoId(postoId);
                    s.setTarga(targa);
                    s.setPrezzo(prezzo);
                    s.setDataInizio(dataInizio);
                    s.setDataFine(dataFine); // Imposta la data fine passata come parametro
                    s.setPagato(false); // Inizialmente non pagato

                    storicoRepository.addStorico(s)
                            .addOnSuccessListener(unused -> listener.onSuccess())
                            .addOnFailureListener(listener::onFailure);
                })
                .addOnFailureListener(listener::onFailure);
    }
}
