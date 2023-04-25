package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
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

    public BaseMapController(TileManager tileManager, MapParameters parameter) {
        this.tileManager = tileManager;
        this.parameter = parameter;
        canvas = new Canvas();
        mapPane = new Pane(canvas);

        addBindings();
        addListeners();
        addEventManagers();

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
    }

    private void addBindings(){
        canvas.widthProperty().bind(mapPane.widthProperty());
        canvas.heightProperty().bind(mapPane.heightProperty());

    }

    private void addListeners(){
        canvas.widthProperty().addListener(e -> setRedrawNeeded());
        canvas.heightProperty().addListener(e -> setRedrawNeeded());
    }

    private void addEventManagers(){
        mapPane.setOnScroll(e -> changeZoom());
        mapPane.setOnMousePressed(e -> {
            clickedXPosition = e.getX();
            clickedYPosition = e.getY();
        });
        mapPane.setOnMouseDragged(e -> {
            parameter.scroll((int) Math.rint((clickedXPosition - e.getX())/2), (int) Math.rint((clickedYPosition - e.getY())/2));
            redrawNeeded = true;
        });
        mapPane.setOnMouseReleased(e -> redrawNeeded = true);
    }

    private void setRedrawNeeded() {
        redrawNeeded = true;
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

        System.out.println(mapPane.getWidth());

        int minTileX = tilePositionCalculator(parameter.getMinX());
        int maxTileX = tilePositionCalculator(parameter.getMinX() + mapPane.getWidth());

        int minTileY = tilePositionCalculator(parameter.getMinY());
        int maxTileY = tilePositionCalculator(parameter.getMinY() + mapPane.getWidth());
        System.out.println(parameter.getZoom() + "zooooom");


        int windowX = 0;
        int windowY = 0;
        for (int y = minTileY; y < maxTileY; y++) {
            windowY = (y == minTileY) ? 0 : windowY + 256;
            for (int x = minTileX; x < maxTileX; x++) {
                windowX = (x == minTileX) ? 0 : windowX + 256;
                try {
                    Image image = tileManager.imageForTileAt(new TileManager.TileId(parameter.getZoom(), x, y));
                    graphics.drawImage(image, windowX, windowY);
                    System.out.println(x + "x and " + y + " y");
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

            parameter.changeZoomLevel(zoomDelta);
            redrawNeeded = true;
        });
    }

}
