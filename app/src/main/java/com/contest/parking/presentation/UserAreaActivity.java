package com.contest.parking.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.contest.parking.R;
import com.contest.parking.data.model.Utente;
import com.contest.parking.data.repository.AuthRepository;
import com.contest.parking.data.repository.StoricoRepository;
import com.contest.parking.data.repository.UtenteRepository;
import com.contest.parking.domain.UseCaseCaricaDatiUtente;
import com.contest.parking.domain.UseCaseCaricaPostoPrenotato;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class UserAreaActivity extends BaseActivity {

    // Gruppo view per utente loggato
    private LinearLayout llUserData;
    private TextView textNome, textCognome, textEmail, textTarga, textPostoPrenotato;
    private MaterialButton btnPaga, btnLogout;

    private AuthRepository authRepository;
    private String currentUid; // ID utente corrente

    // Use case per il caricamento dei dati
    private UseCaseCaricaDatiUtente useCaseCaricaDatiUtente;
    private UseCaseCaricaPostoPrenotato useCaseCaricaPostoPrenotato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inietta il layout specifico per UserAreaActivity nel container di BaseActivity
        setActivityLayout(R.layout.activity_user_area);

        // Binding delle view per la sezione utente loggato
        llUserData = findViewById(R.id.llUserData);
        textNome = findViewById(R.id.textNome);
        textCognome = findViewById(R.id.textCognome);
        textEmail = findViewById(R.id.textEmail);
        textTarga = findViewById(R.id.textTarga);
        textPostoPrenotato = findViewById(R.id.textPostoPrenotato);
        btnPaga = findViewById(R.id.btnPaga);
        btnLogout = findViewById(R.id.btnLogout);

        // Inizializza i repository (per l'autenticazione) e i use case
        authRepository = new AuthRepository();
        useCaseCaricaDatiUtente = new UseCaseCaricaDatiUtente();
        useCaseCaricaPostoPrenotato = new UseCaseCaricaPostoPrenotato();

        // Controlla se l'utente è loggato
        currentUid = authRepository.getCurrentUserId();
        if (currentUid == null) {
            // Nessun utente loggato: rimanda alla LoginActivity
            startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
            finish();
        } else {
            // Utente loggato: mostra le view per utente
            llUserData.setVisibility(View.VISIBLE);

            // Carica i dati utente tramite il use case
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
                    Toast.makeText(UserAreaActivity.this, "Errore caricamento utente: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            // Carica il posto prenotato tramite il use case
            useCaseCaricaPostoPrenotato.loadPostoPrenotato(currentUid, new UseCaseCaricaPostoPrenotato.OnPostoPrenotatoLoadedListener() {
                @Override
                public void onSuccess(String postoId) {
                    if (postoId != null) {
                        textPostoPrenotato.setText("Posto auto prenotato: " + postoId);
                        btnPaga.setVisibility(View.VISIBLE);
                    } else {
                        textPostoPrenotato.setText("Posto auto prenotato: Nessuno");
                        btnPaga.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(UserAreaActivity.this, "Errore caricamento posto prenotato: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnPaga.setVisibility(View.GONE);
                }
            });

            // Gestione click sui bottoni
            btnPaga.setOnClickListener(v -> {
                // Avvia PaymentActivity
                startActivity(new Intent(UserAreaActivity.this, PaymentActivity.class));
            });
            btnLogout.setOnClickListener(v -> {
                authRepository.logoutUser();
                startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
                finish();
            });
        }
    }
}
