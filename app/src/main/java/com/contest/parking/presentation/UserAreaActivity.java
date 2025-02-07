package com.contest.parking.presentation;

import android.content.Intent;
import android.os.Bundle;
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
    private MaterialButton btnPaga, btnLogout;

    private AuthRepository authRepository;
    private UtenteRepository utenteRepository;
    private StoricoRepository storicoRepository;

    private String currentUid; // se vuoi tenere l'id utente

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        // View references
        textNome = findViewById(R.id.textNome);
        textCognome = findViewById(R.id.textCognome);
        textEmail = findViewById(R.id.textEmail);
        textTarga = findViewById(R.id.textTarga);
        textPostoPrenotato = findViewById(R.id.textPostoPrenotato);

        btnPaga = findViewById(R.id.btnPaga);
        btnLogout = findViewById(R.id.btnLogout);

        // Repository
        authRepository = new AuthRepository();
        utenteRepository = new UtenteRepository();
        storicoRepository = new StoricoRepository();

        currentUid = authRepository.getCurrentUserId();
        if (currentUid == null) {
            // Utente non loggato, rimanda a Login
            Toast.makeText(this, "Nessun utente loggato", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 1. Carica i dati utente
        caricaDatiUtente(currentUid);

        // 2. Verifica se c'è un posto prenotato (Storico aperto, dataFine = 0)
        caricaPostoPrenotato(currentUid);

        // Click Paga -> apri PaymentActivity o la tua "CompletaPagamentoActivity"
        btnPaga.setOnClickListener(v -> {
            // Se conosci l'ID del posto prenotato, lo passi all'altra activity
            // Oppure puoi passare "null" se non c'è
            // Esempio semplificato:
            Intent i = new Intent(UserAreaActivity.this, PaymentActivity.class);
            startActivity(i);
        });

        // Click Logout
        btnLogout.setOnClickListener(v -> {
            authRepository.logoutUser();
            startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
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
        // Se hai un metodo "getStoricoAperto" che filtra per (utenteId, dataFine=0):
        storicoRepository.getStoricoApertoByUtente(uid)
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Supponendo che l'utente abbia un solo posto aperto
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
