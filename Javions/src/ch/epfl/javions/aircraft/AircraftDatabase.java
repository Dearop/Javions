package ch.epfl.javions.aircraft;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
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
    private String fileName;
    private String testName;
    private WakeTurbulenceCategory WTCTest;


    /**
     * Stores the specified file name.
     * Throws a NullPointerException if the file name is null.
     * Does not read any data from the file at this point.
     * @param fileName The name of the file containing the aircraft information.
     * @throws NullPointerException If the file name is null.
     */
    public AircraftDatabase(String fileName) {
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
        String aircraft = getClass().getResource("/aircraft.zip").getFile();
        aircraft = URLDecoder.decode(aircraft, UTF_8);
        String stringLineFiltered = "";
        AircraftRegistration registration;
        AircraftTypeDesignator designator;
        String model;
        AircraftDescription description;
        WakeTurbulenceCategory wakeTurbulenceCategory;
        String addressString = String.valueOf(address);
        ArrayList <String[]> lines = new ArrayList<>();

        /**
         * This try/catch gets the zip file that includes all the data that we have about the different aircrafts
         */
        try (ZipFile zipFileUsed = new ZipFile(aircraft);
             InputStream stream = zipFileUsed.getInputStream(zipFileUsed.getEntry("14.csv"));
             Reader reader = new InputStreamReader(stream, UTF_8);
             BufferedReader buffer = new BufferedReader(reader)) {
            while ((stringLineFiltered = buffer.readLine()) != null)
                lines.add(stringLineFiltered.split(",|\\r?\\n"));
        } catch (IOException e) {
            throw new RuntimeException(e + " zipFileError");
        }

        /**
         * This if statement shortens the length of addressString to only the address itself.
         * This is necessary because the valueOf() function that gets initialized when addressString is created,
         * has the following form: "IcaoAddress[string=XXXXXX]"
         */
        if (addressString.length() != 6) {
            addressString = addressString.substring(19,25);
        }

        /**
         * In this for-loop we first search through every ICAO-address until we find the one we are looking for.
         * Once that address gets found we then return the values that are found in the same array in the form of
         * AircraftData.
         */
        for (int i = 0; i < lines.size(); ++i) {
            if (lines.get(i)[0].startsWith(addressString)) {

                registration = new AircraftRegistration(lines.get(i)[1]);
                designator = new AircraftTypeDesignator(lines.get(i)[2]);
                model = lines.get(i)[3];
                testName = model;
                description = new AircraftDescription(lines.get(i)[4]);
                wakeTurbulenceCategory = WakeTurbulenceCategory.of(lines.get(i)[5]);
                WTCTest = wakeTurbulenceCategory;
                return new AircraftData(registration, designator, model, description, wakeTurbulenceCategory);
            }
        }

        return null; // If the address doesn't get found in the ZipFile then null gets returned.
    }

    /**
     * This method was written to test the class
     * @return String created in the get method above that stores the name of the aircraft.
     */
    public String returnModelString() {
        return this.testName;
    }

    /**
     * This method was written to test the class
     * @return WakeTurbulenceCategory created in the get method above.
     */
    public WakeTurbulenceCategory returnWTCValue(){
        return this.WTCTest;
    }
}
