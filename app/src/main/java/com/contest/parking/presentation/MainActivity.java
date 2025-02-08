package com.contest.parking.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.contest.parking.R;
import com.contest.parking.data.model.Luogo;
import com.contest.parking.data.repository.LuogoRepository;
import com.contest.parking.presentation.adapter.LuogoAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLuoghi;
    private LuogoAdapter luogoAdapter;
    private LuogoRepository luogoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurazione della Toolbar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        // Imposta il listener per il pulsante di navigazione se necessario (icona "casa")
        topAppBar.setNavigationOnClickListener(view -> {
            // Logica per tornare alla Home
            Toast.makeText(MainActivity.this, "Sei già nella Home", Toast.LENGTH_SHORT).show();
        });

        // Gestione del click sul menu (icona "omino" - ic_person)
        topAppBar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.action_user) {
                // Avvia la UserAreaActivity
                Intent intent = new Intent(MainActivity.this, UserAreaActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // Configurazione del RecyclerView
        recyclerViewLuoghi = findViewById(R.id.recyclerViewLuoghi);
        recyclerViewLuoghi.setLayoutManager(new LinearLayoutManager(this));

        luogoAdapter = new LuogoAdapter(this, new ArrayList<>());
        recyclerViewLuoghi.setAdapter(luogoAdapter);

        luogoRepository = new LuogoRepository();

        // Caricamento dei dati da Firestore
        luogoRepository.getAllLuoghi().get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Luogo> luoghiList = queryDocumentSnapshots.toObjects(Luogo.class);
            luogoAdapter.setLuoghiList(luoghiList);
        });
    }
}
