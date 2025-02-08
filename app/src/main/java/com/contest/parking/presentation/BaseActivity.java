package com.contest.parking.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import com.contest.parking.R;
import com.google.android.material.button.MaterialButton;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Imposta i bottoni della topbar
        MaterialButton btnHome = findViewById(R.id.btnHome);
        MaterialButton btnUser = findViewById(R.id.btnUser);

        btnHome.setOnClickListener(v -> {
            // Se si è già nella Home, mostra un Toast
            if (this instanceof MainActivity) {
                Toast.makeText(BaseActivity.this, "Sei già nella Home", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(BaseActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnUser.setOnClickListener(v -> {
            // Avvia la UserAreaActivity
            if (this instanceof UserAreaActivity) {
                Toast.makeText(BaseActivity.this, "Sei già nella tua area", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(BaseActivity.this, UserAreaActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Questo metodo serve per caricare il layout specifico della Activity
     * all'interno del container definito in activity_base.xml.
     */
    protected void setActivityLayout(@LayoutRes int layoutResID) {
        FrameLayout container = findViewById(R.id.container);
        getLayoutInflater().inflate(layoutResID, container, true);
    }
}
