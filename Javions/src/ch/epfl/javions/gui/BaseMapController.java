package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * The BaseMapController class is responsible for managing the display of the map on the screen.
 * It uses a TileManager from which it receives the map tiles and displays them afterwards.
 * It listens for changes in the map parameters and updates the map display accordingly.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class BaseMapController {
    private TileManager tileManager;
    private MapParameters parameter;
    private boolean redrawNeeded;
    private Canvas canvas;
    private Pane mapPane;
    private static final int TILE_SIZE = 256;
    private static final int SCROLL_TIME = 200;
    private final ObjectProperty<Point2D> scroller;

    /**
     * Constructor for BaseMapController.
     *
     * @param tileManager TileManager to load and display the map tiles.
     * @param parameter   MapParameters object to store and update map parameters.
     */
    public BaseMapController(TileManager tileManager, MapParameters parameter) {
        this.tileManager = tileManager;
        this.parameter = parameter;
        this.canvas = new Canvas();
        this.mapPane = new Pane(canvas);
        this.scroller = new SimpleObjectProperty<>(new Point2D(0, 0));

        // Add bindings to ensure the canvas resizes to fit the mapPane
        addBindings();

        // Add listeners to listen for changes in the map parameters and update the map display accordingly
        addListeners();

        // Add event handlers to handle user interactions with the map (scroll, drag, etc.)
        addEventManagers();
    }

    /**
     * Add bindings to ensure that the canvas resizes to fit the mapPane.
     */
    private void addBindings() {
        canvas.widthProperty().bind(mapPane.widthProperty());
        canvas.heightProperty().bind(mapPane.heightProperty());
    }

    /**
     * Add listeners to listen for changes in the map parameters, this includes changes in the zoom level,
     * changes in the x and y position, as well as changes in the width and height of the canvas.
     * It then updates the map display accordingly.
     */
    private void addListeners() {

        // Add listener to listen for changes in the scene and redraw the map if needed
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        canvas.widthProperty().addListener(e -> redrawOnNextPulse());
        canvas.heightProperty().addListener(e -> redrawOnNextPulse());

        parameter.zoomProperty().addListener(e -> redrawOnNextPulse());
        parameter.minXProperty().addListener(e -> redrawOnNextPulse());
        parameter.minYProperty().addListener(e -> redrawOnNextPulse());
    }

    /**
     * Add event handlers to handle user interactions with the map (scroll, drag, etc.).
     */
    private void addEventManagers() {

        // Set up a timer to handle the scroll event so that it doesn't fire too frequently
        LongProperty minScrollTime = new SimpleLongProperty();

        // Add scroll event handler to handle zooming in/out of the map
        mapPane.setOnScroll(e -> {
            double zoomDelta = e.getDeltaY();
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + SCROLL_TIME);

            /**
             * Update the map parameters to zoom in/out. Because the method changeZoomLevel
             * only works in the top left corner of the canvas, we need to change the top left corner
             * to be the position of the mouse with the scroll method.
             * Once the zoomLevel has been changed we then need to revert the scroll from before
             * so that there is no shift of the map.
             */
            parameter.scroll(e.getX(), e.getY());
            parameter.changeZoomLevel((int) zoomDelta);
            parameter.scroll(-e.getX(), -e.getY());

            // Set the scroller property to the current mouse position
            scroller.set(new Point2D(e.getX(), e.getY()));
        });

        // Handle mouse press events
        mapPane.setOnMousePressed(e -> {
            // Set the scroller property to the current mouse position
            scroller.set(new Point2D(e.getX(), e.getY()));
        });

        // Handle mouse drag events
        mapPane.setOnMouseDragged(e -> {

            // Scroll the map based on the distance dragged
            parameter.scroll((int) (scroller.get().getX() - e.getX()), (int) (scroller.get().getY()) - e.getY());

            // Set the scroller property to the current mouse position
            scroller.set(new Point2D(e.getX(), e.getY()));
        });

        // Handle mouse release events
        mapPane.setOnMouseReleased(e -> {

            // Set the scroller property to the current mouse position
            scroller.set(new Point2D(e.getX(), e.getY()));
        });
    }


    /**
     * @return the pane containing the canvas on which the whole map is drawn
     */
    public Pane pane() {
        return mapPane;
    }

    /**
     * Centers the map on the specified geographical position.
     * This method is called when an aircraft is double-clicked on the aircraft table
     *
     * @param pos The geographical position to center the map on.
     */
    public void centerOn(GeoPos pos) {
        double xTopPosition = WebMercator.x(parameter.getZoom(), pos.longitude());
        double yTopPosition = WebMercator.y(parameter.getZoom(), pos.latitude());

        // Scroll to the top-left corner of the map
        parameter.scroll(-parameter.getMinX(), -parameter.getMinY());

        // Scroll to the center of the specified position
        parameter.scroll((int) ((xTopPosition - mapPane.getWidth() / 2)),
                (int) ((yTopPosition - mapPane.getHeight() / 2)));
    }

    /**
     * This method calculates the necessary tiles to cover the visible area of the map and draws them on the canvas.
     * It first calculates the minimum and maximum tiles in the x and y directions that are
     * inside the visible area of the map. Then, for each of these tiles, it obtains the corresponding
     * image from the tile manager and draws it on the canvas at the appropriate position.
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext graphics = canvas.getGraphicsContext2D();

        // Calculate the minimum and maximum tiles in the x and y directions that intersect the visible area of the map
        double minTileX = tilePositionCalculator(parameter.getMinX());
        double maxTileX = tilePositionCalculator(parameter.getMinX() + mapPane.getWidth());

        double minTileY = tilePositionCalculator(parameter.getMinY());
        double maxTileY = tilePositionCalculator(parameter.getMinY() + mapPane.getWidth());

        // Determine the initial y position on the canvas for the first tile to be drawn
        int yCoordinateShiftedTile = (int) -parameter.getMinY() % TILE_SIZE;
        for (int y = (int) minTileY; y < (int) maxTileY + 1; y++) {

            // Determine the initial x position on the canvas for the first tile to be drawn in the current row
            int xCoordinateShiftedTile = (int) -parameter.getMinX() % TILE_SIZE;
            for (int x = (int) minTileX; x < (int) maxTileX + 1; x++) {

                try {
                    /**
                     * Obtain the image for the current tile from the tile manager
                     * and draw it on the canvas at the appropriate position
                     */
                    graphics.drawImage(tileManager.imageForTileAt(new TileManager.TileId(parameter.getZoom(), x, y))
                            , xCoordinateShiftedTile, yCoordinateShiftedTile);
                } catch (IOException ignored) {
                    // If an exception is thrown while obtaining the image for the tile,
                    // ignore it and continue to the next tile
                }   // Update the x position on the canvas for the next tile to be drawn in the current row
                xCoordinateShiftedTile += TILE_SIZE;
            }
            // Update the y position on the canvas for the first tile to be drawn in the next row
            yCoordinateShiftedTile += TILE_SIZE;
        }
    }

    /**
     * This method calculates the position of a tile on the map given its position on the screen.
     * It simply divides the screen position by the size of a tile to obtain the corresponding tile position.
     *
     * @param screenPosition the position of the tile on the screen
     * @return the position of the tile on the map
     */
    private double tilePositionCalculator(double screenPosition) {
        return screenPosition / TILE_SIZE;
    }

    /**
     * This method sets the redrawNeeded flag to true and requests
     * a redraw of the canvas on the next pulse of the JavaFX application thread.
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}