package ch.epfl.javions.aircraft;

import java.io.*;
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
    private IcaoAddress fileName;
    //TODO delete
    private String modelhehe;

    /**
     * Stores the specified file name.
     * Throws a NullPointerException if the file name is null.
     * Does not read any data from the file at this point.
     * @param fileName The name of the file containing the aircraft information.
     * @throws NullPointerException If the file name is null.
     */
    public AircraftDatabase(IcaoAddress fileName) {
        if (fileName == null) throw new NullPointerException();
        this.fileName = fileName;
    }

    /**
     * Returns the aircraft data for the specified ICAO address.
     * Searches the sorted file for the address and returns the corresponding data.
     * Returns null if no entry exists for that address.
     * Throws an IOException if there is an input/output error.
     * @param address The ICAO address of the aircraft to retrieve data for.
     * @return The aircraft data for the specified address, or null if no entry exists.
     * @throws IOException If there is an input/output error.
     */
    public AircraftData get(IcaoAddress address) throws IOException {
        String aircraft = getClass().getResource("/Javions/Javions/resources/aircraft.zip").getFile();
        String stringLineFiltered = getStringLineFiltered(aircraft);
        AircraftRegistration registration;
        AircraftTypeDesignator designator;
        String model;
        AircraftDescription description;
        WakeTurbulenceCategory wakeTurbulenceCategory;

        // up to here code works duh
        String addressString = String.valueOf(address);

        if (addressString.length() != 6) {
            return null;
        }

        // splits the string into an array for every line and comma
        String[] lines = stringLineFiltered.split(System.getProperty("line.separator" + ","));

        // +6 because there are always 5 commas and one line.separator till the next ICAO
        for (int i = 0; i < lines.length; i = i + 6) {
            if (lines[i].startsWith(addressString)) {

                registration = new AircraftRegistration(lines[i + 1]);
                designator = new AircraftTypeDesignator(lines[i + 2]);
                model = lines[i + 3];

                modelhehe = model;

                description = new AircraftDescription(lines[i + 4]);
                wakeTurbulenceCategory = WakeTurbulenceCategory.of(lines[i + 5]);
                return new AircraftData(registration, designator, model, description, wakeTurbulenceCategory);
            }

        }

        return null; // has to be the address file called by icao;
    }

    public String returnString() {
        return this.modelhehe;
    }


    /**
     * This method takes the name of a zip file containing a CSV file named "14.csv".
     * It then opens the zip file, retrieves the input stream for the CSV file, and reads each line of the file.
     * The lines are printed to the console as they are read, and the last non-null line is returned.
     * If there is an input/output error while reading the file, a RuntimeException is thrown with an error message
     * indicating that there was an error with the zip file.
     * @param aircraft The name of the zip file containing the CSV file.
     * @return The buffered string of the zip file.
     * @throws RuntimeException If there is an input/output error with the zip file.
     */
    private static String getStringLineFiltered(String aircraft) {

        try (ZipFile zipFileUsed = new ZipFile(aircraft);
             InputStream stream = zipFileUsed.getInputStream(zipFileUsed.getEntry("14.csv"));
             Reader reader = new InputStreamReader(stream, UTF_8);
             BufferedReader buffer = new BufferedReader(reader)) {
            String stringLineFiltered = "";
            while ((stringLineFiltered = buffer.readLine()) != null)
                System.out.println(stringLineFiltered);
            return stringLineFiltered;
        } catch (IOException e) {
            throw new RuntimeException(e + " zipFileError");
        }
    }

}
