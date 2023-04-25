package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import javafx.scene.Scene;
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

    public BaseMapController(TileManager tileManager, MapParameters parameter) {
        this.tileManager = tileManager;
        this.parameter = parameter;
        canvas = new Canvas();
        mapPane = new Pane(canvas);

        canvas.widthProperty().bind(mapPane.widthProperty());
        canvas.heightProperty().bind(mapPane.heightProperty());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        canvas.widthProperty().addListener(e-> setRedrawNeeded());
        canvas.heightProperty().addListener(e -> setRedrawNeeded());
    }

    private void setRedrawNeeded(){
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
        System.out.println(parameter.getZoom()+"zooooom");


        int windowX = 0;
        int windowY = 0;
        for (int y = minTileY; y < maxTileY; y++) {
            windowY = (y == minTileY) ? 0 : windowY + 256;
            for (int x = minTileX; x < maxTileX; x++) {
                windowX = (x == minTileX) ? 0 : windowX + 256;
//                if (viewPositionX >= 0 || viewPositionX <= Math.scalb(1, (int) (8 + currentZoomLevel)) &&
//                        viewPositionY >= 0 || viewPositionX <= Math.scalb(1, (int) (8 + currentZoomLevel)))
                try {
                    Image image = tileManager.imageForTileAt(new TileManager.TileId(parameter.getZoom(), x, y));
                    graphics.drawImage(image, windowX, windowY);
                    System.out.println(x + "x and " + y + " y");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //}
            }
        }
    }

    private int tilePositionCalculator(double screenPosition) {
        return (int) Math.rint(screenPosition / TILE_SIZE);
    }

}
