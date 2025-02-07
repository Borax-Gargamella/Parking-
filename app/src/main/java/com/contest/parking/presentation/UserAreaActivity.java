package com.contest.parking.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.contest.parking.R;
import com.contest.parking.data.model.Utente;
import com.contest.parking.data.repository.AuthRepository;
import com.contest.parking.data.repository.UtenteRepository;

public class UserAreaActivity extends AppCompatActivity {

    private TextView textNome, textCognome, textEmail, textTarga;
    private Button pagaButton, logoutButton;
    private AuthRepository authRepository;
    private UtenteRepository utenteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        textNome = findViewById(R.id.textNome);
        textCognome = findViewById(R.id.textCognome);
        textEmail = findViewById(R.id.textEmail);
        textTarga = findViewById(R.id.textTarga);
        pagaButton = findViewById(R.id.btnPaga);
        logoutButton = findViewById(R.id.btnLogout);

        authRepository = new AuthRepository();
        utenteRepository = new UtenteRepository();

        // Carica i dati utente
        String uid = authRepository.getCurrentUserId();
        if (uid != null) {
            utenteRepository.getUtente(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Utente u = documentSnapshot.toObject(Utente.class);
                        if (u != null) {
                            textNome.setText(u.getNome());
                            textCognome.setText(u.getCognome());
                            textEmail.setText(u.getEmail());
                            textTarga.setText(u.getTarga());
                        }
                    });
        }

        logoutButton.setOnClickListener(v -> {
            authRepository.logoutUser();
            startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
            finish();
        });

        pagaButton.setOnClickListener(v -> {
            // Apri PaymentActivity
            startActivity(new Intent(UserAreaActivity.this, PaymentActivity.class));
        });
    }
}
