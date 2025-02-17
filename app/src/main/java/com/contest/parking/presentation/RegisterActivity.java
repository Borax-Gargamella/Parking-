package com.contest.parking.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.contest.parking.R;
import com.contest.parking.domain.UseCaseRegistraUtente;
import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends BaseActivity {

    private EditText editNome, editCognome, editTarga, editEmail, editPassword, editPassword2;
    private MaterialButton registerButton;
    private UseCaseRegistraUtente useCaseRegistraUtente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inietta il layout specifico della RegisterActivity nel container della BaseActivity
        setActivityLayout(R.layout.activity_register);

        // Inizializza il use case
        useCaseRegistraUtente = new UseCaseRegistraUtente();

        // Associa le view
        editNome = findViewById(R.id.editNome);
        editCognome = findViewById(R.id.editCognome);
        editTarga = findViewById(R.id.editTarga);
        editEmail = findViewById(R.id.editEmailReg);
        editPassword = findViewById(R.id.editPasswordReg);
        editPassword2 = findViewById(R.id.editPasswordReg2);
        registerButton = findViewById(R.id.registerButton);

        // Imposta il listener per il bottone di registrazione
        registerButton.setOnClickListener(v -> {
            String nome = editNome.getText().toString().trim();
            String cognome = editCognome.getText().toString().trim();
            String targa = editTarga.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            String password2 = editPassword2.getText().toString().trim();

            // Chiamata al use case per registrare l'utente
            useCaseRegistraUtente.registraUtente(nome, cognome, targa, email, password, password2, new UseCaseRegistraUtente.OnRegisterCompleteListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(RegisterActivity.this, "Registrazione completata", Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(RegisterActivity.this, "Errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void goToMainActivity() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }
}
