package com.contest.parking.domain;

import android.util.Patterns;
import androidx.annotation.NonNull;
import com.contest.parking.data.repository.AuthRepository;
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
     *  - Controlla che l'email e la password non siano vuote e che l'email sia in un formato valido.
     *  - Se la validazione ha successo, richiama il login tramite AuthRepository.
     *
     * @param email     l'email inserita dall'utente
     * @param password  la password inserita dall'utente
     * @param listener  il callback per comunicare il risultato
     */
    public void logIn(String email, String password, OnLogInCompleteListener listener) {
        // Validazione degli input
        if (email == null || email.trim().isEmpty()) {
            listener.onFailure(new IllegalArgumentException("Email obbligatoria"));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            listener.onFailure(new IllegalArgumentException("Email non valida"));
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            listener.onFailure(new IllegalArgumentException("Password obbligatoria"));
            return;
        }

        // Check if password is valid (at least 6 characters, at least 1 digit, at least 1 uppercase letter, at least 1 lowercase letter, at least 1 special character)
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$")) {
            throw new IllegalArgumentException("Password non valida");
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
