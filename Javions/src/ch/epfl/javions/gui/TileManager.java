package ch.epfl.javions.gui;

import ch.epfl.javions.WebMercator;
import javafx.scene.image.Image;

import java.nio.file.Path;
import java.util.LinkedHashMap;

public final class TileManager {
    private Path path;
    private String serverAddress;
    LinkedHashMap<TileId, Image>
    public TileManager(Path path, String serverAddress){
        this.path = path;
        this.serverAddress = serverAddress;
    }

    public Image imageForTileAt(TileId tileId){

    }

    public record TileId(int zoom, int x, int y){
        public static boolean isValid(int zoom, int x, int y){
            return (x>=0 && y>=0) && (x <= Math.scalb(1, 8+zoom) && y <= Math.scalb(1, 8 + zoom));
        }
    }
}
