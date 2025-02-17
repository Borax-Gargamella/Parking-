package com.contest.parking.presentation;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.core.util.Pair;
import com.contest.parking.R;
import com.contest.parking.data.model.Range;
import com.contest.parking.data.model.Storico;
import com.contest.parking.data.model.Utente;
import com.contest.parking.data.repository.AuthRepository;
import com.contest.parking.data.repository.PostoAutoRepository;
import com.contest.parking.data.repository.StoricoRepository;
import com.contest.parking.data.repository.UtenteRepository;
import com.contest.parking.domain.*;
import com.contest.parking.presentation.utils.CustomDateValidator;
import com.contest.parking.presentation.utils.Validator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PrenotaPostoActivity extends BaseActivity {

    private TextInputEditText editNome, editCognome, editTarga, editDataInizio, editDataFine, editPrezzo;
    private MaterialButton btnPrenota;

    // Repositories e Use Case (adattali al tuo progetto)
    private AuthRepository authRepository;
    private UtenteRepository utenteRepository;
    private StoricoRepository storicoRepository;
    private UseCasePrenotaPosto useCasePrenotaPosto;

    // Dati passati da Intent
    private String spotId;
    private String utenteId;
    private double prezzo;

    // Lista di intervalli prenotati [start, end]
    private List<Range> dateOccupate = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prenota_posto);
        // Adatta se usi una BaseActivity con un container

        // Binding
        editNome = findViewById(R.id.editNome);
        editCognome = findViewById(R.id.editCognome);
        editTarga = findViewById(R.id.editTarga);
        editDataInizio = findViewById(R.id.editDataInizio);
        editDataFine = findViewById(R.id.editDataFine);
        editPrezzo = findViewById(R.id.editPrezzo);
        editPrezzo.setEnabled(false);
        btnPrenota = findViewById(R.id.btnPrenota);

        // Inizializza repository
        authRepository = new AuthRepository();
        utenteRepository = new UtenteRepository();
        storicoRepository = new StoricoRepository();
        useCasePrenotaPosto = new UseCasePrenotaPosto(new PostoAutoRepository(), new StoricoRepository());

        // Recupera parametri da Intent
        spotId = getIntent().getStringExtra("spotId");
        prezzo = getIntent().getDoubleExtra("prezzo", 0.0);
        Toast.makeText(this, "Prezzo: " + prezzo, Toast.LENGTH_SHORT).show();

        // Check validità
        if (spotId == null || spotId.isEmpty()) {
            Toast.makeText(this, "ID del posto non valido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        utenteId = authRepository.getCurrentUserId();
        if (utenteId == null) {
            Toast.makeText(this, "Utente non loggato", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        UseCaseCaricaDatiUtente useCaseCaricaDatiUtente = new UseCaseCaricaDatiUtente();
        useCaseCaricaDatiUtente.loadUserData(utenteId, new UseCaseCaricaDatiUtente.OnUserDataLoadedListener() {
            @Override
            public void onSuccess(Utente utente) {
                editNome.setText(utente.getNome());
                editCognome.setText(utente.getCognome());
                editTarga.setText(utente.getTarga());
                editNome.setEnabled(false);
                editCognome.setEnabled(false);
                editTarga.setEnabled(true);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(PrenotaPostoActivity.this, "Errore caricamento dati utente: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Carica intervalli occupati in base a Storico
        UseCaseCaricaIntervalliOccupati useCaseCaricaIntervalliOccupati = new UseCaseCaricaIntervalliOccupati(storicoRepository);
        useCaseCaricaIntervalliOccupati.execute(spotId, new UseCaseCaricaIntervalliOccupati.OnRangesLoadedListener() {
            @Override
            public void onRangesLoaded(List<Range> ranges) {
                dateOccupate.clear();
                dateOccupate.addAll(ranges);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(PrenotaPostoActivity.this,
                        "Errore caricamento prenotazioni esistenti",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Click su DataInizio e DataFine: apri lo STESSO range picker
        // L'idea è che l'utente seleziona in un colpo solo inizio e fine
        View.OnClickListener openRangePickerListener = v -> openMaterialRangePicker(editDataInizio, editDataFine);
        editDataInizio.setOnClickListener(openRangePickerListener);
        editDataFine.setOnClickListener(openRangePickerListener);

        // Click su Prenota
        btnPrenota.setOnClickListener(v -> effettuaPrenotazione());
    }

    /**
     * Apre un MaterialDatePicker di tipo 'dateRangePicker':
     * L'utente seleziona un giorno di inizio e un giorno di fine.
     * Disabilitiamo i giorni già occupati con CustomDateValidator.
     */
    private void openMaterialRangePicker(TextInputEditText editDataInizio, TextInputEditText editDataFine) {

        UseCaseDateRangePicker useCase = new UseCaseDateRangePicker(dateOccupate, prezzo);

        MaterialDatePicker<Pair<Long, Long>> rangePicker = useCase.buildPicker(new UseCaseDateRangePicker.DateRangePickerCallback() {
            @Override
            public void onDateRangeSelected(long startDay, long endDay) {
                // Formattazione delle date
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                editDataInizio.setText(sdf.format(new Date(startDay)));
                editDataFine.setText(sdf.format(new Date(endDay)));
                // Calcolo del prezzo in base all'intervallo selezionato
                long days = ((endDay - startDay) / 86400000L) + 1;
                editPrezzo.setText(String.valueOf(prezzo * days));
            }

            @Override
            public void onDateRangeInvalid() {
                Toast.makeText(PrenotaPostoActivity.this,
                        "L'intervallo selezionato include giorni occupati!",
                        Toast.LENGTH_LONG).show();
            }
        });

        rangePicker.show(getSupportFragmentManager(), "MATERIAL_RANGE_PICKER");
    }

    /**
     * Al click su "Prenota" recuperiamo dataInizio e dataFine e chiamiamo il UseCase prenota.
     */
    private void effettuaPrenotazione() {
        // Recupera i dati dai campi di input
        String dataInizioStr = editDataInizio.getText().toString().trim();
        String dataFineStr = editDataFine.getText().toString().trim();
        String prezzoStr = editPrezzo.getText().toString().trim();
        String targa = editTarga.getText().toString().trim();

        Toast.makeText(this, prezzoStr, Toast.LENGTH_SHORT).show();

        // Creazione della UseCase, passando il caso d'uso per la prenotazione e la lista dei range occupati
        UseCaseEffettuaPrenotazione useCase = new UseCaseEffettuaPrenotazione(useCasePrenotaPosto, dateOccupate);

        useCase.execute(dataInizioStr, dataFineStr, prezzoStr, targa, spotId, utenteId, new UseCaseEffettuaPrenotazione.Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(PrenotaPostoActivity.this, "Prenotazione effettuata!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PrenotaPostoActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(PrenotaPostoActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
