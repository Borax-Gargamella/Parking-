package com.contest.parking.presentation;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.contest.parking.R;
import com.contest.parking.data.model.Parcheggio;
import com.contest.parking.data.repository.ParcheggioRepository;
import com.contest.parking.presentation.adapter.ParcheggioAdapter;

import java.util.ArrayList;
import java.util.List;

public class ParcheggiActivity extends AppCompatActivity {

    private ParcheggioRepository parcheggioRepository;
    private RecyclerView recyclerViewParcheggi;
    private ParcheggioAdapter parcheggioAdapter;
    private String luogoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcheggi);

        recyclerViewParcheggi = findViewById(R.id.rvParcheggi);
        recyclerViewParcheggi.setLayoutManager(new LinearLayoutManager(this));

        parcheggioAdapter = new ParcheggioAdapter(new ArrayList<>(), this);
        recyclerViewParcheggi.setAdapter(parcheggioAdapter);

        parcheggioRepository = new ParcheggioRepository();

        luogoId = getIntent().getStringExtra("luogoId");
        // Query Firestore
        parcheggioRepository.getParcheggiByLuogo(luogoId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Parcheggio> parcheggi = querySnapshot.toObjects(Parcheggio.class);
                    parcheggioAdapter.setParcheggioList(parcheggi);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
