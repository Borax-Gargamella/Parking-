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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.inappmessaging.model.Button;

public class RegisterActivity extends BaseActivity {

    private EditText editNome, editCognome, editTarga, editEmail, editPassword;
    private MaterialButton registerButton;
    private AuthRepository authRepository;
    private UtenteRepository utenteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inietta il layout specifico della RegisterActivity nel container della BaseActivity
        setActivityLayout(R.layout.activity_register);

        // Inizializza i repository
        authRepository = new AuthRepository();
        utenteRepository = new UtenteRepository();

        // Associa le view
        editNome = findViewById(R.id.editNome);
        editCognome = findViewById(R.id.editCognome);
        editTarga = findViewById(R.id.editTarga);
        editEmail = findViewById(R.id.editEmailReg);
        editPassword = findViewById(R.id.editPasswordReg);
        registerButton = findViewById(R.id.registerButton);

        // Imposta il listener per il bottone di registrazione
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
                                Toast.makeText(RegisterActivity.this, "Registrazione completata", Toast.LENGTH_SHORT).show();
                                // Vai alla MainActivity
                                goToMainActivity();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(RegisterActivity.this, "Impossibile salvare l'utente: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "Errore registrazione: " + task.getException(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void goToMainActivity() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }
}
