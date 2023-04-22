package ch.epfl.javions.gui;

import ch.epfl.javions.WebMercator;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public final class TileManager {
    private Path path;
    private String serverAddress;
    // not sure if loadFactor is true
    LinkedHashMap<TileId, Image> memoryCache = new LinkedHashMap<>(100, 1, true);
    public TileManager(Path path, String serverAddress){
        this.path = path;
        this.serverAddress = serverAddress;
    }

    public Image imageForTileAt(TileId tileId) throws IOException {
        // search in memory cache if found return
        Image image = memoryCache.get(tileId);
        if(image != null){
            return image;
        } else if(){
            //I think we have to write this in an InputStream so the images are in a file in the ressources, what do you think?

        } else {
            URL u = new URL(
                    "https://tile.openstreetmap.org/"+ tileId.zoom() +"/"+ tileId.x +"/"+ tileId.y +".png");
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "Javions");
            InputStream i = c.getInputStream();
        }
        // else if (found in disk memory) put in memory cache and returned
        // else we get it from the tile server place it in disk memory and place it in memory cache and return
        Image coc = new Image("https://play-lh.googleusercontent.com/LByrur1mTmPeNr0ljI-uAUcct1rzmTve5Esau1SwoAzjBXQUby6uHIfHbF9TAT51mgHm")
        return coc;
    }

    public record TileId(int zoom, int x, int y){
        public static boolean isValid(int zoom, int x, int y){// i am not sure if 19 is the highest value that zoomLevel can have but i tried it on this https://tile.openstreetmap.org/20/0/0.png
            return (x>=0 && y>=0) && (x <= Math.scalb(1, 8+zoom) && (y <= Math.scalb(1, 8 + zoom))) && (0 < zoom && zoom <= 19);
        }
    }
}
