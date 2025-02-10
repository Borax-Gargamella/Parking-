package com.contest.parking.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.contest.parking.R;
import com.contest.parking.data.model.Parcheggio;
import com.contest.parking.data.repository.ParcheggioRepository;
import com.contest.parking.presentation.utils.JsonUtils;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class ParcheggioDettaglioActivity extends BaseActivity {

    private ImageView imageViewArea;
    private TextView textId, textNome, textPostiTot, textPrezzo;
    private FrameLayout frameLayoutContainer; // contenitore per immagine e overlay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inietta il layout specifico
        setActivityLayout(R.layout.activity_luogo_detail);

        // Binding delle view
        frameLayoutContainer = findViewById(R.id.frameLayoutContainer);
        imageViewArea = findViewById(R.id.imageViewArea);
        textId = findViewById(R.id.textId);
        textNome = findViewById(R.id.textNome);
        textPostiTot = findViewById(R.id.textPostiTot);
        textPrezzo = findViewById(R.id.textPrezzo);

        // Recupera l'ID del parcheggio passato tramite Intent (consiglio usare "parcheggioId")
        String idParcheggio = getIntent().getStringExtra("parcheggioId");
        String luogoId = getIntent().getStringExtra("luogoId");

        if (idParcheggio == null || idParcheggio.isEmpty()) {
            Toast.makeText(this, "ID parcheggio non fornito", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Recupera i dati del parcheggio da Firestore
        ParcheggioRepository parcheggioRepository = new ParcheggioRepository();
        DocumentReference parcheggioDocRef = parcheggioRepository.getParcheggioDoc(idParcheggio);
        parcheggioDocRef.get().addOnSuccessListener((DocumentSnapshot documentSnapshot) -> {
            if (documentSnapshot.exists()) {
                Parcheggio p = documentSnapshot.toObject(Parcheggio.class);
                if (p != null) {
                    // Aggiorna la UI
                    textId.setText("ID: " + p.getId());
                    textNome.setText("Nome: " + p.getNome());
                    textPostiTot.setText("Posti Totali: " + p.getPostiTot());
                    textPrezzo.setText("Prezzo: " + p.getPrezzo() + " €");

                    // Carica l'immagine dall'assets
                    String imageFolder = p.getImageFolder();
                    if (imageFolder != null && !imageFolder.isEmpty()) {
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

                    // Dopo che l'immagine è stata caricata e disposta, crea i bottoni per i posti auto.
                    // Usa il ViewTreeObserver per attendere che l'ImageView abbia dimensioni definite.
                    imageViewArea.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            imageViewArea.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int imageWidth = imageViewArea.getWidth();
                            int imageHeight = imageViewArea.getHeight();

                            // Costruisci il nome del file JSON
                            // Supponiamo che il file JSON si chiami "postiAuto" + imageFolder + ".json" (es. "postiAutosole.json")
                            String jsonFileName = "postiAuto" + luogoId + ".json";
                            JSONObject jsonObject = JsonUtils.loadJSONFromAsset(getAssets(), jsonFileName);
                            if (jsonObject == null) {
                                Toast.makeText(ParcheggioDettaglioActivity.this, "Errore nel caricamento del JSON", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Il JSON ha una struttura con un array "parkingAreas".
                            JSONArray parkingAreas = jsonObject.optJSONArray("parkingAreas");
                            if (parkingAreas == null) {
                                Toast.makeText(ParcheggioDettaglioActivity.this, "Nessun area di parcheggio nel JSON", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Cerca l'area di parcheggio che corrisponde all'ID del parcheggio (o un campo specifico, ad es. parkingId)
                            JSONObject areaObj = null;
                            for (int i = 0; i < parkingAreas.length(); i++) {
                                JSONObject obj = parkingAreas.optJSONObject(i);
                                if (obj != null && idParcheggio.equals(obj.optString("parkingId"))) {
                                    areaObj = obj;
                                    break;
                                }
                            }

                            if (areaObj == null) {
                                Toast.makeText(ParcheggioDettaglioActivity.this, "Area di parcheggio non trovata nel JSON", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Ottieni l'array di spots per questa area
                            JSONArray spotsArray = areaObj.optJSONArray("spots");
                            if (spotsArray == null) {
                                Toast.makeText(ParcheggioDettaglioActivity.this, "Nessun posto auto definito per questa area", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Per ogni spot, crea un bottone dinamico posizionato in base alle percentuali
                            for (int i = 0; i < spotsArray.length(); i++) {
                                JSONObject spotObj = spotsArray.optJSONObject(i);
                                if (spotObj == null) continue;

                                String spotId = spotObj.optString("id");
                                double xPercent = spotObj.optDouble("xPercent");
                                double yPercent = spotObj.optDouble("yPercent");
                                double widthPercent = spotObj.optDouble("widthPercent");
                                double heightPercent = spotObj.optDouble("heightPercent");

                                int left = (int) (xPercent * imageWidth);
                                int top = (int) (yPercent * imageHeight);
                                int width = (int) (widthPercent * imageWidth);
                                int height = (int) (heightPercent * imageHeight);

                                // Crea un bottone per lo spot (puoi usare MaterialButton o Button)
                                MaterialButton spotButton = new MaterialButton(ParcheggioDettaglioActivity.this);
                                spotButton.setText(spotId);
                                // Personalizza lo stile, ad es. colore trasparente di sfondo, testo in bianco, ecc.
                                spotButton.setBackgroundColor(Color.parseColor("#990000FF")); // colore semi-trasparente
                                spotButton.setTextColor(Color.WHITE);

                                // Imposta il click per aprire ad esempio una Activity di dettaglio del posto o eseguire un'altra azione
                                spotButton.setOnClickListener(v -> {
                                    // Esempio: avvia una nuova activity passando l'ID dello spot
                                    Intent intent = new Intent(ParcheggioDettaglioActivity.this, PrenotaPostoActivity.class);
                                    intent.putExtra("spotId", spotId);
                                    startActivity(intent);
                                });

                                // Imposta i parametri di layout per posizionare il bottone
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
                                params.leftMargin = left;
                                params.topMargin = top;
                                // Aggiungi il bottone al container (sovrapposto all'immagine)
                                frameLayoutContainer.addView(spotButton, params);
                            }
                        }
                    });
                }
            } else {
                Toast.makeText(ParcheggioDettaglioActivity.this, "Parcheggio non trovato", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(ParcheggioDettaglioActivity.this, "Errore: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}
