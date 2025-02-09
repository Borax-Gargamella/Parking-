package com.contest.parking.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.contest.parking.R;

import java.io.IOException;
import java.io.InputStream;

public class ParcheggioDettaglioActivity extends BaseActivity {
    private ImageView imageViewArea;
    private TextView textId, textNome, textPostiTot, textPrezzo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inietta il layout specifico nel container della BaseActivity
        setActivityLayout(R.layout.activity_luogo_detail);

        imageViewArea = findViewById(R.id.imageViewArea);
        textId = findViewById(R.id.textId);
        textNome = findViewById(R.id.textNome);
        textPostiTot = findViewById(R.id.textPostiTot);
        textPrezzo = findViewById(R.id.textPrezzo);

        // Recupera i dati passati tramite Intent
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String nome = intent.getStringExtra("nome");
        int postiTot = intent.getIntExtra("postiTot", 0);
        double prezzo = intent.getDoubleExtra("prezzo", 0.0);
        String imageFolder = intent.getStringExtra("imageFolder"); // ad esempio "sole"

        // Imposta i dati testuali
        textId.setText("ID: " + id);
        textNome.setText("Nome: " + nome);
        textPostiTot.setText("Posti Totali: " + postiTot);
        textPrezzo.setText("Prezzo: " + prezzo + " €");

        // Carica l'immagine dall'assets
        if (imageFolder != null && !imageFolder.isEmpty()) {
            // Costruisci il path: ad esempio "parcheggi/sole/area.png"
            String imagePath = "parcheggi/" + imageFolder + "/area.png";
            try {
                InputStream is = getAssets().open(imagePath);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                imageViewArea.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
