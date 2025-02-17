package com.contest.parking.domain;

import com.contest.parking.presentation.utils.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UseCaseAggiornaCredenziali {

    public interface OnAggiornaCredenzialiListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    // Helper per recuperare l'utente corrente
    private FirebaseUser getCurrentUser(OnAggiornaCredenzialiListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            listener.onFailure(new Exception("Utente non autenticato"));
        }
        return user;
    }

    // Helper per eseguire la re-autenticazione
    private void reauthenticateUser(FirebaseUser user, String currentPassword, OnCompleteListener<Void> onCompleteListener) {
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
        user.reauthenticate(credential).addOnCompleteListener(onCompleteListener);
    }

    // ----------------------
    // Aggiornamento Email
    // ----------------------
    public void aggiornaCredenzialiEmail(String newEmail, String currentPassword, OnAggiornaCredenzialiListener listener) {
        FirebaseUser user = getCurrentUser(listener);
        if (user == null) return;

        // Validazione email
        if (newEmail == null || newEmail.isEmpty()) {
            listener.onFailure(new Exception("Inserisci un'email valida"));
            return;
        }
        if (newEmail.equals(user.getEmail())) {
            listener.onFailure(new Exception("Email uguale a quella corrente"));
            return;
        }
        if (!Validator.isValidEmail(newEmail)) {
            listener.onFailure(new Exception("Email non valida"));
            return;
        }
        if (currentPassword == null || currentPassword.isEmpty()) {
            listener.onFailure(new Exception("Inserisci la password corrente"));
            return;
        }
        // Validazione della password corrente
        if (!Validator.isValidPassword(currentPassword)) {
            listener.onFailure(new Exception("La password non rispetta i requisiti"));
            return;
        }

        // Re-autenticazione
        reauthenticateUser(user, currentPassword, task -> {
            if (task.isSuccessful()) {
                user.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener(taskEmail -> {
                    if (taskEmail.isSuccessful()) {
                        listener.onSuccess();
                    } else {
                        listener.onFailure(taskEmail.getException());
                    }
                });
            } else {
                listener.onFailure(task.getException());
            }
        });
    }

    // ----------------------
    // Aggiornamento Password
    // ----------------------
    public void aggiornaCredenzialiPassword(String currentEmail, String currentPassword, String newPassword, String confirmPassword, OnAggiornaCredenzialiListener listener) {
        // Validazione dei campi per la password
        if (newPassword == null || newPassword.isEmpty() ||
                confirmPassword == null || confirmPassword.isEmpty() ||
                currentPassword == null || currentPassword.isEmpty()) {
            listener.onFailure(new Exception("Compila i campi per la password"));
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            listener.onFailure(new Exception("Le password non corrispondono"));
            return;
        }
        if (newPassword.equals(currentPassword)) {
            listener.onFailure(new Exception("La nuova password deve essere diversa da quella corrente"));
            return;
        }
        if (!Validator.isValidPassword(currentPassword)) {
            listener.onFailure(new Exception("La password corrente non rispetta i requisiti"));
            return;
        }
        if (!Validator.isValidPassword(newPassword)) {
            listener.onFailure(new Exception("La password nuova non rispetta i requisiti"));
            return;
        }
        if (currentEmail == null || currentEmail.isEmpty()) {
            listener.onFailure(new Exception("Email corrente non valida"));
            return;
        }

        FirebaseUser user = getCurrentUser(listener);
        if (user == null) return;

        // Re-autenticazione
        reauthenticateUser(user, currentPassword, task -> {
            if (task.isSuccessful()) {
                user.updatePassword(newPassword).addOnCompleteListener(taskPassword -> {
                    if (taskPassword.isSuccessful()) {
                        listener.onSuccess();
                    } else {
                        listener.onFailure(taskPassword.getException());
                    }
                });
            } else {
                listener.onFailure(task.getException());
            }
        });
    }

    // ----------------------
    // Aggiornamento Entrambi (Email e Password)
    // ----------------------
    public void aggiornaCredenzialiEntrambi(String newEmail, String currentPassword, String newPassword, String confirmPassword, OnAggiornaCredenzialiListener listener) {
        FirebaseUser user = getCurrentUser(listener);
        if (user == null) return;

        // Validazioni comuni per l'email e la password
        if (newEmail == null || newEmail.isEmpty()) {
            listener.onFailure(new Exception("Inserisci un'email valida"));
            return;
        }
        if (!Validator.isValidEmail(newEmail)) {
            listener.onFailure(new Exception("Email non valida"));
            return;
        }
        if (newEmail.equals(user.getEmail())) {
            listener.onFailure(new Exception("Email uguale a quella corrente"));
            return;
        }
        if (currentPassword == null || currentPassword.isEmpty()) {
            listener.onFailure(new Exception("Inserisci la password corrente"));
            return;
        }
        if (newPassword == null || newPassword.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            listener.onFailure(new Exception("Compila i campi per la password"));
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            listener.onFailure(new Exception("La nuova password e la conferma non corrispondono"));
            return;
        }
        if (newPassword.equals(currentPassword)) {
            listener.onFailure(new Exception("La nuova password deve essere diversa dalla corrente"));
            return;
        }
        if (!Validator.isValidPassword(currentPassword)) {
            listener.onFailure(new Exception("La password corrente non rispetta i requisiti"));
            return;
        }
        if (!Validator.isValidPassword(newPassword)) {
            listener.onFailure(new Exception("La password nuova non rispetta i requisiti"));
            return;
        }

        // Re-autenticazione
        reauthenticateUser(user, currentPassword, task -> {
            if (task.isSuccessful()) {
                // Se l'email è diversa, aggiorna prima l'email
                if (!user.getEmail().equals(newEmail)) {
                    user.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener(taskEmail -> {
                        if (taskEmail.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(taskPassword -> {
                                if (taskPassword.isSuccessful()) {
                                    listener.onSuccess();
                                } else {
                                    listener.onFailure(taskPassword.getException());
                                }
                            });
                        } else {
                            listener.onFailure(taskEmail.getException());
                        }
                    });
                } else {
                    // Se l'email è invariata, aggiorna solo la password
                    user.updatePassword(newPassword).addOnCompleteListener(taskPassword -> {
                        if (taskPassword.isSuccessful()) {
                            listener.onSuccess();
                        } else {
                            listener.onFailure(taskPassword.getException());
                        }
                    });
                }
            } else {
                listener.onFailure(task.getException());
            }
        });
    }
}
