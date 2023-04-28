package ch.epfl.javions.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TileManager {
    private final Path path;
    private final String serverAddress;
    private final static int MEMORY_SIZE = 100;
    private final static float MEMORY_LOAD_FACTOR = 0.75f;

    private LinkedHashMap<TileId, Image> memoryCache;

    public TileManager(Path path, String serverAddress) {
        this.path = path;
        this.serverAddress = serverAddress;
        this.memoryCache = new LinkedHashMap<>(MEMORY_SIZE, MEMORY_LOAD_FACTOR, true);
    }

    public record TileId(int zoom, int x, int y) {
        private static final int MAX_ZOOM_LEVEL = 19;
        private static final int MIN_ZOOM_LEVEL = 6;
        private static final int MIN_COORDS = 0;

        public static boolean isValid(TileId id) {// i am not sure if 19 is the highest value that zoomLevel can have but i tried it on this https://tile.openstreetmap.org/20/0/0.png
            int max_coords = (int) Math.scalb(1, 8 + id.zoom);
            return (id.x >= MIN_COORDS && id.y >= MIN_COORDS) &&
                    (id.x <= max_coords && id.y <= max_coords) &&
                    (MIN_ZOOM_LEVEL <= id.zoom && id.zoom <= MAX_ZOOM_LEVEL);

        }
    }

    public Image imageForTileAt(TileId id) throws IOException {

        // getting the image straight from the memory
        if (memoryCache.containsKey(id)) return memoryCache.get(id);

        // creating the path string
        Path directoryPath = directoryPath(id);
        Path imagePath = directoryPath.resolve( id.y() + ".png");

        if (Files.exists(imagePath)) {
            return getImageFromDisk(id, imagePath);
            //return new Image(new ByteArrayInputStream(Files.readAllBytes(imagePath)));
        } else {
            URL u = new URL("https://" + serverAddress + "/" + id.zoom + "/" + id.x + "/" + id.y + ".png");
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "Javions");
            InputStream i = c.getInputStream();
            byte[] bytes = i.readAllBytes();
            i.close();
            Files.createDirectories(directoryPath(id));
            Files.write(imagePath, bytes);
            return new Image(new ByteArrayInputStream(bytes));
        }
    }

    private Image getImageFromDisk(TileId tileId, Path imagePath) throws IOException {

        try (InputStream inputStream = new FileInputStream(imagePath.toFile())) {
            Image image = new Image(inputStream);

            addAndRemoveMemory(tileId, image);
            return image;
        }
    }

    private void addAndRemoveMemory(TileId tileId, Image image) {
        memoryCache.put(tileId, image);
        if (memoryCache.entrySet().size() > MEMORY_SIZE) {
            Iterator<Map.Entry<TileId, Image>> it = memoryCache.entrySet().iterator();
            it.next();
            it.remove();
        }
    }

    private Path directoryPath(TileId id) {
        return path.resolve(Paths.get(id.zoom + "/" + id.x));
    }

}
