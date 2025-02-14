package com.contest.parking.presentation.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

public class ImageCache {
    private LruCache<String, Bitmap> mMemoryCache;

    public ImageCache() {
        // Calcola la quantità di memoria disponibile in kilobyte
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Usa 1/8 della memoria disponibile per la cache
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // Restituisce la dimensione della bitmap in kilobyte
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    // Aggiunge una bitmap alla cache
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    // Recupera una bitmap dalla cache
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}

