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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.contest.parking.data.repository.AuthRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends BaseActivity {

    // Usa TextInputEditText per i campi email e password
    private TextInputEditText editEmail, editPassword;
    private MaterialButton loginButton, registerButton;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Inietta il layout specifico per LoginActivity nel container di BaseActivity
        setActivityLayout(R.layout.activity_login);

        authRepository = new AuthRepository();

        // Se l'utente è già loggato, vai direttamente alla MainActivity
        if (authRepository.getCurrentUser() != null) {
            goToMainActivity();
        }

        // Binding delle view usando gli ID dei campi editabili
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Imposta il listener per il bottone Login
        loginButton.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            doLogin(email, password);
        });

        // Imposta il listener per il bottone Registrati
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void doLogin(String email, String password) {
        authRepository.loginUser(email, password, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login OK", Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Login fallito: " + task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void goToMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
