package com.contest.parking.presentation.utils;

import android.content.res.AssetManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class JsonUtils {
    public static JSONObject loadJSONFromAsset(AssetManager assetManager, String filename) {
        String jsonString;
        try {
            InputStream is = assetManager.open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
            return new JSONObject(jsonString);
        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
