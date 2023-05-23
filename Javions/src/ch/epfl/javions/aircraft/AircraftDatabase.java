package ch.epfl.javions.aircraft;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Objects;
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
    /**
     * Stores the specified file name.
     * Throws a NullPointerException if the file name is null.
     * Does not read any data from the file at this point.
     *
     * @param fileName The name of the file containing the aircraft information.
     * @throws NullPointerException If the file name is null.
     */
    public AircraftDatabase(String fileName) {
        Objects.requireNonNull(fileName);
        this.fileName = fileName;
    }

    // TODO: 4/28/2023 this has to be more efficient

    /**
     * Returns the aircraft data for the specified ICAO address.
     * Searches the sorted file for the address and returns the corresponding data.
     * Returns null if no entry exists for that address.
     * Throws an IOException if there is an input/output error.
     *
     * @param address The ICAO address of the aircraft to retrieve data for.
     * @return The aircraft data for the specified address, or null if no entry exists.
     * @throws IOException If there is an input/output error.
     */
    // TODO: 4/28/2023 Ask about
    public AircraftData get(IcaoAddress address) throws IOException {
        String addressString = address.string();

        try (ZipFile zip = new ZipFile(fileName);
             InputStream stream = zip.getInputStream(zip.getEntry(addressString.substring(addressString.length() - 2) + ".csv"));
             Reader reader = new InputStreamReader(stream, UTF_8);
             BufferedReader buffer = new BufferedReader(reader)) {
                String line;
                while ((line = buffer.readLine()) != null) {
                    if (line.startsWith(addressString)) {
                        String[] splitData = line.split(SEPARATOR, -1);
                        return new AircraftData(
                            new AircraftRegistration(splitData[1]),
                            new AircraftTypeDesignator(splitData[2]),
                            splitData[3],
                            new AircraftDescription(splitData[4]),
                            WakeTurbulenceCategory.of(splitData[5])
                        );
                    }
                }
            }
        return null;
    }
}