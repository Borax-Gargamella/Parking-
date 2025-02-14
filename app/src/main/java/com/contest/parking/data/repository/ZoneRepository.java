package com.contest.parking.data.repository;

import android.content.res.AssetManager;
import com.contest.parking.data.model.Zone;
import com.contest.parking.presentation.utils.JsonUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ZoneRepository {

    /**
     * Legge il file JSON dagli assets e restituisce la lista di Zone per il luogo specificato.
     */
    public List<Zone> getZonesForLuogo(String luogoId, AssetManager assetManager) {
        List<Zone> zones = new ArrayList<>();
        JSONObject zonesJson = JsonUtils.loadJSONFromAsset(assetManager, "luogo_zones.json");
        if (zonesJson == null) return zones;
        JSONArray zonesArray = zonesJson.optJSONArray(luogoId);
        if (zonesArray != null) {
            for (int i = 0; i < zonesArray.length(); i++) {
                try {
                    JSONObject zoneJson = zonesArray.getJSONObject(i);
                    String id = zoneJson.optString("id");
                    double xPercent = zoneJson.optDouble("xPercent");
                    double yPercent = zoneJson.optDouble("yPercent");
                    double widthPercent = zoneJson.optDouble("widthPercent");
                    double heightPercent = zoneJson.optDouble("heightPercent");
                    zones.add(new Zone(id, xPercent, yPercent, widthPercent, heightPercent));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return zones;
    }
}

