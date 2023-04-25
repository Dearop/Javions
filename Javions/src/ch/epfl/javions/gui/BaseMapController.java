package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;


import java.io.IOException;

public final class BaseMapController {
    private TileManager tileManager;
    private MapParameters parameter;
    private boolean redrawNeeded;
    private Canvas canvas;
    private Pane mapPane;
    private static final int TILE_SIZE = 256;
    private double clickedXPosition;
    private double clickedYPosition;
    private boolean dragging = false;
    private long lastRedrawTime = 0;
    private static final long MIN_REDRAW_INTERVAL = 32; // Minimum redraw interval in milliseconds (60 FPS)


    public BaseMapController(TileManager tileManager, MapParameters parameter) {
        this.tileManager = tileManager;
        this.parameter = parameter;
        canvas = new Canvas();
        mapPane = new Pane(canvas);

        addBindings();
        addListeners();
        addEventManagers();
    }

    private void addBindings(){
        canvas.widthProperty().bind(mapPane.widthProperty());
        canvas.heightProperty().bind(mapPane.heightProperty());

    }

    private void addListeners(){
        canvas.widthProperty().addListener(e -> redrawOnNextPulse());
        canvas.heightProperty().addListener(e -> redrawOnNextPulse());
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        parameter.zoomProperty().addListener(e -> redrawOnNextPulse());
        parameter.minXProperty().addListener(e-> redrawOnNextPulse());
        parameter.minYProperty().addListener(e -> redrawOnNextPulse());
    }

    private void addEventManagers(){
        mapPane.setOnScroll(e -> changeZoom());
        mapPane.setOnMousePressed(e -> {
            clickedXPosition = e.getX();
            clickedYPosition = e.getY();
            dragging = true;
        });

        mapPane.setOnMouseDragged(e -> {
            parameter.scroll((int) Math.rint((clickedXPosition - e.getX())/20), (int) Math.rint((clickedYPosition - e.getY())/20));
        });

        mapPane.setOnMouseReleased(e -> {
            dragging = false;
        });
    }

    public Pane pane() {
        return mapPane;
    }

    public void centerOn(GeoPos pos) {

    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext graphics = canvas.getGraphicsContext2D();

        int minTileX = tilePositionCalculator(parameter.getMinX());
        int maxTileX = tilePositionCalculator(parameter.getMinX() + mapPane.getWidth());

        int minTileY = tilePositionCalculator(parameter.getMinY());
        int maxTileY = tilePositionCalculator(parameter.getMinY() + mapPane.getWidth());

        int windowX = 0;
        int windowY = 0;
        for (int y = minTileY -1 ; y < maxTileY + 1; y++) {
            windowY = (y == minTileY) ? 0 : windowY + 256;
            for (int x = minTileX - 1; x < maxTileX + 1; x++) {
                windowX = (x == minTileX) ? 0 : windowX + 256;
                try {
                    Image image = tileManager.imageForTileAt(new TileManager.TileId(parameter.getZoom(), x, y));
                    graphics.drawImage(image, windowX, windowY);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private int tilePositionCalculator(double screenPosition) {
        return (int) Math.rint(screenPosition / TILE_SIZE);
    }

    private void changeZoom(){
        LongProperty minScrollTime = new SimpleLongProperty();
        mapPane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);
            if(TileManager.TileId.isValid(parameter.getZoom() + zoomDelta,
                    (int) parameter.getMinX(), (int) parameter.getMinY())){
                parameter.changeZoomLevel(zoomDelta);
            }});
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
