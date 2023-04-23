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
        canvas.widthProperty().bind(mapPane.widthProperty());
        canvas.heightProperty().bind(mapPane.heightProperty());

        GraphicsContext graphics = canvas.getGraphicsContext2D();
        Image image = tileManager.imageForTileAt(
                new TileManager.TileId(14,
                        3090,
                        6331));
        System.out.println(image);
        graphics.drawImage(image, 0,0);

        mapPane.getChildren().add(canvas);
        return mapPane;
    }

    public void centerOn(GeoPos pos) {

    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        // … à faire : dessin de la carte
    }

}
