package com.contest.parking.presentation;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.contest.parking.R;
import com.contest.parking.data.model.Luogo;
import com.contest.parking.data.repository.LuogoRepository;
import com.contest.parking.presentation.adapter.LuogoAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {

    private RecyclerView recyclerViewLuoghi;
    private LuogoAdapter luogoAdapter;
    private LuogoRepository luogoRepository;

    private static final int REQUEST_CODE_NOTIFICATIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Carica il layout specifico per questa Activity nel container della BaseActivity
        setActivityLayout(R.layout.activity_main_content);

        // Controlla e richiedi il permesso per le notifiche (Android 13+)
        checkNotificationPermission();

        // Controlla se l'app può schedulare allarmi esatti (Android 12+)
        checkExactAlarmPermission();

        // Configurazione del RecyclerView
        recyclerViewLuoghi = findViewById(R.id.recyclerViewLuoghi);
        recyclerViewLuoghi.setLayoutManager(new LinearLayoutManager(this));

        luogoAdapter = new LuogoAdapter(this, new ArrayList<>());
        recyclerViewLuoghi.setAdapter(luogoAdapter);

        luogoRepository = new LuogoRepository();
        luogoRepository.getAllLuoghi().get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Luogo> luoghiList = queryDocumentSnapshots.toObjects(Luogo.class);
            luogoAdapter.setLuoghiList(luoghiList);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Errore nel caricamento dei luoghi", Toast.LENGTH_SHORT).show();
        });
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Verifica se il permesso POST_NOTIFICATIONS è concesso
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Richiedi il permesso
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_NOTIFICATIONS);
            }
        }
    }

    private void checkExactAlarmPermission() {
        // SCHEDULE_EXACT_ALARM è disponibile da Android 12 (API 31) in poi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                // L'app non può schedulare allarmi esatti, quindi indirizza l'utente nelle impostazioni.
                Toast.makeText(this, "Per favore, abilita gli allarmi esatti nelle impostazioni.", Toast.LENGTH_LONG).show();
                // Lancia l'intent per aprire la schermata di impostazioni per gli allarmi esatti.
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permesso notifiche concesso", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permesso notifiche negato. Le notifiche non funzioneranno.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
