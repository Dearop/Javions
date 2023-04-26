package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public final class BaseMapController {
    private TileManager tileManager;
    private MapParameters parameter;
    private boolean redrawNeeded;
    private Canvas canvas;
    private Pane mapPane;

    private int xshift;
    private int yshift;

    private int windowX = -128;
    private int windowY = -128;

    private static final int TILE_SIZE = 256;
    private double clickedXPosition;
    private double clickedYPosition;

    public BaseMapController(TileManager tileManager, MapParameters parameter) {
        this.tileManager = tileManager;
        this.parameter = parameter;
        canvas = new Canvas();
        mapPane = new Pane(canvas);

        this.xshift = 0;
        this.yshift = 0;

        addBindings();
        addListeners();
        addEventManagers();
    }

    private void addBindings() {
        canvas.widthProperty().bind(mapPane.widthProperty());
        canvas.heightProperty().bind(mapPane.heightProperty());
    }

    private void addListeners() {
        canvas.widthProperty().addListener(e -> redrawOnNextPulse());
        canvas.heightProperty().addListener(e -> redrawOnNextPulse());
        parameter.zoomProperty().addListener(e -> redrawOnNextPulse());
        parameter.minXProperty().addListener(e ->{redrawOnNextPulse();});
        parameter.minYProperty().addListener(e ->{redrawOnNextPulse();});
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
    }

    private void addEventManagers() {
        LongProperty minScrollTime = new SimpleLongProperty();

        mapPane.setOnScroll(e -> {
            int zoomDelta = (int) Math.signum(e.getDeltaY());
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);

            parameter.changeZoomLevel(zoomDelta);
        });

        mapPane.setOnMousePressed(e -> {
            clickedXPosition = e.getX();
            clickedYPosition = e.getY();
        });

        mapPane.setOnMouseDragged(e -> {
            parameter.scroll((int) (clickedXPosition - e.getX()), (int)(clickedYPosition - e.getY()));
            safeTheShift((int) (clickedXPosition - e.getX()), (int) (clickedYPosition - e.getY()));
            clickedXPosition = e.getX();
            clickedYPosition = e.getY();
        });

        mapPane.setOnMouseReleased(e -> {

        });

    }

    public Pane pane() {
        return mapPane;
    }

    public void centerOn(GeoPos pos) {
        double xTopPositon = WebMercator.x(parameter.getZoom(), pos.longitude());
        double yTopPosition = WebMercator.y(parameter.getZoom(), pos.latitude());
        parameter.scroll((int)((xTopPositon + mapPane.getWidth() / 2)),(int) ((yTopPosition + mapPane.getHeight() / 2)));
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext graphics = canvas.getGraphicsContext2D();

        double minTileX = tilePositionCalculator(parameter.getMinX());
        double maxTileX = tilePositionCalculator(parameter.getMinX() + mapPane.getWidth());

        double minTileY = tilePositionCalculator(parameter.getMinY());
        double maxTileY = tilePositionCalculator(parameter.getMinY() + mapPane.getWidth());



        for (double y = minTileY - 1; y < maxTileY + 1; y++) {
            windowY = (y == minTileY - 1) ? -128 : windowY + 256;
            for (double x = minTileX - 1; x < maxTileX + 1; x++) {
                windowX = (x == minTileX-1) ? -128 : windowX + 256;
                try {
                    TileManager.TileId id = new TileManager.TileId(parameter.getZoom(), (int) x, (int) y);
                    if (id.isValid(id)) {
                        Image image = tileManager.imageForTileAt(id);
                        graphics.drawImage(image, windowX - xshift, windowY - yshift);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private double tilePositionCalculator(double screenPosition) {
        return Math.rint(screenPosition / TILE_SIZE);
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
    // TODO the shift grid and the windowtile grid are not on top of eachother yet we need to synchronise them
    private void safeTheShift(int x, int y){
        xshift = (x + xshift + windowX) % 128;
        System.out.println(xshift);
        yshift = (y + yshift + windowY) % 128;
    }

}
