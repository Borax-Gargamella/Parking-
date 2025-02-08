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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class UserAreaActivity extends BaseActivity {

    // Gruppo view per utente loggato
    private LinearLayout llUserData;
    private TextView textNome, textCognome, textEmail, textTarga, textPostoPrenotato;
    private MaterialButton btnPaga, btnLogout;

    // Gruppo view per autenticazione (non loggato)
    private LinearLayout llAuthButtons;
    private MaterialButton btnLogin, btnRegister;

    private AuthRepository authRepository;
    private UtenteRepository utenteRepository;
    private StoricoRepository storicoRepository;

    private String currentUid; // ID utente corrente

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

        // Binding delle view per la sezione autenticazione (non loggato)
        llAuthButtons = findViewById(R.id.llAuthButtons);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Inizializza i repository
        authRepository = new AuthRepository();
        utenteRepository = new UtenteRepository();
        storicoRepository = new StoricoRepository();

        // Controlla se l'utente è loggato
        currentUid = authRepository.getCurrentUserId();
        if (currentUid == null) {
            // Nessun utente loggato: nascondi le view per utente e mostra il pannello di autenticazione
            llUserData.setVisibility(View.GONE);
            llAuthButtons.setVisibility(View.VISIBLE);

            btnLogin.setOnClickListener(v -> {
                startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
            });
            btnRegister.setOnClickListener(v -> {
                startActivity(new Intent(UserAreaActivity.this, RegisterActivity.class));
            });
        } else {
            // Utente loggato: mostra le view per utente e nascondi il pannello di autenticazione
            llUserData.setVisibility(View.VISIBLE);
            llAuthButtons.setVisibility(View.GONE);
            // Carica i dati utente
            caricaDatiUtente(currentUid);
            // Carica il posto prenotato, se presente
            caricaPostoPrenotato(currentUid);

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

    private void caricaDatiUtente(String uid) {
        utenteRepository.getUtente(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Utente u = documentSnapshot.toObject(Utente.class);
                        if (u != null) {
                            textNome.setText("Nome: " + u.getNome());
                            textCognome.setText("Cognome: " + u.getCognome());
                            textEmail.setText("Email: " + u.getEmail());
                            textTarga.setText("Targa: " + u.getTarga());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Errore caricamento utente: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void caricaPostoPrenotato(String uid) {
        storicoRepository.getStoricoApertoByUtente(uid)
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        QueryDocumentSnapshot doc = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                        String postoId = doc.getString("postoAutoId");
                        textPostoPrenotato.setText("Posto auto prenotato: " + postoId);
                    } else {
                        textPostoPrenotato.setText("Posto auto prenotato: Nessuno");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Errore caricamento posto prenotato: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
