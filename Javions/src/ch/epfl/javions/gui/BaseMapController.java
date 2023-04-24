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
    public BaseMapController(TileManager tileManager, MapParameters parameter) {
        this.tileManager = tileManager;
        this.parameter = parameter;
        canvas = new Canvas();
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
    }

    public Pane pane() throws IOException {
        Pane mapPane = new Pane();
        BorderPane root = new BorderPane(mapPane);

        canvas.widthProperty().bind(mapPane.widthProperty());
        canvas.heightProperty().bind(mapPane.heightProperty());

        root.setCenter(mapPane);
        GraphicsContext graphics = canvas.getGraphicsContext2D();

        double currentZoomLevel = parameter.getZoom();
        double viewPositionX = parameter.getMinX();
        double viewPositionY = parameter.getMinY();
        int tileCoordinateX = (int) Math.rint(parameter.getMinX() / 256);
        int tileCoordinateY = (int) Math.rint(parameter.getMinY() / 256);

        if(viewPositionX >= 0 || viewPositionX <= Math.scalb(1,(int) (8 + currentZoomLevel)) &&
            viewPositionY >= 0 || viewPositionX <= Math.scalb(1,(int) (8 + currentZoomLevel))) {
            graphics.drawImage(tileManager.imageForTileAt(
                    new TileManager.TileId(parameter.getZoom(),tileCoordinateX, tileCoordinateY)),
                    0 , 0, canvas.getWidth(), canvas.getHeight());
        }

        mapPane.getChildren().add(canvas);
        return root;
    }

    public void centerOn(GeoPos pos) {

    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;


        // … à faire : dessin de la carte
    }

}
