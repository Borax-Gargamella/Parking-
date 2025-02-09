package com.contest.parking.domain;

import android.util.Patterns;
import com.contest.parking.data.model.Utente;
import com.contest.parking.data.repository.AuthRepository;
import com.contest.parking.data.repository.UtenteRepository;
import com.contest.parking.presentation.utils.Validator;

public class UseCaseRegistraUtente {

    // Definiamo un'interfaccia callback per il completamento della registrazione
    public interface OnRegisterCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void registraUtente(String nome, String cognome, String targa, String email, String password,
                               OnRegisterCompleteListener listener) {
        try {
            // Validazione degli input tramite Validator
            Validator.validateRegistrationInputs(nome, cognome, targa, email, password);
        } catch (IllegalArgumentException e) {
            listener.onFailure(e);
            return;
        }

        // Inizializza i repository
        AuthRepository authRepository = new AuthRepository();
        UtenteRepository utenteRepository = new UtenteRepository();

        // Esegui la registrazione tramite FirebaseAuth
        authRepository.registerUser(email, password, task -> {
            if (task.isSuccessful()) {
                // Ottieni l'UID dell'utente registrato
                String uid = authRepository.getCurrentUserId();
                // Crea un oggetto Utente usando l'UID
                Utente utente = new Utente(uid, nome, cognome, targa, email);
                // Salva l'utente su Firestore usando l'UID come documento ID
                utenteRepository.addUtente(utente)
                        .addOnSuccessListener(unused -> listener.onSuccess())
                        .addOnFailureListener(e -> listener.onFailure(e));
            } else {
                listener.onFailure(task.getException());
            }
        });
    }
}
