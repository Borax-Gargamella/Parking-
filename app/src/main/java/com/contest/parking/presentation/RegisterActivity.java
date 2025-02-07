package com.contest.parking.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.contest.parking.R;
import com.contest.parking.data.model.Utente;
import com.contest.parking.data.repository.AuthRepository;
import com.contest.parking.data.repository.UtenteRepository;
import com.google.firebase.inappmessaging.model.Button;

public class RegisterActivity extends AppCompatActivity {

    private EditText editNome, editCognome, editTarga, editEmail, editPassword;
    private Button registerButton;
    private AuthRepository authRepository;
    private UtenteRepository utenteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = new AuthRepository();
        utenteRepository = new UtenteRepository();

        editNome = findViewById(R.id.editNome);
        editCognome = findViewById(R.id.editCognome);
        editTarga = findViewById(R.id.editTarga);
        editEmail = findViewById(R.id.editEmailReg);
        editPassword = findViewById(R.id.editPasswordReg);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String nome = editNome.getText().toString().trim();
            String cognome = editCognome.getText().toString().trim();
            String targa = editTarga.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            // 1. Registrazione con FirebaseAuth
            authRepository.registerUser(email, password, task -> {
                if (task.isSuccessful()) {
                    // 2. Ottenere l'UID
                    String uid = authRepository.getCurrentUserId();

                    // 3. Creare un oggetto Utente e salvarlo su Firestore
                    Utente utente = new Utente(uid, nome, cognome, targa, email);
                    utenteRepository.addUtente(utente)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Registrazione completata", Toast.LENGTH_SHORT).show();
                                // Vai alla MainActivity
                                goToMainActivity();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Impossibile salvare l'utente: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                } else {
                    Toast.makeText(this, "Errore registrazione: " + task.getException(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void goToMainActivity() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }
}
