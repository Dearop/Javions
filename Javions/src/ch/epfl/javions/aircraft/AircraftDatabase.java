package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


import static java.nio.charset.StandardCharsets.UTF_8;

public final class AircraftDatabase {
    private String fileName;

    public AircraftDatabase(String fileName) {
        if (fileName == null) throw new NullPointerException();
        this.fileName = fileName;
    }

    public AircraftData get(IcaoAddress address) throws IOException {
        String aircraft = getClass().getResource("/aircraft.zip").getFile();
        String stringLineFiltered = getStringLineFiltered(aircraft);
        AircraftRegistration registration;
        AircraftTypeDesignator designator;
        String model;
        AircraftDescription description;
        WakeTurbulenceCategory wakeTurbulenceCategory;

        // up to here code works duh
        String addressString = String.valueOf(address);

        if(addressString.length() != 6){
            return null;
        }


        // splits the string into an array for every line and comma
        String[] lines = stringLineFiltered.split(System.getProperty("line.separator"));

        // +6 because there are always 5 commas and one line.separator till the next ICAO
        for (int i = 0; i < lines.length; i = i + 6) {
            if(lines[i].startsWith(addressString)) {

                registration = new AircraftRegistration(lines[i+1]);
                designator = new AircraftTypeDesignator(lines[i+2]);
                model = lines[i+3];
                description = new AircraftDescription(lines[i+4]);
                wakeTurbulenceCategory = WakeTurbulenceCategory.of(lines[i+5]);
                return new AircraftData(registration, designator, model, description, wakeTurbulenceCategory);
            }

        }

        return null; // has to be the address file called by icao;
}

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
