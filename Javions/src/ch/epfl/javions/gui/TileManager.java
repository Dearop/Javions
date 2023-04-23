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
    private Path path;
    private String serverAddress;
    // not sure if loadFactor is true
    private LinkedHashMap<TileId, Image> memoryCache = new LinkedHashMap<>(100, 1, true);
    private static final String FILE_SEPERATOR = System.getProperty("file.seperator");
    public TileManager(Path path, String serverAddress) {
        this.path = path;
        this.serverAddress = serverAddress;
    }

    public Image imageForTileAt(TileId tileId) throws IOException {

        // search in memory cache if found return
        Image image = memoryCache.get(tileId);
        if(image != null){
            return image;
            // TODO: 4/22/2023 might need to take off the ".bin"
        } else if(Files.exists(path) &&
                Files.exists(Paths.get(tileId.zoom + FILE_SEPERATOR+tileId.x + FILE_SEPERATOR+tileId.y))){

            //I think we have to write this in an InputStream so the images are in a file in the resources, what do you think?
            InputStream i = new FileInputStream(Paths.get(tileId.zoom 
                    + FILE_SEPERATOR+tileId.x 
                    + FILE_SEPERATOR+tileId.y).toFile());

            image =  new Image(new ByteArrayInputStream(i.readAllBytes()));
            memoryCache.put(tileId, image);

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
            Path directoryPath = path.resolve(Paths.get(tileId.zoom +"/" +tileId.x));
            Path filePath = directoryPath.resolve(String.valueOf(tileId.y));

            if(Files.exists(Paths.get(String.valueOf(tileId.zoom)))){
                if(!Files.exists(directoryPath)) {
                    Files.createDirectory(directoryPath);
                }
            } else {
                Files.createDirectories(directoryPath);
            }

            OutputStream stream = new FileOutputStream(filePath.toFile());
            stream.write(bytes);
        }
        // else if (found in disk memory) put in memory cache and returned
        // else we get it from the tile server place it in disk memory and place it in memory cache and return
        return image;
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
