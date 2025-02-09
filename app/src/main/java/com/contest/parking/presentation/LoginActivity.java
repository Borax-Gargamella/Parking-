package com.contest.parking.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.contest.parking.R;
import com.contest.parking.domain.UseCaseLogInUtente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.contest.parking.data.repository.AuthRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends BaseActivity {

    private TextInputEditText editEmail, editPassword;
    private MaterialButton loginButton, registerButton;
    private AuthRepository authRepository;
    private UseCaseLogInUtente useCaseLogInUtente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inietta il layout specifico per LoginActivity nel container di BaseActivity
        setActivityLayout(R.layout.activity_login);

        authRepository = new AuthRepository();
        useCaseLogInUtente = new UseCaseLogInUtente();

        // Se l'utente è già loggato, vai direttamente alla MainActivity
        if (authRepository.getCurrentUser() != null) {
            goToMainActivity();
        }

        // Binding delle view
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Imposta il listener per il bottone Login
        loginButton.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            // Invoca il use case per il login
            useCaseLogInUtente.logIn(email, password, new UseCaseLogInUtente.OnLogInCompleteListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(LoginActivity.this, "Login OK", Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(LoginActivity.this, "Login fallito: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        // Imposta il listener per il bottone Registrati
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void goToMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
