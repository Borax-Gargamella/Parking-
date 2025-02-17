package com.contest.parking.domain;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.contest.parking.data.model.Range;
import com.contest.parking.presentation.utils.Validator;
import com.contest.parking.presentation.utils.wrapper.PrenotazioneValidatedData;
import java.util.List;

public class UseCaseEffettuaPrenotazione {

    private Context context;
    private UseCasePrenotaPosto useCasePrenotaPosto;
    private List<Range> dateOccupate;

    // Interfaccia per restituire il risultato al chiamante
    public interface Callback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    // Modifica il costruttore per passare anche il Context
    public UseCaseEffettuaPrenotazione(Context context, UseCasePrenotaPosto useCasePrenotaPosto, List<Range> dateOccupate) {
        this.context = context;
        this.useCasePrenotaPosto = useCasePrenotaPosto;
        this.dateOccupate = dateOccupate;
    }

    /**
     * Esegue la prenotazione e, se andata a buon fine, schedula un promemoria per il giorno prima.
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
                        // Schedula l'allarme per il giorno prima
                        scheduleReminder(validatedData.getDataInizioMs());
                        callback.onSuccess();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure("Errore: " + e.getMessage());
                    }
                }
        );
    }

    /**
     * Schedula un promemoria (allarme) per il giorno prima dell'inizio della prenotazione.
     * Se il tempo calcolato è nel passato, non schedula nulla.
     *
     * @param bookingStartMillis Data di inizio prenotazione in millisecondi
     */
    private void scheduleReminder(long bookingStartMillis) {
        long oneDayInMillis = 24 * 60 * 60 * 1000L;
        long reminderTime = bookingStartMillis - oneDayInMillis;

        // Se il promemoria sarebbe in un momento già passato, non schedula nulla.
        if (reminderTime <= System.currentTimeMillis()) {
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }

        // Controlla il permesso per gli allarmi esatti (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(context, "Impossibile schedulare allarmi esatti, controlla le impostazioni.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // Controlla il permesso per le notifiche (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Permesso per le notifiche non concesso.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Intent intent = new Intent(context, PrenotazioneReminderReceiver.class);
        intent.putExtra("message", "Domani inizia la tua prenotazione!");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
    }
}
