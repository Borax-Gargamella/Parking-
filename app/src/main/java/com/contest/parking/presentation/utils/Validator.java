package com.contest.parking.presentation.utils;

import android.util.Patterns;

public class Validator {

    /**
     * Valida gli input per la registrazione.
     * Se uno qualsiasi dei campi è vuoto o non valido, lancia un'eccezione IllegalArgumentException.
     *
     * @param nome     il nome dell'utente
     * @param cognome  il cognome
     * @param targa    la targa dell'auto
     * @param email    l'email
     * @param password la password
     */
    public static void validateRegistrationInputs(String nome, String cognome, String targa, String email, String password) {
        if (nome == null || nome.trim().isEmpty() ||
                cognome == null || cognome.trim().isEmpty() ||
                targa == null || targa.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Tutti i campi sono obbligatori");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Email non valida");
        }
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Password non valida");
        }
    }

    /**
     * Valida gli input per il login (solo email e password).
     *
     * @param email    l'email
     * @param password la password
     */
    public static void validateLoginInputs(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email obbligatoria");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Email non valida");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password obbligatoria");
        }
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Password non valida");
        }
    }

    /**
     * Verifica se l'email è in un formato valido.
     *
     * @param email l'email da verificare
     * @return true se l'email è valida, false altrimenti
     */
    public static boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Verifica se la password rispetta i requisiti:
     * - Almeno 6 caratteri
     * - Almeno 1 cifra, 1 lettera minuscola, 1 lettera maiuscola e 1 carattere speciale
     * - Nessuno spazio
     *
     * @param password la password da verificare
     * @return true se la password è valida, false altrimenti
     */
    public static boolean isValidPassword(String password) {
        // Regex: almeno 6 caratteri, almeno una cifra, una minuscola, una maiuscola, un carattere speciale e nessuno spazio
        return password != null && password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$");
    }
}
