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

/**
 * The TileManager class is responsible for managing the tiles needed for the map visualization.
 * It downloads the tiles from the Internet when needed and stores them in a memory cache for faster retrieval.
 * The tiles are identified by a TileId object, which contains the zoom level and the x,y coordinates of the tile.
 * The maximum zoom level is 19, and the minimum zoom level is 6.
 * The class can retrieve an image for a given TileId object from the memory cache or from the disk if it exists.
 * If the tile is not present in either the memory cache or the disk, it downloads it from the Internet, stores it on disk,
 * and returns it as an Image object.
 * The class uses an LRU (Least Recently Used) memory cache implemented as a LinkedHashMap to store the tile images.
 * When the cache exceeds its maximum size, the least recently used entry is removed from the cache.
 * The class is immutable once initialized and can only be used to retrieve images for a given TileId.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class TileManager {
    private final Path path;
    private final String serverAddress;
    private final static int MEMORY_SIZE = 100;
    private final static float MEMORY_LOAD_FACTOR = 0.75f;

    private LinkedHashMap<TileId, Image> memoryCache;

    /**
     * Constructs a TileManager object with the given path and server address.
     * The path specifies the directory where the tile images are stored on disk.
     * The server address specifies the URL of the tile server.
     *
     * @param path          the path where the tile images are stored on disk.
     * @param serverAddress the URL of the tile server.
     */
    public TileManager(Path path, String serverAddress) {
        this.path = path;
        this.serverAddress = serverAddress;
        this.memoryCache = new LinkedHashMap<>(MEMORY_SIZE, MEMORY_LOAD_FACTOR, true);
    }

    /**
     * A record representing the unique identifier of a tile.
     * It contains the zoom level and the x,y coordinates of the tile.
     * The class provides a static method to check if a TileId object is valid.
     */
    public record TileId(int zoom, int x, int y) {
        private static final int MAX_ZOOM_LEVEL = 19;
        private static final int MIN_ZOOM_LEVEL = 6;
        private static final int MIN_COORDS = 0;

        /**
         * TODO this method is never used!!
         * Checks if a TileId object is valid.
         * A TileId object is considered valid if its zoom level is between MIN_ZOOM_LEVEL and MAX_ZOOM_LEVEL,
         * and its x,y coordinates are between MIN_COORDS and a maximum value that depends on the zoom level.
         * @param id the TileId object to check.
         * @return true if the TileId object is valid, false otherwise.
         */
        public static boolean isValid(TileId id) {
            int max_coords = (int) Math.scalb(1, 8 + id.zoom);
            return (id.x >= MIN_COORDS && id.y >= MIN_COORDS) &&
                    (id.x <= max_coords && id.y <= max_coords) &&
                    (MIN_ZOOM_LEVEL <= id.zoom && id.zoom <= MAX_ZOOM_LEVEL);

        }
    }

    /**
     * Retrieves an image for the given TileId object. If the image is present in the memory cache,
     * it returns the cached image. Otherwise, it looks for the image on disk. If it is present on disk,
     * it loads it into memory, adds it to the cache, and returns it. If it is not present on disk,
     * it downloads it from the Internet, saves it on disk, adds it to the cache, and returns it.
     * If any IO exception is thrown while retrieving the image, it propagates the exception to the caller.
     * @param id the TileId object that identifies the tile image to retrieve.
     * @return the Image object corresponding to the given TileId object.
     * @throws IOException if any IO exception is thrown while retrieving the image.
     */
    public Image imageForTileAt(TileId id) throws IOException {

        // getting the image straight from the memory
        if (memoryCache.containsKey(id)) return memoryCache.get(id);

        // creating the path string
        Path directoryPath = directoryPath(id);
        Path imagePath = directoryPath.resolve(id.y() + ".png");

        if (Files.exists(imagePath)) {
            return getImageFromDisk(id, imagePath);
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

    /**
     * Retrieves an image from the disk for the given TileId object.
     * It reads the image from the file located at imagePath, creates an Image object from it,
     * adds it to the cache, and returns it.
     * @param tileId the TileId object that identifies the tile image to retrieve from disk.
     * @param imagePath the path of the image file to read.
     * @return the Image object corresponding to the given TileId object.
     * @throws IOException if any IO exception is thrown while reading the image file.
     */
    private Image getImageFromDisk(TileId tileId, Path imagePath) throws IOException {

        try (InputStream inputStream = new FileInputStream(imagePath.toFile())) {
            Image image = new Image(inputStream);

            addAndRemoveMemory(tileId, image);
            return image;
        }
    }

    /**
     * Adds the given TileId and Image objects to the memory cache, and removes the least recently used entry
     * if the cache exceeds its maximum size.
     * @param tileId the TileId object to add to the cache.
     * @param image the Image object to add to the cache.
     */
    private void addAndRemoveMemory(TileId tileId, Image image) {
        memoryCache.put(tileId, image);
        if (memoryCache.entrySet().size() > MEMORY_SIZE) {
            Iterator<Map.Entry<TileId, Image>> it = memoryCache.entrySet().iterator();
            it.next();
            it.remove();
        }
    }

    /**
     * @param id the TileId object that identifies the image file to store.
     * @return the path of the directory where the image file for the given TileId object should be stored.
     */
    private Path directoryPath(TileId id) {
        return path.resolve(Paths.get(id.zoom + "/" + id.x));
    }

}
