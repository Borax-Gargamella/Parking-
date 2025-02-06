package com.contest.parking.presentation;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.contest.parking.data.model.Luogo;
import com.contest.parking.data.repository.LuogoRepository;
import com.contest.parking.presentation.adapter.LuogoAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLuoghi;
    private LuogoAdapter luogoAdapter;
    private LuogoRepository luogoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // R.layout.activity_main is not defined

        recyclerViewLuoghi = findViewById(R.id.recyclerViewLuoghi);
        recyclerViewLuoghi.setLayoutManager(new LinearLayoutManager(this));

        luogoAdapter = new LuogoAdapter(this, new ArrayList<>());
        recyclerViewLuoghi.setAdapter(luogoAdapter);

        luogoRepository = new LuogoRepository();

        //Load from firestore
        luogoRepository.getAllLuoghi().get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Luogo> luoghiList = queryDocumentSnapshots.toObjects(Luogo.class);
            luogoAdapter.setLuoghiList(luoghiList);
        });

    }
}
