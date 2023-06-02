package ch.epfl.javions.gui;

import ch.epfl.javions.Math2;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.WebMercator;
import javafx.beans.property.*;
import javafx.geometry.Point2D;

/**
 * A class representing the parameters of a map, including zoom level and minimum X and Y coordinates.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class MapParameters {

    // The zoom level of the map
    private final IntegerProperty zoom = new SimpleIntegerProperty();

    // The minimum X coordinate of the map, which is on the left side of the canvas
    private final DoubleProperty minX = new SimpleDoubleProperty();

    // The minimum Y coordinate of the map, which is located at the top of the canvas
    private final DoubleProperty minY = new SimpleDoubleProperty();
    private static final int MIN_ZOOM = 6; // The minimum zoom level allowed
    private static final int MAX_ZOOM = 19; // The maximum zoom level allowed

    private static final int ALLOWED_ZOOM_CHANGE = 1;
    /**
     * Constructs a MapParameters object with the specified zoom level and minimum X and Y coordinates.
     * It's parameters tell us precisely where the user will start.
     *
     * @param zoomLevel The zoom level of the map on start, must be between MIN_ZOOM and MAX_ZOOM inclusive.
     * @param minX      The minimum X coordinate of the map on start.
     * @param minY      The minimum Y coordinate of the map on start.
     * @throws IllegalArgumentException If the zoom level is not between MIN_ZOOM and MAX_ZOOM.
     */
    public MapParameters(int zoomLevel, double minX, double minY) {
        Preconditions.checkArgument(zoomLevel >= MIN_ZOOM && zoomLevel <= MAX_ZOOM);
        Preconditions.checkArgument(minX >= 0);
        Preconditions.checkArgument(minY >= 0);
        this.zoom.set(zoomLevel);
        this.minX.set(minX);
        this.minY.set(minY);
    }

    /**
     * @return A read-only IntegerProperty representing the zoom level of the map.
     */
    public ReadOnlyIntegerProperty zoomProperty() {
        return this.zoom;
    }

    /**
     * @return The zoom level of the map.
     */
    public int getZoom() {
        return this.zoom.get();
    }

    /**
     * @return A read-only DoubleProperty representing the minimum X coordinate of the map.
     */
    public ReadOnlyDoubleProperty minXProperty() {
        return this.minX;
    }

    /**
     * @return The minimum X coordinate of the map.
     */
    public double getMinX() {
        return this.minX.get();
    }

    /**
     * @return A read-only DoubleProperty representing the minimum Y coordinate of the map.
     */
    public ReadOnlyDoubleProperty minYProperty() {
        return this.minY;
    }

    /**
     * @return The minimum Y coordinate of the map.
     */
    public double getMinY() {
        return this.minY.get();
    }

    /**
     * Scrolls the map by the specified amount in the X and Y directions.
     *
     * @param x The amount to scroll in the X direction.
     * @param y The amount to scroll in the Y direction.
     */
    public void scroll(double x, double y) {
        minX.set(minX.get() + x);
        minY.set(minY.get() + y);
    }

    /**
     * Changes the zoom level of the map by the specified amount.
     *
     * @param zoomDifference The amount to change the zoom level by, can be positive or negative.
     */
    public void changeZoomLevel(int zoomDifference) {

        zoomDifference = (zoomDifference > 0) ? ALLOWED_ZOOM_CHANGE : -ALLOWED_ZOOM_CHANGE;

        int zoomBoundary = Math2.clamp(MIN_ZOOM, zoom.get() + zoomDifference, MAX_ZOOM);

        if (zoom.get() != zoomBoundary) {
            zoom.set(zoomBoundary);
            minX.set((minX.get()) * Math.scalb(1, zoomDifference));
            minY.set((minY.get()) * Math.scalb(1, zoomDifference));
        }
    }
}
