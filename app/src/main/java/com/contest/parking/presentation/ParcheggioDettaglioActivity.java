package com.contest.parking.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.contest.parking.R;
import com.contest.parking.data.model.Parcheggio;
import com.contest.parking.data.repository.ParcheggioRepository;
import com.google.firebase.firestore.DocumentReference;

import java.io.IOException;
import java.io.InputStream;

public class ParcheggioDettaglioActivity extends BaseActivity {

    private ImageView imageViewArea;
    private TextView textId, textNome, textPostiTot, textPrezzo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inietta il layout specifico per ParcheggioDettaglioActivity nel container di BaseActivity
        setActivityLayout(R.layout.activity_luogo_detail);

        // Binding delle view
        imageViewArea = findViewById(R.id.imageViewArea);
        textId = findViewById(R.id.textId);
        textNome = findViewById(R.id.textNome);
        textPostiTot = findViewById(R.id.textPostiTot);
        textPrezzo = findViewById(R.id.textPrezzo);

        // Recupera l'ID del parcheggio passato tramite Intent (consiglio rinominarlo in "parcheggioId" per maggiore chiarezza)
        String idParcheggio = getIntent().getStringExtra("zoneId");
        if (idParcheggio == null || idParcheggio.isEmpty()) {
            Toast.makeText(this, "ID parcheggio non fornito", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Recupera i dati del parcheggio da Firestore
        ParcheggioRepository parcheggioRepository = new ParcheggioRepository();
        DocumentReference parcheggioDocRef = parcheggioRepository.getParcheggioDoc(idParcheggio);
        parcheggioDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Parcheggio p = documentSnapshot.toObject(Parcheggio.class);
                if (p != null) {
                    // Aggiorna la UI con i dati del parcheggio
                    textId.setText("ID: " + p.getId());
                    textNome.setText("Nome: " + p.getNome());
                    textPostiTot.setText("Posti Totali: " + p.getPostiTot());
                    textPrezzo.setText("Prezzo: " + p.getPrezzo() + " €");

                    // Carica l'immagine dall'assets in base al campo imageFolder (assumendo che il modello Parcheggio lo fornisca)
                    String imageFolder = p.getImageFolder();
                    if (imageFolder != null && !imageFolder.isEmpty()) {
                        // Costruisci il path dell'immagine: ad esempio "parcheggi/sole/parcheggio.png"
                        String imagePath = "parcheggi/" + imageFolder + "/parcheggio.png";
                        try {
                            InputStream is = getAssets().open(imagePath);
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            imageViewArea.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(ParcheggioDettaglioActivity.this, "Immagine non trovata", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else {
                Toast.makeText(ParcheggioDettaglioActivity.this, "Parcheggio non trovato", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(ParcheggioDettaglioActivity.this, "Errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}
