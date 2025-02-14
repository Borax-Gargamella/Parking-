package com.contest.parking.presentation;


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
import com.contest.parking.domain.UseCasePrenotaPosto;
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

        // Carica dati utente, se vuoi precompilare campi come nome/cognome/targa
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
                            editTarga.setEnabled(true);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PrenotaPostoActivity.this, "Errore caricamento dati utente: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });


        // Carica intervalli occupati in base a Storico
        caricaIntervalliOccupati(spotId);

        // Click su DataInizio e DataFine: apri lo STESSO range picker
        // L'idea è che l'utente seleziona in un colpo solo inizio e fine
        View.OnClickListener openRangePickerListener = v -> openMaterialRangePicker(editDataInizio, editDataFine);
        editDataInizio.setOnClickListener(openRangePickerListener);
        editDataFine.setOnClickListener(openRangePickerListener);

        // Click su Prenota
        btnPrenota.setOnClickListener(v -> effettuaPrenotazione());
    }

    /**
     * Legge i record di Storico per lo spotId e riempie dateOccupate.
     */
    private void caricaIntervalliOccupati(String spotId) {
        storicoRepository.getStoricoBySpotId(spotId, new StoricoRepository.OnStoricoLoadedListener() {
            @Override
            public void onStoricoLoaded(List<Storico> listaStorico) {
                for (Storico s : listaStorico) {
                    // Supponendo che s.getDataInizio() / getDataFine() siano long (ms)
                    dateOccupate.add(new Range(s.getDataInizio(), s.getDataFine()));
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(PrenotaPostoActivity.this,
                        "Errore caricamento prenotazioni esistenti",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Apre un MaterialDatePicker di tipo 'dateRangePicker':
     * L'utente seleziona un giorno di inizio e un giorno di fine.
     * Disabilitiamo i giorni già occupati con CustomDateValidator.
     */


    private void openMaterialRangePicker(TextInputEditText editDataInizio, TextInputEditText editDataFine) {

        // Calcola "oggi" a mezzanotte
        long todayMidnight = truncateToDay(System.currentTimeMillis());

        // 2) Creiamo un validator che blocca tutto ciò che è prima di todayMidnightLocal
        CalendarConstraints.DateValidator minDateValidator =
                DateValidatorPointForward.from(todayMidnight);

        // 3) Creiamo un "CompositeDateValidator" che unisce minDateValidator e il tuo
        // CustomDateValidator, così verranno disabilitati sia i giorni passati,
        // sia i giorni occupati
        CalendarConstraints.DateValidator compositeValidator =
                CompositeDateValidator.allOf(
                        Arrays.asList(minDateValidator, new CustomDateValidator(dateOccupate))
                );

        // 4) Costruiamo i "constraints" e settiamo lo start (solo per far partire il calendario da "oggi")
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setStart(todayMidnight)
                .setValidator(compositeValidator);

        // 2) Crea il range picker
        MaterialDatePicker<Pair<Long, Long>> rangePicker = MaterialDatePicker.Builder
                .dateRangePicker()
                .setTitleText("Seleziona l'intervallo di giorni liberi")
                .setCalendarConstraints(constraintsBuilder.build())
                .build();

        // 3) Mostra
        rangePicker.show(getSupportFragmentManager(), "MATERIAL_RANGE_PICKER");

        // 4) Listener
        rangePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection != null) {
                long startDay = selection.first;
                long endDay = selection.second;

                // Tronca a mezzanotte
                startDay = truncateToDay(startDay);
                endDay = truncateToDay(endDay);

                // STEP FONDAMENTALE:
                // Controlla giorno per giorno se è disabilitato
                if (!checkIntervalValid(startDay, endDay)) {
                    Toast.makeText(this,
                            "L'intervallo selezionato include giorni occupati!",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Se arrivi qui, tutti i giorni sono validi
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                editDataInizio.setText(sdf.format(new Date(startDay)));
                editDataFine.setText(sdf.format(new Date(endDay)));
                editPrezzo.setText(String.format(Locale.getDefault(), "%.2f", prezzo * ((endDay - startDay) / 86400000L + 1)));
            }
        });
    }

    /**
     * Al click su "Prenota" recuperiamo dataInizio e dataFine e chiamiamo il UseCase prenota.
     */
    private void effettuaPrenotazione() {
        String dataInizioStr = editDataInizio.getText().toString().trim();
        String dataFineStr = editDataFine.getText().toString().trim();
        Double prezzo = Double.parseDouble(editPrezzo.getText().toString().trim());

        if (dataInizioStr.isEmpty() || dataFineStr.isEmpty()) {
            Toast.makeText(this, "Seleziona un intervallo di giorni", Toast.LENGTH_SHORT).show();
            return;
        }

        // Converte "dd/MM/yyyy" in millisecondi (mezzanotte)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        long dataInizioMs, dataFineMs;
        try {
            dataInizioMs = sdf.parse(dataInizioStr).getTime();
            dataFineMs = sdf.parse(dataFineStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Formato data non valido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Se la data fine è prima della data inizio, errore
        if (dataFineMs < dataInizioMs) {
            Toast.makeText(this, "Data fine precedente alla data inizio!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prenotazione giornaliera: dataFine in realtà copre TUTTO il giorno
        // Quindi facciamo: dataFineMs = dataFineMs + 24h - 1
        long dataFineGiornoIntero = dataFineMs + 86400000L - 1; // (24*60*60*1000) - 1

        // Check finale di sovrapposizione (se serve)
        if (isSovrapposto(dataInizioMs, dataFineGiornoIntero)) {
            Toast.makeText(this, "Le date selezionate si sovrappongono a un'altra prenotazione!", Toast.LENGTH_LONG).show();
            return;
        }

        // Controllo formato targa AA000AA
        String targa = editTarga.getText().toString().trim();
        if (!Validator.isValidTarga(targa)) {
            Toast.makeText(this, "Formato targa non valido", Toast.LENGTH_SHORT).show();
            return;
        }

        //Prezzo uguale prezzo * giorni
        prezzo = prezzo * ((dataFineGiornoIntero - dataInizioMs) / 86400000L + 1);

        // Nel DB memorizzi: dataInizioMs, dataFineGiornoIntero
        useCasePrenotaPosto.prenotaPosto(spotId, utenteId, targa,
                prezzo, dataInizioMs, dataFineGiornoIntero, new UseCasePrenotaPosto.OnPrenotaPostoCompleteListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(PrenotaPostoActivity.this,
                                "Prenotazione effettuata!",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(PrenotaPostoActivity.this,
                                "Errore: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Controlla se [start, end] si sovrappone a uno qualunque dei range in dateOccupate.
     */
    private boolean isSovrapposto(long start, long end) {
        Range range = new Range(start, end);
        for (Range r : dateOccupate) {
            r.overlaps(range);
        }
        return false;
    }

    /**
     * Utility per troncare un timestamp a mezzanotte locale.
     */
    private long truncateToDay(long timeInMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private boolean checkIntervalValid(long startDay, long endDay) {
        // Cicla tutti i giorni da startDay a endDay
        // Se ne trovi uno occupato, ritorna false

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(startDay);

        while (c.getTimeInMillis() <= endDay) {
            long dayStart = c.getTimeInMillis();
            long dayEnd = dayStart + 86400000L - 1;

            // Se dayStart/dayEnd si sovrappone a uno dei Range occupati -> false
            for (Range r : dateOccupate) {
                if (dayStart <= r.end && r.start <= dayEnd) {
                    return false;
                }
            }
            // Passa al giorno successivo
            c.add(Calendar.DATE, 1);
        }
        return true;
    }
}
