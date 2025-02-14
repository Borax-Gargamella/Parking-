package com.contest.parking.data.model;

import android.graphics.Rect;

// Rappresenta una zona di un'immagine.

public class Zone {
    private String id;
    private double xPercent;
    private double yPercent;
    private double widthPercent;
    private double heightPercent;

    public Zone(String id, double xPercent, double yPercent, double widthPercent, double heightPercent) {
        this.id = id;
        this.xPercent = xPercent;
        this.yPercent = yPercent;
        this.widthPercent = widthPercent;
        this.heightPercent = heightPercent;
    }

    // Getter
    public String getId() { return id; }
    public double getXPercent() { return xPercent; }
    public double getYPercent() { return yPercent; }
    public double getWidthPercent() { return widthPercent; }
    public double getHeightPercent() { return heightPercent; }

    /**
     * Calcola le coordinate in pixel in base alle dimensioni dell'immagine.
     */
    public Rect getPixelCoordinates(int imageWidth, int imageHeight) {
        int left = (int) (xPercent * imageWidth);
        int top = (int) (yPercent * imageHeight);
        int width = (int) (widthPercent * imageWidth);
        int height = (int) (heightPercent * imageHeight);
        return new Rect(left, top, left + width, top + height);
    }
}

