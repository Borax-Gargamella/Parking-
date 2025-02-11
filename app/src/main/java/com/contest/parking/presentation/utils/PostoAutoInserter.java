package com.contest.parking.presentation.utils;

import android.content.res.AssetManager;
import android.util.Log;
import com.contest.parking.data.model.PostoAuto;
import com.contest.parking.data.repository.PostoAutoRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import org.json.JSONArray;
import org.json.JSONObject;

public class PostoAutoInserter {

    private AssetManager assetManager;
    private PostoAutoRepository postoAutoRepository;

    public PostoAutoInserter(AssetManager assetManager) {
        this.assetManager = assetManager;
        this.postoAutoRepository = new PostoAutoRepository();
    }

    /**
     * Legge il file JSON (ad es. "postiAutoSole.json") e inserisce nel database i posti auto.
     *
     * Il JSON deve avere una struttura simile a:
     * {
     *   "parkingAreas": [
     *     {
     *       "parkingId": "areaA",
     *       "spots": [
     *         {
     *           "id": "A1",
     *           "xPercent": 0.04819,
     *           "yPercent": 0.08305,
     *           "widthPercent": 0.15964,
     *           "heightPercent": 0.10554
     *         },
     *         ...
     *       ]
     *     },
     *     {
     *       "parkingId": "areaB",
     *       "spots": [ ... ]
     *     }
     *   ]
     * }
     *
     * Tutti i posti avranno categoria "normale" e statoOccupato false di default.
     *
     * @param jsonFileName il nome del file JSON da caricare dagli assets.
     */
    public void inserisciPostiAuto(String jsonFileName) {
        JSONObject json = JsonUtils.loadJSONFromAsset(assetManager, jsonFileName);
        if (json == null) {
            Log.e("PostoAutoInserter", "Errore nel caricamento del JSON");
            return;
        }

        JSONArray parkingAreas = json.optJSONArray("parkingAreas");
        if (parkingAreas == null) {
            Log.e("PostoAutoInserter", "Nessuna area di parcheggio trovata nel JSON");
            return;
        }

        // Itera sulle aree di parcheggio
        for (int i = 0; i < parkingAreas.length(); i++) {
            JSONObject areaObj = parkingAreas.optJSONObject(i);
            if (areaObj == null) continue;

            String parkingId = areaObj.optString("parkingId");
            JSONArray spots = areaObj.optJSONArray("spots");
            if (spots == null) continue;

            // Itera sui posti (spots)
            for (int j = 0; j < spots.length(); j++) {
                JSONObject spotObj = spots.optJSONObject(j);
                if (spotObj == null) continue;

                String spotId = spotObj.optString("id");
                double xPercent = spotObj.optDouble("xPercent");
                double yPercent = spotObj.optDouble("yPercent");
                double widthPercent = spotObj.optDouble("widthPercent");
                double heightPercent = spotObj.optDouble("heightPercent");

                // Crea un oggetto PostoAuto e imposta i campi
                PostoAuto posto = new PostoAuto();
                posto.setId(spotId);  // Usa l'ID definito nel JSON come ID del documento
                posto.setCategoria("normale");
                // Categoria fissa per tutti
                  // Posto libero di default

                // Inserisci il posto auto nel database usando il metodo che rispetta l'ID specificato
                postoAutoRepository.addPostoAutoWithId(posto)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("PostoAutoInserter", "Posto auto inserito: " + spotId);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("PostoAutoInserter", "Errore nell'inserimento del posto auto: " + spotId, e);
                        });
            }
        }
    }
}
