package com.contest.parking.domain;

import androidx.annotation.NonNull;
import com.contest.parking.data.repository.AuthRepository;
import com.contest.parking.presentation.utils.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class UseCaseLogInUtente {

    // Interfaccia di callback per comunicare il risultato del login
    public interface OnLogInCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    private AuthRepository authRepository;

    public UseCaseLogInUtente() {
        authRepository = new AuthRepository();
    }

    /**
     * Esegue la logica di login:
     * - Verifica che l'email e la password siano valide.
     * - Se la validazione ha successo, richiama il login tramite AuthRepository.
     *
     * @param email     l'email inserita dall'utente
     * @param password  la password inserita dall'utente
     * @param listener  il callback per comunicare il risultato
     */
    public void logIn(String email, String password, OnLogInCompleteListener listener) {
        try {
            // Validazione degli input tramite Validator
            Validator.validateLoginInputs(email, password);
        } catch (IllegalArgumentException e) {
            listener.onFailure(e);
            return;
        }

        // Esegui il login tramite AuthRepository
        authRepository.loginUser(email, password, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    listener.onSuccess();
                } else {
                    listener.onFailure(task.getException());
                }
            }
        });
    }
}
