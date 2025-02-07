package com.contest.parking.presentation;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.contest.parking.data.model.Parcheggio;
import com.contest.parking.data.repository.ParcheggioRepository;
import com.contest.parking.presentation.adapter.ParcheggioAdapter;
import com.example.parking.R;

import java.util.ArrayList;
import java.util.List;

public class ParcheggiActivity extends AppCompatActivity {

    private ParcheggioRepository parcheggioRepository;
    private RecyclerView recyclerViewParcheggi;
    private ParcheggioAdapter parcheggioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcheggi);

        String luogoId = getIntent().getStringExtra("luogoId");

        recyclerViewParcheggi = findViewById(R.id.rvParcheggi);
        recyclerViewParcheggi.setLayoutManager(new LinearLayoutManager(this));

        parcheggioAdapter = new ParcheggioAdapter(new ArrayList<>(), this);
        recyclerViewParcheggi.setAdapter(parcheggioAdapter);

        parcheggioRepository = new ParcheggioRepository();

        // Esempio di query su Firestore (se hai un campo "luogoId" su Parcheggio)
        parcheggioRepository.getParcheggiByLuogo(luogoId)
                .get()
                .addOnSuccessListener(snap -> {
                    List<Parcheggio> parcheggi = snap.toObjects(Parcheggio.class);
                    parcheggioAdapter.setParcheggioList(parcheggi);
                });
    }
}
