package com.contest.parking.presentation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.contest.parking.R;
import com.contest.parking.data.model.Utente;
import com.contest.parking.data.repository.AuthRepository;
import com.contest.parking.data.repository.PostoAutoRepository;
import com.contest.parking.data.repository.StoricoRepository;
import com.contest.parking.data.repository.UtenteRepository;
import com.contest.parking.domain.UseCasePrenotaPosto;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PrenotaPostoActivity extends BaseActivity {

    private TextInputEditText editNome, editCognome, editTarga, editDataInizio, editDataFine;
    private MaterialButton btnPrenota;

    // Use case e repository
    private UseCasePrenotaPosto useCasePrenotaPosto;
    private AuthRepository authRepository;
    private UtenteRepository utenteRepository;

    // Dati per la prenotazione
    private String spotId;    // ID del posto auto (passato tramite Intent)
    private String utenteId;  // ID dell'utente loggato
    private double prezzo;    // Prezzo della prenotazione (passato tramite Intent)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inietta il layout specifico per PrenotaPostoActivity nel container della BaseActivity
        setActivityLayout(R.layout.activity_prenota_posto);

        // Binding delle view
        editNome = findViewById(R.id.editNome);
        editCognome = findViewById(R.id.editCognome);
        editTarga = findViewById(R.id.editTarga);
        editDataInizio = findViewById(R.id.editDataInizio);
        editDataFine = findViewById(R.id.editDataFine);
        btnPrenota = findViewById(R.id.btnPrenota);

        // Inizializza repository e use case
        authRepository = new AuthRepository();
        utenteRepository = new UtenteRepository();
        useCasePrenotaPosto = new UseCasePrenotaPosto(new PostoAutoRepository(), new StoricoRepository());

        // Recupera i dati passati tramite Intent
        spotId = getIntent().getStringExtra("spotId");
        prezzo = getIntent().getDoubleExtra("prezzo", 0.0);
        if (spotId == null || spotId.isEmpty()) {
            Toast.makeText(this, "ID del posto auto non fornito", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Recupera l'ID dell'utente loggato
        utenteId = authRepository.getCurrentUserId();
        if (utenteId == null) {
            Toast.makeText(this, "Utente non loggato", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Carica i dati dell'utente per pre-compilare i campi (nome, cognome, targa)
        utenteRepository.getUtente(utenteId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Utente user = documentSnapshot.toObject(Utente.class);
                        if (user != null) {
                            editNome.setText(user.getNome());
                            editCognome.setText(user.getCognome());
                            editTarga.setText(user.getTarga());
                            // Rendi i campi non editabili
                            editNome.setEnabled(false);
                            editCognome.setEnabled(false);
                            editTarga.setEnabled(false);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PrenotaPostoActivity.this, "Errore caricamento dati utente: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Imposta il listener per il campo Data Inizio
        editDataInizio.setOnClickListener(v -> {
            pickDateTime(editDataInizio);
        });

        // Imposta il listener per il campo Data Fine
        editDataFine.setOnClickListener(v -> {
            pickDateTime(editDataFine);
        });

        // Listener per il bottone Prenota
        btnPrenota.setOnClickListener(v -> {
            String dataInizioStr = editDataInizio.getText().toString().trim();
            String dataFineStr = editDataFine.getText().toString().trim();
            if (dataInizioStr.isEmpty() || dataFineStr.isEmpty()) {
                Toast.makeText(PrenotaPostoActivity.this, "Seleziona le date di prenotazione", Toast.LENGTH_SHORT).show();
                return;
            }

            // Converte le date in formato "dd/MM/yyyy HH:mm" in timestamp (millisecondi)
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            long dataInizio, dataFine;
            try {
                dataInizio = sdf.parse(dataInizioStr).getTime();
                dataFine = sdf.parse(dataFineStr).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(PrenotaPostoActivity.this, "Formato data non valido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verifica che la data fine sia successiva alla data inizio
            if (dataFine <= dataInizio) {
                Toast.makeText(PrenotaPostoActivity.this, "La data fine deve essere successiva alla data inizio", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chiama il use case per prenotare il posto auto, passando dataInizio e dataFine
            useCasePrenotaPosto.prenotaPosto(spotId, utenteId, editTarga.getText().toString().trim(), prezzo, dataInizio, dataFine,
                    new UseCasePrenotaPosto.OnPrenotaPostoCompleteListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(PrenotaPostoActivity.this, "Prenotazione effettuata con successo!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(PrenotaPostoActivity.this, "Errore nella prenotazione: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    /**
     * Mostra un DatePickerDialog seguito da un TimePickerDialog per selezionare data e ora,
     * e imposta il risultato formattato nel campo di input passato.
     *
     * Il formato utilizzato è "dd/MM/yyyy HH:mm".
     */
    private void pickDateTime(TextInputEditText targetField) {
        Calendar calendar = Calendar.getInstance();
        // Mostra il DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(PrenotaPostoActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Una volta selezionata la data, mostra il TimePickerDialog
                    Calendar newCalendar = Calendar.getInstance();
                    newCalendar.set(selectedYear, selectedMonth, selectedDay);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(PrenotaPostoActivity.this,
                            (timePicker, selectedHour, selectedMinute) -> {
                                // Imposta la data e ora formattate
                                newCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                                newCalendar.set(Calendar.MINUTE, selectedMinute);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                targetField.setText(sdf.format(newCalendar.getTime()));
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
