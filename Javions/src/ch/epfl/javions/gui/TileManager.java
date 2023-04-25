package ch.epfl.javions.gui;

import ch.epfl.javions.WebMercator;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

public final class TileManager {
    private final Path path;
    private final String serverAddress;
    // not sure if loadFactor is true
    private LinkedHashMap<TileId, Image> memoryCache = new LinkedHashMap<>(100, 1, true);
    public TileManager(Path path, String serverAddress) {
        this.path = path;
        this.serverAddress = serverAddress;
    }

    public Image imageForTileAt(TileId tileId) throws IOException {
        Path directoryPath = path.resolve(Paths.get(tileId.zoom +"/" +tileId.x));
        Path filePath = directoryPath.resolve(tileId.y + ".png");

        // search in memory cache if found return
        Image image = memoryCache.get(tileId);
        if(image != null){
            return image;
            // TODO: 4/22/2023 might need to take off the ".bin"
        } else if(Files.exists(filePath)){

            image =  new Image(new ByteArrayInputStream(Files.readAllBytes(filePath)));
            memoryCache.put(tileId, image);
            return image;
        } else {

            //here we go and get the image from the url, then we create an inputStream to read the bytes
            URL u = new URL(
                    "https://"+ serverAddress + "/" + tileId.zoom + "/" + tileId.x + "/" + tileId.y +".png");

            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "Javions");
            InputStream i = c.getInputStream();

            byte[] bytes = i.readAllBytes();
            i.close();

            //adding to Memorycache
            image = new Image(new ByteArrayInputStream(bytes));
            memoryCache.put(tileId, image);

            //adding to disk Memory
            Files.createDirectories(directoryPath);

            Files.write(filePath, bytes);
            return image;
        }
        // else if (found in disk memory) put in memory cache and returned
        // else we get it from the tile server place it in disk memory and place it in memory cache and return
    }

    public record TileId(int zoom, int x, int y){

        public TileId{
            if(!isValid(zoom, x ,y)){
                throw new IllegalArgumentException();
            }
        }
        public static boolean isValid(int zoom, int x, int y){// i am not sure if 19 is the highest value that zoomLevel can have but i tried it on this https://tile.openstreetmap.org/20/0/0.png
            return (x>=0 && y>=0) && (x <= Math.scalb(1, 8+zoom) && (y <= Math.scalb(1, 8 + zoom))) && (0 < zoom && zoom < 20);
        }
    }
}
