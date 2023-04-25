package ch.epfl.javions.gui;

import ch.epfl.javions.WebMercator;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;

public final class TileManager {
    private final Path path;
    private final String serverAddress;
    private final static int MAX_SIZE = 100;
    private LinkedHashMap<TileId, Image> memoryCache = new LinkedHashMap<>(100, 1, true);
    private Iterator<Image> iterator = this.memoryCache.values().iterator();
    public TileManager(Path path, String serverAddress) {
        this.path = path;
        this.serverAddress = serverAddress;
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

    public Image imageForTileAt(TileId id) throws IOException {
        if(!memoryCache.containsKey(id)){
            if(memoryCache.size() == MAX_SIZE)
                memoryCache.remove(memoryCache.keySet().iterator().next());
        }
        refreshCacheIterator();
        memoryCache.put(id, getFromFilesOrAdd(id));
        return memoryCache.get(id);
    }

    private void refreshCacheIterator(){
        if(!iterator.hasNext()){
            iterator = memoryCache.values().iterator();
        }
    }

    private Image getFromFilesOrAdd(TileId tileId) throws IOException{
        Path imagePath = tildIdPath(tileId);
        if(Files.exists(imagePath)){
            return new Image(new ByteArrayInputStream(Files.readAllBytes(imagePath)));
        }
        URL u = new URL("https://"+ serverAddress + "/" + tileId.zoom + "/" + tileId.x + "/" + tileId.y +".png");
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "Javions");
        InputStream i = c.getInputStream();
        byte[] bytes = i.readAllBytes();
        i.close();
        Files.createDirectories(directoryPath(tileId));
        Files.write(imagePath, bytes);
        return new Image(new ByteArrayInputStream(bytes));
    }

    private Path tildIdPath(TileId id){
        return path.resolve(Paths.get(id.zoom +"/" +id.x+ "/" + id.y + ".png"));
    }

    private Path directoryPath(TileId id){
        return path.resolve(Paths.get(id.zoom +"/" +id.x));
    }
}
