package com.contest.parking.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.contest.parking.R;
import com.contest.parking.data.model.Storico;
import com.contest.parking.data.model.Utente;
import com.contest.parking.data.repository.AuthRepository;
import com.contest.parking.data.repository.StoricoRepository;
import com.contest.parking.data.repository.UtenteRepository;
import com.contest.parking.domain.UseCaseCaricaDatiUtente;
import com.contest.parking.domain.UseCaseCaricaPostoPrenotato;
import com.contest.parking.presentation.adapter.StoricoAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class UserAreaActivity extends BaseActivity {

    private LinearLayout llUserData;
    private TextView textNome, textCognome, textEmail, textTarga, textPostoPrenotato;
    private MaterialButton btnPaga, btnLogout;

    private RecyclerView recyclerPrenotazioni; // la "tabella"
    private StoricoAdapter storicoAdapter;

    private AuthRepository authRepository;
    private String currentUid;

    private UseCaseCaricaDatiUtente useCaseCaricaDatiUtente;
    private UseCaseCaricaPostoPrenotato useCaseCaricaPostoPrenotato;

    // Aggiungi un tuo repository/storico
    private StoricoRepository storicoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityLayout(R.layout.activity_user_area);

        // Binding
        llUserData = findViewById(R.id.llUserData);
        textNome = findViewById(R.id.textNome);
        textCognome = findViewById(R.id.textCognome);
        textEmail = findViewById(R.id.textEmail);
        textTarga = findViewById(R.id.textTarga);
        textPostoPrenotato = findViewById(R.id.textPostoPrenotato);
        btnPaga = findViewById(R.id.btnPaga);
        btnLogout = findViewById(R.id.btnLogout);

        recyclerPrenotazioni = findViewById(R.id.recyclerPrenotazioni);
        recyclerPrenotazioni.setLayoutManager(new LinearLayoutManager(this));

        authRepository = new AuthRepository();
        useCaseCaricaDatiUtente = new UseCaseCaricaDatiUtente();
        useCaseCaricaPostoPrenotato = new UseCaseCaricaPostoPrenotato();
        storicoRepository = new StoricoRepository(); // supponendo tu abbia questo

        currentUid = authRepository.getCurrentUserId();
        if (currentUid == null) {
            startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
            finish();
        } else {
            llUserData.setVisibility(View.VISIBLE);
            caricaDatiUtente();
            caricaPostoPrenotato();
            caricaPrenotazioniNonPagate(); // Nuovo metodo
        }

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
                textNome.setText("Nome: " + utente.getNome());
                textCognome.setText("Cognome: " + utente.getCognome());
                textEmail.setText("Email: " + utente.getEmail());
                textTarga.setText("Targa: " + utente.getTarga());
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(UserAreaActivity.this,
                        "Errore caricamento utente: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void caricaPostoPrenotato() {
        // Tua vecchia logica: se c'è un "postoId", mostra il bottone paga "classico"
        useCaseCaricaPostoPrenotato.loadPostoPrenotato(currentUid, new UseCaseCaricaPostoPrenotato.OnPostoPrenotatoLoadedListener() {
            @Override
            public void onSuccess(String postoId) {
                if (postoId != null) {
                    //textPostoPrenotato.setText("Posto auto prenotato: ");
                } else {
                    //textPostoPrenotato.setText("Posto auto prenotato: Nessuno");
                }
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(UserAreaActivity.this,
                        "Errore caricamento posto prenotato: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                btnPaga.setVisibility(View.GONE);
            }
        });

        // Se vuoi puoi anche rimuovere questa vecchia logica
        // e far gestire tutto dal "recycler di prenotazioni"
    }

    /**
     * Carica tutti i record di Storico per l'utente currentUid
     * con "pagato" = false (es: prenotazioni in sospeso).
     */
    private void caricaPrenotazioniNonPagate() {
        storicoRepository.getStoricoNonPagatoByUtente(currentUid, new StoricoRepository.OnStoricoLoadedListener() {
            @Override
            public void onStoricoLoaded(List<Storico> storiciNonPagati) {
                if (storiciNonPagati.size()==1) {
                    textPostoPrenotato.setText("Posto auto prenotato:" + storiciNonPagati.size());
                } else if (storiciNonPagati.isEmpty()) {
                    textPostoPrenotato.setText("Nessun posto auto prenotato");
                } else {
                    textPostoPrenotato.setText("Posti auto prenotati:" + storiciNonPagati.size());
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
            startActivity(intent);
        });
        recyclerPrenotazioni.setAdapter(storicoAdapter);
    }
}
