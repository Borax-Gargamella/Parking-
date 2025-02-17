package com.contest.parking.presentation;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.contest.parking.R;
import com.contest.parking.data.model.Zone;
import com.contest.parking.data.repository.ZoneRepository;
import com.contest.parking.domain.UseCaseCaricaZone;
import com.contest.parking.presentation.utils.JsonUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class AreaSelectionActivity extends BaseActivity {

    private TextView textDescrizione, textNome;
    private ImageView imageViewLuogo;
    private FrameLayout frameLayoutContainer;
    private String luogoId, luogoDescrizione, nome;

    // Repository/Use Case
    private UseCaseCaricaZone useCaseCaricaZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActivityLayout(R.layout.activity_area_selection);

        imageViewLuogo = findViewById(R.id.imageViewSole);
        frameLayoutContainer = findViewById(R.id.frameLayoutContainer);
        textDescrizione = findViewById(R.id.itemLuogoDescrizione);
        textNome = findViewById(R.id.itemLuogoNome);

        // Recupera l'ID del luogo (passato tramite Intent)
        luogoId = getIntent().getStringExtra("luogoId");
        nome = getIntent().getStringExtra("nome");
        luogoDescrizione = getIntent().getStringExtra("descrizione");

        textDescrizione.setText(luogoDescrizione);
        textNome.setText(nome);

        // Ottieni l'ID della risorsa dinamicamente
        int imageResId = getResources().getIdentifier(luogoId, "drawable", getPackageName());
        if (imageResId != 0) {
            imageViewLuogo.setImageResource(imageResId);
        } else {
            Toast.makeText(this, "Immagine non trovata", Toast.LENGTH_SHORT).show();
        }

        // Carica il file JSON dagli assets
        useCaseCaricaZone = new UseCaseCaricaZone(new ZoneRepository());

        // Usa un ViewTreeObserver per attendere che l'ImageView abbia dimensioni definite
        imageViewLuogo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageViewLuogo.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Rimuovi eventuali overlay già presenti
                frameLayoutContainer.removeAllViews();
                frameLayoutContainer.addView(imageViewLuogo);

                int imageWidth = imageViewLuogo.getWidth();
                int imageHeight = imageViewLuogo.getHeight();

                // Ottieni la lista di zone dal Use Case
                List<Zone> zones = useCaseCaricaZone.execute(luogoId, getAssets());
                if (zones != null && !zones.isEmpty()) {
                    for (Zone zone : zones) {
                        // Calcola le coordinate in pixel usando il metodo del modello
                        Rect rect = zone.getPixelCoordinates(imageWidth, imageHeight);

                        // Crea una view trasparente per la zona (cliccabile)
                        View zoneView = new View(AreaSelectionActivity.this);
                        //zoneView.setBackgroundColor(Color.parseColor("#55FF0000"));
                        zoneView.setBackground(getDrawable(R.drawable.border));
                        zoneView.setClickable(true);
                        zoneView.setOnClickListener(v -> {
                            // Avvia la Activity di dettaglio passando l'ID della zona e del luogo
                            Intent intent = new Intent(AreaSelectionActivity.this, ParcheggioDettaglioActivity.class);
                            intent.putExtra("parcheggioId", zone.getId());
                            intent.putExtra("luogoId", luogoId);
                            startActivity(intent);
                        });

                        // Imposta i parametri per posizionare la view
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                rect.width(), rect.height());
                        params.leftMargin = rect.left;
                        params.topMargin = rect.top;
                        frameLayoutContainer.addView(zoneView, params);
                    }
                }
            }
        });
    }
}
