package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * The AircraftDatabase class represents a database of aircraft information stored in a file.
 * The file is read in a sorted order by ICAO address, and the get() method takes advantage of this
 * to quickly find the desired aircraft information, if it exists.
 * The constructor takes a file name as an argument and stores it in a class attribute.
 * The get() method takes an ICAO address and returns the corresponding aircraft data,
 * or null if no entry exists for that address. It throws an IOException if there is an input/output error.
 *
 * @author Henri Antal (339444)
 * @author Paul Quesnot (347572)
 */
public final class AircraftDatabase {
    private final String fileName;
    public static final String SEPARATOR = ",";
    public static final String FILETYPE = ".csv";
    private ConcurrentHashMap<String, AircraftData> dataHashMap = new ConcurrentHashMap<>();
    /**
     * Stores the specified file name.
     * Throws a NullPointerException if the file name is null.
     * Does not read any data from the file at this point.
     *
     * @param fileName The name of the file containing the aircraft information.
     * @throws NullPointerException If the file name is null.
     */
    public AircraftDatabase(String fileName) {
        this.fileName = Objects.requireNonNull(fileName);
        createMapFromAddressToAircraftData();
    }

    /**
     * Fills the HashMap attribute of the class with AircraftData Instances that are associated
     * to their IcaoAddress. This was done to improve efficiency.
     * Throws an IOException if there is an input/output error.
     *.
     * @throws IOException If there is an input/output error.
     */
    private void createMapFromAddressToAircraftData(){
        try (ZipFile zip = new ZipFile(fileName)) {
            zip.stream().forEach(e -> {
                String entryName = e.getName();
                if (entryName.endsWith(FILETYPE)) {
                    try (InputStream stream = zip.getInputStream(e);
                         Reader reader = new InputStreamReader(stream, UTF_8);
                         BufferedReader buffer = new BufferedReader(reader)) {

                        buffer.lines().map(l -> l.split(SEPARATOR, -1))
                                .forEach(s -> {
                                    try {
                                        AircraftData d = new AircraftData(
                                                new AircraftRegistration(s[1]),
                                                new AircraftTypeDesignator(s[2]),
                                                s[3],
                                                new AircraftDescription(s[4]),
                                                WakeTurbulenceCategory.of(s[5])
                                        );
                                        dataHashMap.put(s[0], d);
                                    } catch (Exception l) {}
                                });
                    } catch (IOException l) {}
                }
            });
        }catch (IOException l){}
    }

    /**
     * Gets AircraftData associated to the IcaoAddress from the Map which is filled out in the method seen above.
     * This method was changed from the original method for efficiency reasons.
     *
     * @param address IcaoAddress of the AircraftData we are trying to fetch from the file.
     * @return AircraftData which is associated to the IcaoAddress of the file.
     */
    public AircraftData get(IcaoAddress address) {
        return dataHashMap.get(address.string());
    }
}