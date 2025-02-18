package com.contest.parking.domain;

import com.contest.parking.data.repository.UtenteRepository;
import com.contest.parking.presentation.utils.Validator;

import java.util.HashMap;
import java.util.Map;

public class UseCaseAggiornaDatiUtente {

    private UtenteRepository utenteRepository;

    // Interfaccia callback per notificare il risultato dell'aggiornamento
    public interface OnAggiornaDatiUtenteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    // Costruttore che inietta il repository
    public UseCaseAggiornaDatiUtente(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    /**
     * Valida e aggiorna i dati dell'utente.
     *
     * @param uid      L'ID dell'utente.
     * @param nome     Il nuovo nome.
     * @param cognome  Il nuovo cognome.
     * @param targa    La nuova targa.
     * @param listener Callback per il risultato.
     */
    public void aggiornaDatiUtente(String uid, String nome, String cognome, String targa, OnAggiornaDatiUtenteListener listener) {
        // Validazione di base: controlla che i campi non siano vuoti
        if (nome == null || nome.isEmpty() ||
                cognome == null || cognome.isEmpty() ||
                targa == null || targa.isEmpty()) {
            listener.onFailure(new Exception("Tutti i campi sono obbligatori"));
            return;
        }

        // Validazione specifica della targa usando un Validator personalizzato
        if (!Validator.isValidTarga(targa)) {
            listener.onFailure(new Exception("Formato targa non valido"));
            return;
        }

        // Prepara i dati da aggiornare
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("nome", nome);
        updatedData.put("cognome", cognome);
        updatedData.put("targa", targa);

        // Chiama il metodo nel repository per aggiornare i dati
        utenteRepository.updateUtente(uid, updatedData)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }
}

