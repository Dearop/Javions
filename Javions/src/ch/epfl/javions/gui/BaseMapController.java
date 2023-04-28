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

public final class BaseMapController {
    private TileManager tileManager;
    private MapParameters parameter;
    private boolean redrawNeeded;
    private Canvas canvas;
    private Pane mapPane;
    private static final int TILE_SIZE = 256;
    private static final int SCROLL_TIME = 200;

    private final ObjectProperty<Point2D> scroller;


    public BaseMapController(TileManager tileManager, MapParameters parameter) {
        this.tileManager = tileManager;
        this.parameter = parameter;
        this.canvas = new Canvas();
        this.mapPane = new Pane(canvas);
        this.scroller = new SimpleObjectProperty<>(new Point2D(0, 0));

        addBindings();
        addListeners();
        addEventManagers();
    }

    private void addBindings() {
        canvas.widthProperty().bind(mapPane.widthProperty());
        canvas.heightProperty().bind(mapPane.heightProperty());
    }

    private void addListeners() {
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

    private void addEventManagers() {
        LongProperty minScrollTime = new SimpleLongProperty();

        mapPane.setOnScroll(e -> {
            double zoomDelta = e.getDeltaY();
            if (zoomDelta == 0) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + SCROLL_TIME);


            parameter.scroll(e.getX(), e.getY());
            parameter.changeZoomLevel((int) zoomDelta);
            parameter.scroll(-e.getX(), -e.getY());

            scroller.set(new Point2D(e.getX(), e.getY()));
        });

        mapPane.setOnMousePressed(e -> {
            scroller.set(new Point2D(e.getX(), e.getY()));
        });

        mapPane.setOnMouseDragged(e -> {

            parameter.scroll((int) (scroller.get().getX() - e.getX()), (int) (scroller.get().getY()) - e.getY());
            scroller.set(new Point2D(e.getX(), e.getY()));
        });

        mapPane.setOnMouseReleased(e -> {
            scroller.set(new Point2D(e.getX(), e.getY()));
        });
    }


    /**
     * this is from a test
     *
     * @return
     */
    public Pane pane() {
        return mapPane;
    }

    public void centerOn(GeoPos pos) {
        double xTopPositon = WebMercator.x(parameter.getZoom(), pos.longitude());
        double yTopPosition = WebMercator.y(parameter.getZoom(), pos.latitude());
        parameter.scroll((int) ((xTopPositon + mapPane.getWidth() / 2)), (int) ((yTopPosition + mapPane.getHeight() / 2)));
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext graphics = canvas.getGraphicsContext2D();

        double minTileX = tilePositionCalculator(parameter.getMinX());
        double maxTileX = tilePositionCalculator(parameter.getMinX() + mapPane.getWidth());

        double minTileY = tilePositionCalculator(parameter.getMinY());
        double maxTileY = tilePositionCalculator(parameter.getMinY() + mapPane.getWidth());

        int destinationY = (int) -parameter.getMinY() % TILE_SIZE;
        for (int y = (int) minTileY; y < (int) maxTileY + 1; y++) {

            int destinationX = (int) -parameter.getMinX() % TILE_SIZE;
        for (int x = (int) minTileX; x < (int) maxTileX + 1; x++) {

                try {
                    graphics.drawImage(tileManager.imageForTileAt(new TileManager.TileId(parameter.getZoom(), x, y))
                            , destinationX, destinationY);
                } catch (IOException ignored) {
                }
                destinationX += TILE_SIZE;
            }
            destinationY += TILE_SIZE;
        }
    }
    private double tilePositionCalculator(double screenPosition) {
        return screenPosition / TILE_SIZE;
    }
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}
