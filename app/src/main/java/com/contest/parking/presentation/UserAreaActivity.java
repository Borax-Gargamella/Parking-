package com.contest.parking.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.contest.parking.R;
import com.contest.parking.data.model.Storico;
import com.contest.parking.data.model.Utente;
import com.contest.parking.data.repository.AuthRepository;
import com.contest.parking.data.repository.StoricoRepository;
import com.contest.parking.data.repository.UtenteRepository;
import com.contest.parking.domain.UseCaseAggiornaDatiUtente;
import com.contest.parking.domain.UseCaseCaricaDatiUtente;
import com.contest.parking.domain.UseCaseCaricaPrenotazioniNonPagate;
import com.contest.parking.presentation.adapter.StoricoAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class UserAreaActivity extends BaseActivity {

    private LinearLayout llUserData;
    private TextView textNome, textCognome, textEmail, textTarga, textPostoPrenotato;
    private MaterialButton btnLogout, btnModificaDati, btnModificaCredenziali;

    private RecyclerView recyclerPrenotazioni; // la "tabella"
    private StoricoAdapter storicoAdapter;

    private UtenteRepository utenteRepository;
    private AuthRepository authRepository;
    private String currentUid;

    private UseCaseCaricaDatiUtente useCaseCaricaDatiUtente;
    private UseCaseCaricaPrenotazioniNonPagate useCaseCaricaPrenotazioniNonPagate;
    private UseCaseAggiornaDatiUtente useCaseAggiornaDatiUtente;

    // Aggiungi un tuo repository/storico
    private StoricoRepository storicoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityLayout(R.layout.activity_user_area);

        // Binding
        llUserData = findViewById(R.id.llUserData);
        textNome = findViewById(R.id.editNome);
        textCognome = findViewById(R.id.editCognome);
        textEmail = findViewById(R.id.textEmail);
        textTarga = findViewById(R.id.editTarga);
        textPostoPrenotato = findViewById(R.id.textPostoPrenotato);
        btnLogout = findViewById(R.id.btnLogout);
        btnModificaDati = findViewById(R.id.btnModificaDati);
        btnModificaCredenziali = findViewById(R.id.btnModificaCredenziali);

        recyclerPrenotazioni = findViewById(R.id.recyclerPrenotazioni);
        recyclerPrenotazioni.setLayoutManager(new LinearLayoutManager(this));

        authRepository = new AuthRepository();
        useCaseCaricaDatiUtente = new UseCaseCaricaDatiUtente();

        storicoRepository = new StoricoRepository();
        useCaseCaricaPrenotazioniNonPagate = new UseCaseCaricaPrenotazioniNonPagate(storicoRepository);

        utenteRepository = new UtenteRepository();
        useCaseAggiornaDatiUtente = new UseCaseAggiornaDatiUtente(utenteRepository);

        currentUid = authRepository.getCurrentUserId();
        if (currentUid == null) {
            startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
            finish();
        } else {
            llUserData.setVisibility(View.VISIBLE);
            caricaDatiUtente();
            caricaPrenotazioniNonPagate();
        }

        // Gestione click modifica Dati Utente
        btnModificaDati.setOnClickListener(v -> {
            if (btnModificaDati.getText().toString().equals("Modifica Dati")) {
                // Abilita la modifica dei campi
                textNome.setEnabled(true);
                textCognome.setEnabled(true);
                textTarga.setEnabled(true);
                btnModificaDati.setText("Salva");
            } else {
                // Raccogli i dati aggiornati
                String nuovoNome = textNome.getText().toString().trim();
                String nuovoCognome = textCognome.getText().toString().trim();
                String nuovaTarga = textTarga.getText().toString().trim();
                nuovaTarga = nuovaTarga.toUpperCase();

                // Chiama il Use Case per aggiornare i dati
                useCaseAggiornaDatiUtente.aggiornaDatiUtente(currentUid, nuovoNome, nuovoCognome, nuovaTarga, new UseCaseAggiornaDatiUtente.OnAggiornaDatiUtenteListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(UserAreaActivity.this, "Dati aggiornati con successo", Toast.LENGTH_SHORT).show();
                        // Disabilita i campi e ripristina il testo del bottone
                        textNome.setEnabled(false);
                        textCognome.setEnabled(false);
                        textTarga.setEnabled(false);
                        btnModificaDati.setText("Modifica Dati");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(UserAreaActivity.this, "Errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // Gestione click logout
        btnLogout.setOnClickListener(v -> {
            authRepository.logoutUser();
            startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void caricaDatiUtente() {
        useCaseCaricaDatiUtente.loadUserData(currentUid, new UseCaseCaricaDatiUtente.OnUserDataLoadedListener() {
            @Override
            public void onSuccess(Utente utente) {
                textNome.setText(utente.getNome());
                textCognome.setText(utente.getCognome());
                textEmail.setText(utente.getEmail());
                textTarga.setText(utente.getTarga());
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(UserAreaActivity.this,
                        "Errore caricamento utente: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Carica tutti i record di Storico per l'utente currentUid
     * con "pagato" = false (es: prenotazioni in sospeso).
     */
    private void caricaPrenotazioniNonPagate() {
        useCaseCaricaPrenotazioniNonPagate.execute(currentUid, new UseCaseCaricaPrenotazioniNonPagate.Callback() {
            @Override
            public void onSuccess(List<Storico> storiciNonPagati) {
                // Aggiornamento della UI
                if (storiciNonPagati.size() == 1) {
                    textPostoPrenotato.setText("Posto auto prenotato: " + storiciNonPagati.size());
                } else if (storiciNonPagati.isEmpty()) {
                    textPostoPrenotato.setText("Nessun posto auto prenotato");
                } else {
                    textPostoPrenotato.setText("Posti auto prenotati: " + storiciNonPagati.size());
                }
                setupRecyclerView(storiciNonPagati);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(UserAreaActivity.this,
                        "Errore caricamento prenotazioni non pagate: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupRecyclerView(List<Storico> list) {
        storicoAdapter = new StoricoAdapter(list, item -> {
            // onPagaClick
            // Avvia PaymentActivity, passandogli l'ID del doc (o i dati che servono)
            Intent intent = new Intent(UserAreaActivity.this, PaymentActivity.class);
            intent.putExtra("Data", item.getDataInizio() + " " + item.getDataFine());
            intent.putExtra("storicoId", item.getId());
            intent.putExtra("importo", item.getPrezzo());
            startActivity(intent);
        });
        recyclerPrenotazioni.setAdapter(storicoAdapter);
    }
}
