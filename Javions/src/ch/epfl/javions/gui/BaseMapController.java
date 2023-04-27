package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.WebMercator;
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

            parameter.scroll(e.getSceneX() + xshift, e.getSceneY() + yshift);
            parameter.changeZoomLevel(zoomDelta);
            parameter.scroll(-e.getSceneX() - xshift, -e.getSceneY() - yshift);
        });

        mapPane.setOnMousePressed(e -> {
            clickedXPosition = e.getX();
            clickedYPosition = e.getY();
        });

        mapPane.setOnMouseDragged(e -> {
            parameter.scroll((int) (clickedXPosition - e.getX()), (int)(clickedYPosition - e.getY()));

            if((int)parameter.getMinX() % 256 > 128){
                xshift = ((int)parameter.getMinX() % 256) - 128;
            } else {
                xshift = ((int)parameter.getMinX() % 256) + 128;
            }

            if((int)parameter.getMinY() % 256 > 128){
                yshift = ((int)parameter.getMinY() % 256) -128;
            } else {
                yshift = ((int)parameter.getMinY() % 256) +128;
            }

            clickedXPosition = e.getX();
            clickedYPosition = e.getY();
        });

        mapPane.setOnMouseReleased(e -> {});
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



        for (double y = minTileY - 1; y < maxTileY + 2; y++) {
            windowY = (y == minTileY ) ? 0 : windowY + 256;
            for (double x = minTileX - 1; x < maxTileX + 2; x++) {
                windowX = (x == minTileX) ? 0 : windowX + 256;
                try {
                    TileManager.TileId id = new TileManager.TileId(parameter.getZoom(), (int) x, (int) y);

                    if(TileManager.TileId.isValid(id)){
                        Image image = tileManager.imageForTileAt(id);
                        graphics.drawImage(image, windowX - xshift, windowY - yshift);
                    }
                } catch (IOException ignored) {
                    System.out.println("GINGER");
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

}
