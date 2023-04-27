package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import javafx.beans.property.*;

public final class MapParameters {
    private IntegerProperty zoom = new SimpleIntegerProperty();
    private DoubleProperty minX = new SimpleDoubleProperty();
    private DoubleProperty minY = new SimpleDoubleProperty();
    private static final int MIN_ZOOM = 6;
    private static final int MAX_ZOOM = 19;

    public MapParameters(int zoom, double minX, double minY) {
        if (zoom < MIN_ZOOM || zoom > MAX_ZOOM) throw new IllegalArgumentException();
        this.zoom.set(zoom);
        this.minX.set(minX);
        this.minY.set(minY);
    }

    // zoom
    public ReadOnlyIntegerProperty zoomProperty() {
        return zoom;
    }

    public int getZoom() {
        return this.zoom.get();
    }

    //minX
    public ReadOnlyDoubleProperty minXProperty() {
        return this.minX;
    }

    public double getMinX() {
        return this.minX.get();
    }

    //minY
    public ReadOnlyDoubleProperty minYProperty() {
        return this.minY;
    }

    public double getMinY() {
        return this.minY.get();
    }

    public void scroll(double x, double y) {
        if(minX.get() + x >= 0 && minY.get() + y >= 0 ){// TODO && minX.get() + 2000 < Math.scalb(1,zoom.get()) && minY.get() + 2000 < Math.scalb(1, zoom.get())){
            minX.set(minX.get() + x);
            minY.set(minY.get() + y);
        }
    }

    public void changeZoomLevel(int zoomDifference) {
        if (zoom.get() + zoomDifference <= 19 && zoom.get() + zoomDifference >= 6) {
            zoom.set(Math2.clamp(MIN_ZOOM, zoom.get() + zoomDifference, MAX_ZOOM));
            minX.set((minX.get()) * Math.scalb(1, zoomDifference));
            minY.set((minY.get()) * Math.scalb(1, zoomDifference));
        }

    }
}
