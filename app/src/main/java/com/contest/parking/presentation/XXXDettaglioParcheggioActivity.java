package com.contest.parking.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.contest.parking.R;
import com.contest.parking.data.model.Parcheggio;
import com.contest.parking.data.repository.AuthRepository;
import com.contest.parking.data.repository.ParcheggioRepository;
import com.contest.parking.data.repository.PostoAutoRepository;
import com.contest.parking.data.repository.StoricoRepository;
import com.contest.parking.domain.UseCasePrenotaPosto;
import com.google.android.material.button.MaterialButton;

public class XXXDettaglioParcheggioActivity extends BaseActivity {

    private TextView textNomeParcheggio, textDescrizione, textPrezzo;
    private MaterialButton btnPrenota, btnCompletaPagamento;

    private ParcheggioRepository parcheggioRepo;
    private UseCasePrenotaPosto useCasePrenota;
    private AuthRepository authRepository;

    private String parcheggioId;
    private Parcheggio parcheggioCorrente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inietta il layout specifico per DettaglioParcheggioActivity
        setActivityLayout(R.layout.activity_dettaglio_parcheggio);

        textNomeParcheggio = findViewById(R.id.textNomeParcheggioDettaglio);
        textDescrizione = findViewById(R.id.textDescrizioneParcheggio);
        textPrezzo = findViewById(R.id.textPrezzoDettaglio);

        btnPrenota = findViewById(R.id.buttonPrenota);
        btnCompletaPagamento = findViewById(R.id.buttonCompletaPagamento);

        // Inizializza i repository e UseCase
        parcheggioRepo = new ParcheggioRepository();
        useCasePrenota = new UseCasePrenotaPosto(new PostoAutoRepository(), new StoricoRepository());
        authRepository = new AuthRepository();

        // Ricevi l'ID del parcheggio tramite Intent
        parcheggioId = getIntent().getStringExtra("parcheggioId");

        // Carica i dettagli del parcheggio
        loadParcheggioDetails(parcheggioId);

        // Click su Prenota
        btnPrenota.setOnClickListener(v -> {
            // Esempio: prenota un posto generico
            String samplePostoId = "somePostoId";
            String userId = authRepository.getCurrentUserId();
            String targa = "AB123CD"; // Se disponibile
            double prezzo = parcheggioCorrente != null ? parcheggioCorrente.getPrezzo() : 2.5;

            useCasePrenota.prenotaPosto(samplePostoId, userId, targa, prezzo, aVoid -> {
                Toast.makeText(this, "Posto prenotato con successo!", Toast.LENGTH_SHORT).show();
            });
        });

        // Click su "Completa Pagamento"
        btnCompletaPagamento.setOnClickListener(v -> {
            Intent i = new Intent(XXXDettaglioParcheggioActivity.this, CompletaPagamentoActivity.class);
            i.putExtra("postoId", "somePostoId"); // Passa le informazioni necessarie
            startActivity(i);
        });
    }

    private void loadParcheggioDetails(String parcheggioId) {
        parcheggioRepo.getParcheggioDoc(parcheggioId).get()
                .addOnSuccessListener(docSnap -> {
                    if (docSnap.exists()) {
                        Parcheggio p = docSnap.toObject(Parcheggio.class);
                        parcheggioCorrente = p;
                        if (p != null) {
                            textNomeParcheggio.setText(p.getNome());
                            textDescrizione.setText("Qui puoi parcheggiare in sicurezza…");
                            textPrezzo.setText("Prezzo orario: " + p.getPrezzo() + " €");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Errore caricamento parcheggio: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
