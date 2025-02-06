package com.contest.parking.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.contest.parking.data.repository.AuthRepository;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginButton, registerButton;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // R.layout.activity_login is not defined

        authRepository = new AuthRepository();

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener((View v) -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            doLogin(email, password);
        });

        registerButton.setOnClickListener((View v) -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void doLogin(String email, String password) {
        authRepository.loginUser(email, password, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login OK", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login fallito: " + task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
