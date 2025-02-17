package com.contest.parking.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.contest.parking.R;
import com.contest.parking.data.model.Luogo;
import com.contest.parking.data.repository.LuogoRepository;
import com.contest.parking.presentation.adapter.LuogoAdapter;
import com.contest.parking.presentation.utils.PostoAutoInserter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {

    private RecyclerView recyclerViewLuoghi;
    private LuogoAdapter luogoAdapter;
    private LuogoRepository luogoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Carica il layout specifico per questa Activity nel container della BaseActivity
        setActivityLayout(R.layout.activity_main_content);

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
}
