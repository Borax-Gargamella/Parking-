package com.contest.parking.presentation.utils;

import android.util.Patterns;
import com.contest.parking.data.model.Range;
import com.contest.parking.presentation.utils.wrapper.PrenotazioneValidatedData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
    public static void validateRegistrationInputs(String nome, String cognome, String targa, String email, String password, String password2) {
        if (nome == null || nome.trim().isEmpty() ||
                cognome == null || cognome.trim().isEmpty() ||
                targa == null || targa.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                password2 == null || password2.trim().isEmpty()) {
            throw new IllegalArgumentException("Tutti i campi sono obbligatori");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Email non valida");
        }

        if (!password.equals(password2)) {
            throw new IllegalArgumentException("Le password non corrispondono");
        }

        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Password non valida");
        }

        if (!isValidTarga(targa)) {
            throw new IllegalArgumentException("Targa non valida");
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
        return password != null && password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!.?])(?=\\S+$).{6,}$");
    }

    /**
     * Verifica se la targa è in un formato valido.
     *
     * @param targa la targa da verificare
     * @return true se la targa è valida, false altrimenti
     */
    public static boolean isValidTarga(String targa) {
        return targa != null && !targa.trim().isEmpty() && targa.matches("^[A-Z]{2}[0-9]{3}[A-Z]{2}$");
    }

    /**
     * Valida gli input per la prenotazione:
     * - Controlla che le date e il prezzo non siano vuoti
     * - Converte le date da "dd/MM/yyyy" a millisecondi (a mezzanotte)
     * - Verifica che la data di fine non sia precedente a quella di inizio
     * - Calcola la data di fine che copre l'intero giorno
     * - Verifica che l'intervallo non si sovrapponga a prenotazioni esistenti
     * - Verifica il formato della targa
     * - Calcola il prezzo totale (prezzo base * numero di giorni)
     *
     * @param dataInizioStr stringa con la data di inizio ("dd/MM/yyyy")
     * @param dataFineStr   stringa con la data di fine ("dd/MM/yyyy")
     * @param prezzoStr     prezzo base per giorno (come stringa)
     * @param targa         targa del veicolo (formato AA000AA)
     * @param dateOccupate  lista dei range già occupati
     * @return un oggetto PrenotazioneValidatedData contenente i dati validati e convertiti
     * @throws IllegalArgumentException se uno dei controlli fallisce
     */
    public static PrenotazioneValidatedData validatePrenotazioneInputs(
            String dataInizioStr,
            String dataFineStr,
            String prezzoStr,
            String targa,
            List<Range> dateOccupate) {

        // Controllo che le date non siano vuote
        if (dataInizioStr == null || dataInizioStr.trim().isEmpty() ||
                dataFineStr == null || dataFineStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Seleziona un intervallo di giorni");
        }

        // Controllo che il prezzo non sia vuoto
        if (prezzoStr == null || prezzoStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Prezzo non valido");
        }

        // Parsing del prezzo base
        double prezzo;
        try {
            prezzo = Double.parseDouble(prezzoStr.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
            //throw new IllegalArgumentException("Prezzo non valido");
        }

        // Conversione delle date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        long dataInizioMs, dataFineMs;
        try {
            dataInizioMs = sdf.parse(dataInizioStr).getTime();
            dataFineMs = sdf.parse(dataFineStr).getTime();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Formato data non valido");
        }

        // Verifica che la data di fine non sia precedente a quella di inizio
        if (dataFineMs < dataInizioMs) {
            throw new IllegalArgumentException("Data fine precedente alla data inizio!");
        }

        // Calcola la data di fine che copre l'intero giorno
        long dataFineGiornoIntero = dataFineMs + 86400000L - 1;

        // Verifica che l'intervallo non si sovrapponga a prenotazioni esistenti
        if (isSovrapposto(dataInizioMs, dataFineGiornoIntero, dateOccupate)) {
            throw new IllegalArgumentException("Le date selezionate si sovrappongono a un'altra prenotazione!");
        }

        // Verifica il formato della targa
        if (targa == null || targa.trim().isEmpty() || !isValidTarga(targa)) {
            throw new IllegalArgumentException("Formato targa non valido");
        }

        // Calcola il numero di giorni (inclusivi) e il prezzo totale
        long giorni = ((dataFineGiornoIntero - dataInizioMs) / 86400000L) + 1;
        double prezzoTotale = prezzo * giorni;

        return new PrenotazioneValidatedData(dataInizioMs, dataFineGiornoIntero, prezzoTotale, giorni);
    }

    /**
     * Verifica se l'intervallo [start, end] si sovrappone a uno dei range in dateOccupate.
     *
     * @param start        inizio dell'intervallo in millisecondi
     * @param end          fine dell'intervallo in millisecondi
     * @param dateOccupate lista dei range occupati
     * @return true se c'è sovrapposizione, false altrimenti
     */
    private static boolean isSovrapposto(long start, long end, List<Range> dateOccupate) {
        Range range = new Range(start, end);
        for (Range r : dateOccupate) {
            if (r.overlaps(range)) {
                return true;
            }
        }
        return false;
    }
}
