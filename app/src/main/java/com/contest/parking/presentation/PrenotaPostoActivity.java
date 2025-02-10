package com.contest.parking.presentation;

import android.app.DatePickerDialog;
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

    private TextInputEditText editNome, editCognome, editTarga, editDataInizio;
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

        // Imposta il listener sul campo Data Inizio per aprire un DatePickerDialog
        editDataInizio.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(PrenotaPostoActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String data = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        editDataInizio.setText(data);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Listener per il bottone Prenota
        btnPrenota.setOnClickListener(v -> {
            String dataInizioStr = editDataInizio.getText().toString().trim();
            if (dataInizioStr.isEmpty()) {
                Toast.makeText(PrenotaPostoActivity.this, "Seleziona la data di prenotazione", Toast.LENGTH_SHORT).show();
                return;
            }

            // Converte la data in formato "dd/MM/yyyy" in un timestamp (millisecondi)
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            long dataInizio;
            try {
                dataInizio = sdf.parse(dataInizioStr).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(PrenotaPostoActivity.this, "Formato data non valido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chiama il use case per prenotare il posto auto, passando anche dataInizio
            useCasePrenotaPosto.prenotaPosto(spotId, utenteId, editTarga.getText().toString().trim(), prezzo, dataInizio,
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
}
