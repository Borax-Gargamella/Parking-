package com.contest.parking.domain;

import android.util.Patterns;
import com.contest.parking.data.model.Utente;
import com.contest.parking.data.repository.AuthRepository;
import com.contest.parking.data.repository.UtenteRepository;

public class UseCaseRegistraUtente {

    // Definiamo un'interfaccia callback per il completamento della registrazione
    public interface OnRegisterCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void registraUtente(String nome, String cognome, String targa, String email, String password,
                               OnRegisterCompleteListener listener) {
        // Validazione degli input
        if (nome.isEmpty() || cognome.isEmpty() || targa.isEmpty() || email.isEmpty() || password.isEmpty()) {
            listener.onFailure(new IllegalArgumentException("Tutti i campi sono obbligatori"));
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            listener.onFailure(new IllegalArgumentException("Email non valida"));
            return;
        }
        // Check if password is valid (at least 6 characters, at least 1 digit, at least 1 uppercase letter, at least 1 lowercase letter, at least 1 special character)
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$")) {
            throw new IllegalArgumentException("Password non valida");
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
