package com.contest.parking.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class UserAreaActivity extends AppCompatActivity {

    private TextView textNome, textCognome, textEmail, textTarga, textPostoPrenotato;
    private MaterialButton btnPaga, btnLogout, btnLogin;

    private AuthRepository authRepository;
    private UtenteRepository utenteRepository;
    private StoricoRepository storicoRepository;

    private String currentUid; // ID utente corrente

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        // Riferimenti alle view per l'utente loggato
        textNome = findViewById(R.id.textNome);
        textCognome = findViewById(R.id.textCognome);
        textEmail = findViewById(R.id.textEmail);
        textTarga = findViewById(R.id.textTarga);
        textPostoPrenotato = findViewById(R.id.textPostoPrenotato);
        btnPaga = findViewById(R.id.btnPaga);
        btnLogout = findViewById(R.id.btnLogout);

        // Riferimento al bottone Login/Registrazione
        btnLogin = findViewById(R.id.btnLogin);

        // Inizializza i repository
        authRepository = new AuthRepository();
        utenteRepository = new UtenteRepository();
        storicoRepository = new StoricoRepository();

        // Ottieni l'ID utente corrente
        currentUid = authRepository.getCurrentUserId();
        if (currentUid == null) {
            // Nessun utente loggato:
            Toast.makeText(this, "Nessun utente loggato", Toast.LENGTH_SHORT).show();

            // Nascondi le view destinate all'utente loggato
            textNome.setVisibility(View.GONE);
            textCognome.setVisibility(View.GONE);
            textEmail.setVisibility(View.GONE);
            textTarga.setVisibility(View.GONE);
            textPostoPrenotato.setVisibility(View.GONE);
            btnPaga.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);

            // Rendi visibile il bottone per login/registrazione
            btnLogin.setVisibility(View.VISIBLE);
            btnLogin.setOnClickListener(v -> {
                // Avvia la LoginActivity
                Intent intent = new Intent(UserAreaActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();  // Termina questa activity se lo desideri
            });
            return;  // Interrompe l'esecuzione del resto del codice
        }

        // Se l'utente è loggato, assicurati che il bottone Login sia nascosto
        btnLogin.setVisibility(View.GONE);

        // 1. Carica i dati utente
        caricaDatiUtente(currentUid);

        // 2. Verifica se c'è un posto prenotato (Storico aperto, dataFine = 0)
        caricaPostoPrenotato(currentUid);

        // Click Paga -> apri PaymentActivity o la tua "CompletaPagamentoActivity"
        btnPaga.setOnClickListener(v -> {
            Intent i = new Intent(UserAreaActivity.this, PaymentActivity.class);
            startActivity(i);
        });

        // Click Logout
        btnLogout.setOnClickListener(v -> {
            authRepository.logoutUser();
            Intent intent = new Intent(UserAreaActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
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
