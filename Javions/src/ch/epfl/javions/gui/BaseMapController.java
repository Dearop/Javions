package ch.epfl.javions.gui;

import ch.epfl.javions.GeoPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

public final class BaseMapController {
    private TileManager tileManager;
    private MapParameter parameter;
    private boolean redrawNeeded;
    private Canvas canvas;

    public BaseMapController(TileManager tileManager, MapParameter parameter){
        this.tileManager = tileManager;
        this.parameter = parameter;
        canvas = new Canvas();
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
    }

    public void pane(){
        Pane mapPane = new Pane(canvas);
        canvas.widthProperty().bind(mapPane.widthProperty());
        canvas.heightProperty().bind(mapPane.heightProperty());
        GraphicsContext graphics = canvas.getGraphicsContext2D();

        graphics.drawImage();
    }

    public void centerOn(GeoPos pos){

    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        // … à faire : dessin de la carte
    }
}
