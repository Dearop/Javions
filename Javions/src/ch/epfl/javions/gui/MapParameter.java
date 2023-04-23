package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;

public final class MapParameter {
    private IntegerProperty zoom;
    private DoubleProperty minX;
    private DoubleProperty minY;
    private static final int MIN_ZOOM = 6;
    private static final int MAX_ZOOM = 19;

    public MapParameter(int zoom, double minX, double minY) {
        if (zoom < MIN_ZOOM || zoom > MAX_ZOOM) throw new IllegalArgumentException();
        this.zoom.set(zoom);
        this.minX.set(minX);
        this.minY.set(minY);
    }

    // zoom
    public ReadOnlyIntegerProperty zoomProperty(){
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

    public void scroll(int x, int y){
        minX.set(minX.get()+x);
        minY.set(minY.get()+y);
    }

    public void changeZoomLevel(int zoomDifference){
        zoom.set(Math2.clamp(MIN_ZOOM, zoom.get()+zoomDifference , MAX_ZOOM));

        // TODO: 23.04.23 we not sure!
        minX.set((minX.get()/Math.scalb(1, 8+zoom.get()-zoomDifference))*Math.scalb(1, 8+zoom.get()));
        minY.set((minY.get()/Math.scalb(1, 8+zoom.get() - zoomDifference))*Math.scalb(1, 8+zoom.get()));
    }
}
