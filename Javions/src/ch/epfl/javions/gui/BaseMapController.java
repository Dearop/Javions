package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapController {
    private TileManager tileManager;
    private MapParameters parameter;
    private boolean redrawNeeded;
    private Canvas canvas;
    private Pane mapPane;
    private static final int TILE_SIZE = 256;

    public BaseMapController(TileManager tileManager, MapParameters parameter){
        this.tileManager = tileManager;
        this.parameter = parameter;
        canvas = new Canvas();
        mapPane = new Pane(canvas);

        canvas.widthProperty().bind(mapPane.widthProperty());
        canvas.heightProperty().bind(mapPane.heightProperty());

        redrawNeeded = true;
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
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

        for (int y = minTileY; y < maxTileY; y ++) {
            for (int x = minTileX; x < maxTileX; x ++) {
                //if (viewPositionX >= 0 || viewPositionX <= Math.scalb(1, (int) (8 + currentZoomLevel)) &&
                        //viewPositionY >= 0 || viewPositionX <= Math.scalb(1, (int) (8 + currentZoomLevel))) {
                    System.out.println("bitch");
                    try{
                        Image image = tileManager.imageForTileAt(new TileManager.TileId(parameter.getZoom(), x, y));
                        graphics.drawImage(image, x * TILE_SIZE, y * TILE_SIZE);
                    }catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                //}
            }
        }
    }

        private int tilePositionCalculator(double screenPosition){
            return (int) Math.rint(screenPosition / TILE_SIZE);
        }

    }
