package com.contest.parking.presentation;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.contest.parking.R;
import com.contest.parking.presentation.utils.JsonUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class AreaSelectionActivity extends BaseActivity {

    private ImageView imageViewLuogo;
    private FrameLayout frameLayoutContainer;
    private String luogoId;  // Ad esempio "luogo1"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActivityLayout(R.layout.activity_area_selection);

        imageViewLuogo = findViewById(R.id.imageViewSole);
        frameLayoutContainer = findViewById(R.id.frameLayoutContainer);

        // Recupera l'ID del luogo (passato tramite Intent)
        luogoId = getIntent().getStringExtra("luogoId");  // Es. "luogo1"


        // Carica il file JSON dagli assets
        JSONObject zonesJson = JsonUtils.loadJSONFromAsset(getAssets(), "luogo_zones.json");
        if (zonesJson == null) return;

        // Usa un ViewTreeObserver per attendere che l'ImageView abbia dimensioni definite
        imageViewLuogo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageViewLuogo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int imageWidth = imageViewLuogo.getWidth();
                int imageHeight = imageViewLuogo.getHeight();

                // Estrai l'array di zone per il luogo corrente
                JSONArray zonesArray = zonesJson.optJSONArray(luogoId);
                if (zonesArray != null) {
                    for (int i = 0; i < zonesArray.length(); i++) {
                        JSONObject zone;
                        try {
                            zone = zonesArray.getJSONObject(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }

                        String parcheggioId = zone.optString("id");
                        double xPercent = zone.optDouble("xPercent");
                        double yPercent = zone.optDouble("yPercent");
                        double widthPercent = zone.optDouble("widthPercent");
                        double heightPercent = zone.optDouble("heightPercent");

                        // Calcola coordinate in pixel
                        int left = (int) (xPercent * imageWidth);
                        int top = (int) (yPercent * imageHeight);
                        int width = (int) (widthPercent * imageWidth);
                        int height = (int) (heightPercent * imageHeight);

                        // Crea una view trasparente per la zona
                        View zoneView = new View(AreaSelectionActivity.this);
                        // Imposta un colore semi-trasparente per debug (modifica o rimuovi per produzione)
                        zoneView.setBackgroundColor(Color.parseColor("#55FF0000"));
                        zoneView.setOnClickListener(v -> {
                            // Avvia la Activity di dettaglio passando l'ID della zona e del luogo
                            Intent intent = new Intent(AreaSelectionActivity.this, ParcheggioDettaglioActivity.class);
                            intent.putExtra("parcheggioId", parcheggioId);
                            intent.putExtra("luogoId", luogoId);
                            startActivity(intent);
                        });

                        // Aggiungi la view al FrameLayout
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
                        params.leftMargin = left;
                        params.topMargin = top;
                        frameLayoutContainer.addView(zoneView, params);
                    }
                }
            }
        });
    }
}
